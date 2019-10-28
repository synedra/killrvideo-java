package com.killrvideo.service.user.dao;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.killrvideo.service.user.dao.UserDseDao;
import com.killrvideo.service.user.dto.User;
import com.killrvideo.service.user.dto.UserCredentials;

/**
 * Implementation of User Operation in {@link UserDseDao} for bootcamp:
 * 
 * KISS:
 * - Using SimpleStatement Only
 * - Avoiding PrepareStatement
 * - Avoiding QueryBuilder to ensure same code as other languages
 * - Avoiding Dao and Mapper with Bean
 *
 * @author DataStax Developer Advocates team.
 * @author DataStax Curriculum team.
 * @author YOU => wanna be a hero right ? Here is your chance
 */
public class UserDseDao {

    /** Connectivity to Cassandra/DSE. */
    private CqlSession cqlSession;
    
    /** Constructor for the class, providing Session */
    public UserDseDao(CqlSession cqlSession) {
        this.cqlSession   = cqlSession;
    }
    
    /**
     * Creates a statement to insert a record into table 'user_credentials'
     * 
     * @param userid
     *      user unique identifier
     * @param email
     *      user email adress (PK)
     * @param password
     *      user encoded password
     * @return
     *      expected statement
     */
    private SimpleStatement createStatementToInsertUserCredentials(UUID userid, String email, String password) {
        return SimpleStatement.builder("TODO: PLACE YOUR CQL HERE")
            .addPositionalValues(userid, email, password)
            .build();
    }
    
    /**
     * Creates a statement to insert a record into table 'users'
     * 
     * @param user
     *      Java object wrapping all expected properties
     * @param password
     *      user encoded password
     * @return
     *      expected statement
     */
    private SimpleStatement createStatementToInsertUser(User user) {
        return SimpleStatement.builder("TODO: PLACE YOUR CQL HERE")
            .addPositionalValues(user.getUserid(),
                user.getFirstname(),
                user.getLastname(), 
                user.getEmail(),
                Instant.now())
            .build();
    }
    
    /**
     * Creates a statement to retrieve a record in table 'user_credentials' based on primary key 'email'
     * 
     * @param email
     *      value for the pk
     * @return
     *      expected statement
     */
    private SimpleStatement createStatementToFindUserCredentials(String email) {
        return SimpleStatement.builder("TODO: PLACE YOUR CQL HERE")
            .addPositionalValues(email).build();
    }
    
    /**
     * Creates a statement search for users based on their unique user identifier (PK)
     * 
     * @param listOfUserIds
     *     enumeration of searched user identifiers
     * @return
     *      expected statement
     */
    private SimpleStatement createStatementToSearchUsers(List<UUID> listOfUserIds) {
        return SimpleStatement.builder("TODO: PLACE YOUR CQL HERE")
            .addPositionalValues(listOfUserIds)
            .build();
    }
    
    /* Execute Synchronously */
    public UserCredentials getUserCredential(String email) {
        ResultSet rs  = cqlSession.execute(createStatementToFindUserCredentials(email));
        Row       row = rs.one(); // Request with Pk ensure unicity
        return mapAsUserCredential(row);                                             
    }
    
    /* Execute ASynchronously */
    public CompletionStage<UserCredentials> getUserCredentialAsync(String email) {
        return cqlSession.executeAsync(createStatementToFindUserCredentials(email))
                         .thenApply(AsyncResultSet::one)
                         .thenApply(this::mapAsUserCredential);
    }
    
   

    /** {@inheritDoc} */
    public CompletionStage<Void> createUserAsync(User user, String hashedPassword) {
        
        CompletionStage<AsyncResultSet> resultInsertCredentials = cqlSession.executeAsync(
                createStatementToInsertUserCredentials(user.getUserid(), user.getEmail(), hashedPassword));
        
        CompletionStage<AsyncResultSet> resultInsertUser = resultInsertCredentials.thenCompose(rs -> {
          if (rs != null && rs.wasApplied()) {
              return cqlSession.executeAsync(createStatementToInsertUser(user));
          }
          return resultInsertCredentials;
        });

        return resultInsertUser.thenAccept(rs -> {
            if (rs != null && !rs.wasApplied()) {
                String errMsg = "Exception creating user because it already exists";
                throw new CompletionException(errMsg, new IllegalArgumentException(errMsg));
            }
        });
    }

    /** {@inheritDoc} */
    public CompletionStage<List<User>> getUserProfilesAsync(List<UUID> userids) {
        return cqlSession.executeAsync(createStatementToSearchUsers(userids))
                .thenApply(AsyncResultSet::currentPage)
                .thenApply(rowList -> StreamSupport
                    .stream(rowList.spliterator(), false)
                    .filter(Objects::nonNull)
                    .map(this::mapAsUser)
                    .collect(Collectors.toList()));
    }
    
    /* Map from Row to expected Bean */
    protected UserCredentials mapAsUserCredential(Row row) {
        return new UserCredentials(
                row.getString("email"),
                row.getString("password"),
                row.getUuid("userid"));
    }
    
    protected User mapAsUser(Row row) {
        return new User(
                row.getUuid("userid"),
                row.getString("firstname"),
                row.getString("lastname"),
                row.getString("email"), 
                Date.from(row.getInstant("created_date")));
    }
    
}