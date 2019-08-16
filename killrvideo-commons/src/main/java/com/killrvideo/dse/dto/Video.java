package com.killrvideo.dse.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

/**
 * Pojo representing DTO for table 'videos'.
 *
 * @author DataStax Developer Advocates team.
 */
@Entity
public class Video extends AbstractVideo {

    /** Serial. */
    private static final long serialVersionUID = 7035802926837646137L;
    
    @PartitionKey
    @CqlName(VIDEOS_COLUMN_VIDEOID)
    private UUID videoid;

    @NotNull
    @CqlName(VIDEOS_COLUMN_USERID)
    private UUID userid;

    @Length(min = 1, message = "description must not be empty")
    @CqlName(VIDEOS_COLUMN_DESCRIPTION)
    private String description;

    @Length(min = 1, message = "location must not be empty")
    @CqlName(VIDEOS_COLUMN_LOCATION)
    private String location;

    @CqlName(VIDEOS_COLUMN_LOCATIONTYPE)
    private int locationType;

    @CqlName(VIDEOS_COLUMN_TAGS)
    private Set<String> tags = new HashSet<>();

    @NotNull
    @CqlName(VIDEOS_COLUMN_ADDED_DATE)
    private Date addedDate;

    /**
     * Default Constructor allowing reflection.
     */
    public Video() {}
    
    /**
     * Default Constructor allowing reflection.
     */
    public Video(String title) {
        this.name = title;
    }
    
    /**
     * Constructor wihout location nor preview.
     */
    public Video(UUID videoid, UUID userid, String name, String description, int locationType, Set<String> tags, Date addedDate) {
        this(videoid, userid, name, description, null, locationType, null, tags, addedDate);
    }

    /**
     * All attributes constructor.
     */
    public Video(UUID videoid, UUID userid, String name, String description, String location, int locationType, String previewImageLocation, Set<String> tags, Date addedDate) {
        super(name, previewImageLocation);
        this.videoid = videoid;
        this.userid = userid;
        this.description = description;
        this.location = location;
        this.locationType = locationType;
        this.tags = tags;
        this.addedDate = addedDate;
    }

    /**
     * Getter for attribute 'videoid'.
     *
     * @return
     *       current value of 'videoid'
     */
    public UUID getVideoid() {
        return videoid;
    }

    /**
     * Setter for attribute 'videoid'.
     * @param videoid
     * 		new value for 'videoid '
     */
    public void setVideoid(UUID videoid) {
        this.videoid = videoid;
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
     * Getter for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for attribute 'description'.
     * @param description
     * 		new value for 'description '
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for attribute 'location'.
     *
     * @return
     *       current value of 'location'
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter for attribute 'location'.
     * @param location
     * 		new value for 'location '
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter for attribute 'locationType'.
     *
     * @return
     *       current value of 'locationType'
     */
    public int getLocationType() {
        return locationType;
    }

    /**
     * Setter for attribute 'locationType'.
     * @param locationType
     * 		new value for 'locationType '
     */
    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    /**
     * Getter for attribute 'tags'.
     *
     * @return
     *       current value of 'tags'
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Setter for attribute 'tags'.
     * @param tags
     * 		new value for 'tags '
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * Getter for attribute 'addedDate'.
     *
     * @return
     *       current value of 'addedDate'
     */
    public Date getAddedDate() {
        return addedDate;
    }

    /**
     * Setter for attribute 'addedDate'.
     * @param addedDate
     * 		new value for 'addedDate '
     */
    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
    
    
}
