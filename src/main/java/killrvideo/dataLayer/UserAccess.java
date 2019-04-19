package killrvideo.dataLayer;

import java.io.File;
import java.util.UUID;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import killrvideo.entity.User;

public class UserAccess {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserAccess.class);

  private DseSession session;
  
  public UserAccess(DseSession dseSession) {
    session = dseSession;
  }

  public boolean createNewUser(String password, User user) throws Exception {
    LOGGER.debug("------Start createNewUser------");

    SimpleStatement insertCredentialsCommand = SimpleStatement.newInstance(
      "INSERT INTO killrvideo.user_credentials (email, password, userid) VALUES " + 
      "(?, ?, ?) IF NOT EXISTS", user.getEmail(), password, user.getUserid());

    ResultSet meResultSet = session.execute(insertCredentialsCommand);
            
    boolean wasApplied = meResultSet.wasApplied();
       
    if(wasApplied)
    {         
      // Build up a second command containing another insert into the users
      // table. Be sure to include all the data for all the columns.
      SimpleStatement insertUserCommand = SimpleStatement.newInstance(
        "INSERT INTO killrvideo.users (userid, created_date, email, firstname, lastname) " +
        "VALUES (?, ?, ?, ?, ?)", user.getUserid(), Instant.now(),
        user.getEmail(), user.getFirstname(), user.getLastname());
             
      // Execute() your command.
      session.execute(insertUserCommand);
    }

    return true;
  }

  public UUID getAuthenticatedIdByEmailPassword(String email, String password) throws Exception {
    LOGGER.debug("------Start getAuthenticatedIdByEmailPassword------");
    
	// Create a string to SELECT the user from user_credentials based on email
	// TBD: Create the SELECT command string that selects from user_credentials:
	String command = "SELECT * FROM killrvideo.user_credentials WHERE email = ?";
    // Create the SimpleStatement that combines the command with the email:
    SimpleStatement statement = SimpleStatement.newInstance(command, email);
	// Execute the query string and get the result set
	ResultSet meResultSet = session.execute(statement);
	// Get the row from the result set
	Row meRow = meResultSet.one();
	// Create a UUID for the returned user ID and initialize it to null
	UUID userId = null;
	// If the row exists,
	if (meRow != null) {
		// Get the password value from the row
		String passwordFromDB = meRow.getString("password");
		// If the password value from the row equals password parameter,
		if (password.equals(passwordFromDB)) {
			// Set the returned user ID to the row’s user ID
			userId = meRow.getUuid("userid");
		}
	}
	// Return the user ID
	return userId;
  }
  
  public User getUserById(UUID userid) throws Exception {
    LOGGER.debug("------Start getUserById------");
    // Create a string to SELECT the user from users based on userid
	String command = "SELECT * FROM killrvideo.users WHERE userid = ?";
    // Create a string to SELECT the user from users based on parameterized userid
    SimpleStatement statement = SimpleStatement.newInstance(command, userid);
    // Execute the query string and get the result set
    ResultSet meResultSet = session.execute(statement);
    // Get the row from the result set
    Row meRow = meResultSet.one();
    	
    // Extract all the values from the row and create a User object
    String firstname = meRow.getString("firstname");
    String lastname = meRow.getString("lastname");
    String email = meRow.getString("email");
    Instant createdAt = meRow.getInstant("created_date");
    User user = new User(userid, firstname, lastname, email, createdAt);

    // Return the User object    	
    return user;
  }
}