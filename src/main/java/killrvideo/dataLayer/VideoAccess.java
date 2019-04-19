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
    
  public void addNewVideo(Video video) throws Exception {
    LOGGER.debug("------Start add new video------");

  }

  public Video getVideo(UUID videoId) throws Exception {
    LOGGER.debug("------Start get video------");
    return null;  
  }

  public List<LatestVideos> getLatestVideos() throws Exception {
    LOGGER.debug("------Start get latest videos------");
    return new ArrayList<LatestVideos>();
  } 
}
