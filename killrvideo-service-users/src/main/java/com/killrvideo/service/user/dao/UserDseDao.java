package com.killrvideo.service.user.dao;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.killrvideo.service.user.dto.User;
import com.killrvideo.service.user.dto.UserCredentials;

/**
 * Definition of operations for Microservice USER
 *
 * @author DataStax Developer Advocates team.
 */
@Dao
public interface UserDseDao {

    /**
     * As the email is here PRIMARY KEY we can use convention over code.
     *
     * @param email
     *      user email
     * @return
     *      expect Code
     */
    @Select
    CompletionStage< UserCredentials > getUserCredentialAsync(String email);
    
    /**
     * Create user Asynchronously composing things. (with Mappers)
     * 
     * @param user
     *      user Management
     * @param hashedPassword
     *      hashed Password
     * @return
     */
    @QueryProvider(
            providerClass = UserDseDaoQueryProvider.class, 
            entityHelpers = { User.class, UserCredentials.class })
    CompletionStage<Void> createUserAsync(User user, String hashedPassword);
    
    /**
     * Search users based on user id.
     * 
     * @param userids
     *      list of user ids
     * @return
     *      list of user beans
     */
    @QueryProvider(
            providerClass = UserDseDaoQueryProvider.class, 
            entityHelpers = { User.class, UserCredentials.class })
    CompletionStage < List < User > > getUserProfilesAsync(List < UUID > userids);
    
}
