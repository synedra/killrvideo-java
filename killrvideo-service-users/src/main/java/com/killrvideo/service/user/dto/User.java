package com.killrvideo.service.user.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.killrvideo.dse.dao.DseSchema;

/**
 * Pojo representing DTO for table 'users'.
 *
 * @author DataStax Developer Advocates team.
 */
@Entity
@CqlName(DseSchema.TABLENAME_USERS)
public class User implements DseSchema, Serializable {

    /** Serial. */
    private static final long serialVersionUID = 1916681315036907552L;
    
    @PartitionKey
    @CqlName(USER_COLUMN_USERID)
    private UUID userid;

    @Length(min = 1, message = "firstName must not be empty")
    @CqlName(USER_COLUMN_FIRSTNAME)
    private String firstname;

    @Length(min = 1, message = "lastname must not be empty")
    @CqlName(USER_COLUMN_LASTNAME)
    private String lastname;

    @Length(min = 1, message = "email must not be empty")
    @CqlName(USER_COLUMN_EMAIL)
    private String email;

    @NotNull
    @CqlName(USER_COLUMN_CREATE)
    private Date createdAt;

    /**
     * Default constructor (reflection)
     */
    public User() {}

    /**
     * Constructor with all parameters.
     */
    public User(UUID userid, String firstname, String lastname, String email, Date createdAt) {
        this.userid = userid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.createdAt = createdAt;
    }

    /**
     * Getter for attribute 'userid'.
     *
     * @return
     *       current value of 'userid'
     */
    public UUID getUserid() {
        return userid;
    }

    /**
     * Setter for attribute 'userid'.
     * @param userid
     * 		new value for 'userid '
     */
    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    /**
     * Getter for attribute 'firstname'.
     *
     * @return
     *       current value of 'firstname'
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Setter for attribute 'firstname'.
     * @param firstname
     * 		new value for 'firstname '
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Getter for attribute 'lastname'.
     *
     * @return
     *       current value of 'lastname'
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Setter for attribute 'lastname'.
     * @param lastname
     * 		new value for 'lastname '
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Getter for attribute 'email'.
     *
     * @return
     *       current value of 'email'
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for attribute 'email'.
     * @param email
     * 		new value for 'email '
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for attribute 'createdAt'.
     *
     * @return
     *       current value of 'createdAt'
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter for attribute 'createdAt'.
     * @param createdAt
     * 		new value for 'createdAt '
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
