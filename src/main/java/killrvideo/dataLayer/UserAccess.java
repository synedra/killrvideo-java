package killrvideo.dataLayer;

import java.io.File;
import java.util.UUID;
import java.time.Instant;

import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import killrvideo.entity.User;

public class UserAccess {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserAccess.class);

  public boolean createNewUser(String password, User user) throws Exception {
    LOGGER.debug("------Start createNewUser------");
    return false;
  }

  public UUID getAuthenticatedIdByEmailPassword(String email, String password) throws Exception {
    LOGGER.debug("------Start getAuthenticatedIdByEmailPassword------");
    return null;
  }

  public User getUserById(UUID userid) throws Exception {
    LOGGER.debug("------Start getUserById------");
    return null;
  }
}
