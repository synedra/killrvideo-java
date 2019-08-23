package com.killrvideo.service.user.dao;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;
import static com.killrvideo.dse.utils.DseUtils.bind;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.killrvideo.dse.dao.DseSchema;
import com.killrvideo.service.user.dto.User;
import com.killrvideo.service.user.dto.UserCredentials;

/**
 * Implementations of operations in user services.
 * 
 * @author DataStax Developer Advocates team.
 */
public class UserDseDaoQueryProvider implements DseSchema {
    
    private final CqlSession dseSession;
    
    private final EntityHelper<User>            entityHelperUser;
    private final EntityHelper<UserCredentials> entityHelperUserCredentials;
    
    private PreparedStatement psInsertUserCredentialsLwt;
    private PreparedStatement psInsertUserLwt;
    private PreparedStatement psSelectUsersIn;
    
    /**
     * Constructor invoked by the DataStax driver based on Annotation {@link QueryProvider} 
     * set on class {@link UserDseDao}.
     * 
     * @param context
     *      context to extrat dse session
     * @param helperUser
     *      entity helper to interact with bean {@link User}
     * @param helperUserCredentials
     *      entity helper to interact with bean {@link UserCredentials}
     */
    public UserDseDaoQueryProvider(MapperContext context, 
            EntityHelper<User> helperUser, EntityHelper<UserCredentials> helperUserCredentials) {
        this.dseSession                  = context.getSession();
        this.entityHelperUser            = helperUser;
        this.entityHelperUserCredentials = helperUserCredentials;
        this.psInsertUserCredentialsLwt  = dseSession.prepare(helperUserCredentials.insert().ifNotExists().build());
        this.psInsertUserLwt             = dseSession.prepare(helperUser.insert().ifNotExists().build());
        this.psSelectUsersIn             = dseSession.prepare(
                selectFrom(TABLENAME_USERS).all()
                         .where(column(COMMENTS_COLUMN_USERID).in(bindMarker(COMMENTS_COLUMN_USERID)))
                         .build());
    }
    
    /** Javadoc in {@link UserDseDao} */
    public CompletionStage<Void> createUserAsync(User user, String hashedPassword) {
        String errMsg = String.format("Exception creating user because it already exists with email %s", user.getEmail());
        UserCredentials newRecord = new UserCredentials(user.getEmail(), hashedPassword,user.getUserid());
        CompletionStage<AsyncResultSet> cs1 = 
                dseSession.executeAsync(bind(psInsertUserCredentialsLwt, newRecord, entityHelperUserCredentials));
        return cs1
           .thenCompose(rs -> rs.wasApplied() ?
                dseSession.executeAsync(bind(psInsertUserLwt, user, entityHelperUser)) : cs1)
           .thenAccept(rs -> { 
                if (!rs.wasApplied()) {
                    throw new CompletionException(errMsg, new IllegalArgumentException(errMsg));
                }
        });
    }
   
    /** Javadoc in {@link UserDseDao} */
    public CompletionStage<List<User>> getUserProfilesAsync(List<UUID> userids) {
        return dseSession.executeAsync(psSelectUsersIn.bind().setList(0, userids, UUID.class))
                         .thenApply(ars -> StreamSupport.stream(ars.currentPage().spliterator(), false)
                                                        .filter(Objects::nonNull)
                                                        .map(entityHelperUser::get)
                                                        .collect(Collectors.toList()));
    }    

    /**
     * Cassandra and DSE Session are stateless. For each request a coordinator is chosen
     * and execute query against the cluster. The Driver is stateful, it has to maintain some
     * network connections pool, here we properly cleanup things.
     * 
     * @throws Exception
     *      error on cleanup.
     */
    @Override
    protected void finalize() throws Throwable {
        if (dseSession  != null && !dseSession.isClosed()) {
            dseSession.close();
        }
    }
    
}
