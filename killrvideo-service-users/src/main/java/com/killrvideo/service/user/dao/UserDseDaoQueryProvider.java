package com.killrvideo.service.user.dao;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.killrvideo.dse.dao.DseSchema;
import com.killrvideo.service.user.dto.User;
import com.killrvideo.service.user.dto.UserCredentials;

public class UserDseDaoQueryProvider implements DseSchema {

    /** Loger for that class. */
    private static Logger LOGGER = LoggerFactory.getLogger(UserDseDao.class);
    
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
     * @param helperVideo
     *      entity helper to interact with bean {@link UserCredentials}
     */
    public UserDseDaoQueryProvider(MapperContext context,
            EntityHelper<User> helperUser, 
            EntityHelper<UserCredentials> helperCredentials) {
        this.dseSession                  = context.getSession();
        this.entityHelperUser            = helperUser;
        this.entityHelperUserCredentials = helperCredentials;
        
        this.psInsertUserCredentialsLwt = dseSession.prepare(
                insertInto(TABLENAME_USER_CREDENTIALS)
                 .value(USERCREDENTIAL_COLUMN_USERID_,   bindMarker(USERCREDENTIAL_COLUMN_USERID_))
                 .value(USERCREDENTIAL_COLUMN_EMAIL_,    bindMarker(USERCREDENTIAL_COLUMN_EMAIL_))
                 .value(USERCREDENTIAL_COLUMN_PASSWORD_, bindMarker(USERCREDENTIAL_COLUMN_PASSWORD_))
                 .ifNotExists().build());
        
        this.psInsertUserLwt = dseSession.prepare(
                insertInto(TABLENAME_USERS)
                 .value(USER_COLUMN_USERID_,    bindMarker(USER_COLUMN_USERID_))
                 .value(USER_COLUMN_FIRSTNAME_, bindMarker(USER_COLUMN_FIRSTNAME_))
                 .value(USER_COLUMN_LASTNAME_,  bindMarker(USER_COLUMN_LASTNAME_))
                 .value(USER_COLUMN_EMAIL_,     bindMarker(USER_COLUMN_EMAIL_))
                 .value(USER_COLUMN_CREATE_,    bindMarker(USER_COLUMN_CREATE_))
                 .ifNotExists().build());
        
        this.psSelectUsersIn = dseSession.prepare(
                selectFrom(TABLENAME_USERS)
                 .all()
                 .where(column(COMMENTS_COLUMN_USERID).in(bindMarker(COMMENTS_COLUMN_USERID)))
                 .build());
    }
    
    /** Javadoc in {@link UserDseDao} */
    public CompletionStage<Void> createUserAsync(User user, String hashedPassword) {
        String errMsg = String.format("Exception creating user because it already exists with email %s", user.getEmail());
        
        CompletionStage<AsyncResultSet> cs1 = dseSession.executeAsync(psInsertUserCredentialsLwt.bind()
                .setString(USERCREDENTIAL_COLUMN_EMAIL_, user.getEmail())
                .setString(USERCREDENTIAL_COLUMN_PASSWORD_, hashedPassword)
                .setUuid(USERCREDENTIAL_COLUMN_USERID_, user.getUserid()));
        
        CompletionStage<AsyncResultSet> cs2 = cs1.thenCompose(rs -> {
            if (rs != null && rs.wasApplied()) {
                final BoundStatement insertUserQuery = psInsertUserLwt.bind()
                                .setUuid(USER_COLUMN_USERID_,      user.getUserid())
                                .setString(USER_COLUMN_FIRSTNAME_, user.getFirstname())
                                .setString(USER_COLUMN_LASTNAME_,  user.getLastname())
                                .setString(USER_COLUMN_EMAIL_,     user.getEmail())
                                .setInstant(USER_COLUMN_CREATE_,   Instant.now());
                    return dseSession.executeAsync(insertUserQuery);
            }
            return cs1;
        });

        /**
         * ThenAccept in the same thread pool (not using thenAcceptAsync())
         */
        return cs2.thenAccept(rs -> {
            if (rs != null && !rs.wasApplied()) {
                LOGGER.error(errMsg);
                throw new CompletionException(errMsg, new IllegalArgumentException(errMsg));
            }
        });
    }
   
    /** Javadoc in {@link UserDseDao} */
    public CompletionStage<List<User>> getUserProfilesAsync(List<UUID> userids) {
        return null;
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
