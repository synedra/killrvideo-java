package killrvideo.entity;

import java.util.UUID;

/**
 * Pojo representing DTO for table 'video_recommendations'.
 *
 * @author DataStax evangelist team.
 */
public class VideoRecommendations extends AbstractVideoList {

    /** Serial. */
    private static final long serialVersionUID = -1811715623098399219L;

    private UUID userid;

    private float rating;

    private UUID authorid;

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
     * Getter for attribute 'rating'.
     *
     * @return
     *       current value of 'rating'
     */
    public float getRating() {
        return rating;
    }

    /**
     * Setter for attribute 'rating'.
     * @param rating
     * 		new value for 'rating '
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * Getter for attribute 'authorid'.
     *
     * @return
     *       current value of 'authorid'
     */
    public UUID getAuthorid() {
        return authorid;
    }

    /**
     * Setter for attribute 'authorid'.
     * @param authorid
     * 		new value for 'authorid '
     */
    public void setAuthorid(UUID authorid) {
        this.authorid = authorid;
    }
    
}
