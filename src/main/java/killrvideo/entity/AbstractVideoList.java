package killrvideo.entity;

import java.util.Date;
import java.util.UUID;

/**
 * Pojo representing multiple videso.
 *
 * @author DataStax evangelist team.
 */
public class AbstractVideoList extends AbstractVideo {

    /** Serial. */
    private static final long serialVersionUID = 1319627901957309436L;

    private Date addedDate;

    private UUID videoid;
    
    /**
     * Allow default initializations.
     */
    protected AbstractVideoList() {}

    /**
     * Constructor used by sub entities.
     */
    protected AbstractVideoList(String name, String preview, Date addedDate, UUID videoid) {
        super(name, preview);
        this.addedDate = addedDate;
        this.videoid   = videoid;
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
     *      new value for 'addedDate '
     */
    public void setAddedDate(Date addedDate) {
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
     *      new value for 'videoid '
     */
    public void setVideoid(UUID videoid) {
        this.videoid = videoid;
    }
    
}
