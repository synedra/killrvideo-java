package com.killrvideo.conf;

import com.datastax.oss.driver.api.core.config.DriverOption;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implementing custom properties and profile.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum KillrvideoDriverOption implements DriverOption {
    
    SEARCH_CONSISTENCY("profiles."+ DseDriverConfiguration.EXECUTION_PROFILE_SEARCH + ".basic.request.consistency"),
    SEARCH_TIMEOUT("profiles."+     DseDriverConfiguration.EXECUTION_PROFILE_SEARCH + ".basic.request.timeout"),
    
    GRAPH_NAME("basic.graph.name"),
    GRAPH_TIMEOUT("basic.graph.timeout"),
    
    ;
    
    private final String path;

    KillrvideoDriverOption(String path) {
      this.path = path;
    }

    @NonNull
    @Override
    public String getPath() {
      return path;
    }
  }