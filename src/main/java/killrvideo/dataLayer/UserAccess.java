package killrvideo.dataLayer;

import java.io.File;
import java.util.UUID;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

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
