package killrvideo.dataLayer;

import java.io.File;
import java.time.Instant;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilderDsl;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import killrvideo.entity.Video;

public class VideoAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccess.class);
    
    public void addNewVideo(Video video) throws Exception {
        LOGGER.debug("------Start createNewUser------");

    }
}
