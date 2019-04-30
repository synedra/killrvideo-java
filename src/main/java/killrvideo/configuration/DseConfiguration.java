package killrvideo.configuration;

import java.io.*;

import com.datastax.dse.driver.api.core.DseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Configuration
public class DseConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DseConfiguration.class);
    
  @Bean
  public DseSession initializeDSE() {
    DseSession session = null;
    try
    { 
      session = DseSession.builder().forClusterConfig(
        "/projects/creds/config.yaml").build();
    }
    catch(Throwable e)
    {
      e.printStackTrace();
      LOGGER.debug("Error: " + e);
    }
    return session;                   
  }
  
  @Inject
  DseSession dseSession;

  @PreDestroy
  public void destroy() {
    if(dseSession != null)
      dseSession.close();
    dseSession = null;
  }

  private void exitOnError(String errorString, Integer status, Exception e) {
    LOGGER.error(errorString, e);
    System.exit(status);
  }
}
