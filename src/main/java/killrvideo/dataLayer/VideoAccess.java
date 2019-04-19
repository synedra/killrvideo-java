package killrvideo.dataLayer;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import killrvideo.entity.LatestVideos;
import killrvideo.entity.Video;

public class VideoAccess {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAccess.class);

  private DseSession session;
    
  public VideoAccess(DseSession dseSession) {
    session = dseSession;
  }
    
  public void addNewVideo(Video video) throws Exception {
    LOGGER.debug("------Start add new video------");

    // Create the SimpleStatementBuilder with bind 
    // markers for every column in the Videos table
    SimpleStatementBuilder insertVideoBuilder = 
      QueryBuilder.insertInto("killrvideo", "videos")
        .value("videoId", QueryBuilder.bindMarker())
        .value("userId", QueryBuilder.bindMarker())
        .value("name", QueryBuilder.bindMarker())
        .value("description", QueryBuilder.bindMarker())
        .value("location", QueryBuilder.bindMarker())
        .value("location_type", QueryBuilder.bindMarker())
        .value("preview_image_location", QueryBuilder.bindMarker())
        .value("tags", QueryBuilder.bindMarker())
        .value("added_date", QueryBuilder.bindMarker())
        .builder();
                       
    SimpleStatementBuilder insertLatestVideoBuilder = 
      QueryBuilder.insertInto("killrvideo", "latest_videos")
        .value("bucket", QueryBuilder.bindMarker())
        .value("added_date", QueryBuilder.bindMarker())
        .value("videoid", QueryBuilder.bindMarker())
        .value("name", QueryBuilder.bindMarker())
        .value("preview_image_location", QueryBuilder.bindMarker())
        .value("userid", QueryBuilder.bindMarker())
        .builder();

    // Create a SimpleStatement from your builder adding
	// values from the video instant above
    SimpleStatement insertVideoStatement = insertVideoBuilder.build();
    SimpleStatement insertLatestVideoStatement = insertLatestVideoBuilder.build();

    PreparedStatement insertVideoStatementPrepared = 
      session.prepare(insertVideoStatement);
    PreparedStatement insertLatestVideoStatementPrepared =
      session.prepare(insertLatestVideoStatement);
        
    Instant now = Instant.now();

    BoundStatement insertVideoStatementBound = insertVideoStatementPrepared.bind()
      .setUuid("videoid", video.getVideoid())
      .setUuid("userid", video.getUserid()) 
      .setString("name", video.getName())
      .setString("description", video.getDescription())
      .setString("location", video.getLocation())
      .setInt("location_type", 0)
      .setString("preview_image_location", video.getPreviewImageLocation())
      .setInstant("added_date", now)
      .setSet("tags", video.getTags(), String.class);
               
     BoundStatement insertLatestVideoStatementBound = insertLatestVideoStatementPrepared.bind()
       .setInt("bucket", 0)
       .setInstant("added_date", now)
       .setUuid("videoid", video.getVideoid())
       .setString("name", video.getName())
       .setString("preview_image_location", video.getPreviewImageLocation())
       .setUuid("userid", video.getUserid());      

    //----------------------------------------------------------------
    // Build a BatchStatement:
    //----------------------------------------------------------------
    BatchStatement batch = BatchStatement.newInstance(DefaultBatchType.LOGGED,
            insertVideoStatementBound, insertLatestVideoStatementBound);
                
    //----------------------------------------------------------------
    // Execute the statement here:
    //----------------------------------------------------------------
    session.execute(batch);
  }

  public Video getVideo(UUID videoId) throws Exception {
    LOGGER.debug("------Start get video------");
        
    SimpleStatement getVideo = SimpleStatement.newInstance(
      "SELECT * FROM VIDEOS WHERE videoid = ?", videoId);
    
    Row row = session.execute(getVideo).one();
    
    Video video = new Video(
      row.getUuid("videoid"),
      row.getUuid("userid"),
      row.getString("name"),
      row.getString("description"),
      row.getString("location"),
      row.getInt("location_type"),
      row.getString("preview_image_location"),
      row.getSet("tags", String.class),
      row.getInstant("added_date")); 

    return video;     
  }

  public List<LatestVideos> getLatestVideos() throws Exception {
    LOGGER.debug("------Start get latest videos------");
    
    SimpleStatement getLatestVideos = SimpleStatement.newInstance(
      "SELECT * FROM killrvideo.latest_videos WHERE bucket = 0 LIMIT 5");

    ResultSet results = session.execute(getLatestVideos);
    
    List<LatestVideos> ret = new ArrayList<LatestVideos>();
    
    for(Row row : results) {
      LatestVideos latestVideo = new LatestVideos(
        row.getUuid("userid"),
        row.getUuid("videoid"),
        row.getString("name"),
        row.getString("preview_image_location"),
        row.getInstant("added_date")
      );
      ret.add(latestVideo);
    }
    
    return ret;
  } 
}
