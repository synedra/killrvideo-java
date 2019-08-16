package com.killrvideo.dse.dao;

import java.text.SimpleDateFormat;

import com.datastax.oss.driver.api.core.CqlIdentifier;

/**
 * Information related to SCHEMA : use to 'decorate' POJO in Mapper, then prepareStatements.
 *
 * @author DataStax Developer Advocates team.
 */
public interface DseSchema {
    
    SimpleDateFormat FORMATTER_DAY = new SimpleDateFormat("yyyyMMdd");
    
    // user_credentials
    String USERCREDENTIAL_COLUMN_USERID            ="userid" ;
    String USERCREDENTIAL_COLUMN_PASSWORD          = "\"password\"";
    String USERCREDENTIAL_COLUMN_EMAIL             = "email";
    
    CqlIdentifier TABLENAME_USER_CREDENTIALS       = CqlIdentifier.fromCql("user_credentials");
    CqlIdentifier USERCREDENTIAL_COLUMN_USERID_    = CqlIdentifier.fromCql(USERCREDENTIAL_COLUMN_USERID);
    CqlIdentifier USERCREDENTIAL_COLUMN_PASSWORD_  = CqlIdentifier.fromCql(USERCREDENTIAL_COLUMN_PASSWORD);
    CqlIdentifier USERCREDENTIAL_COLUMN_EMAIL_     = CqlIdentifier.fromCql(USERCREDENTIAL_COLUMN_EMAIL);
    
    // users
    String USER_COLUMN_USERID                 = "userid";
    String USER_COLUMN_FIRSTNAME              = "firstname";
    String USER_COLUMN_LASTNAME               = "lastname";
    String USER_COLUMN_EMAIL                  = "email";
    String USER_COLUMN_CREATE                 = "created_date";
    
    CqlIdentifier TABLENAME_USERS             = CqlIdentifier.fromCql("users");
    CqlIdentifier USER_COLUMN_USERID_         = CqlIdentifier.fromCql(USER_COLUMN_USERID);
    CqlIdentifier USER_COLUMN_FIRSTNAME_      = CqlIdentifier.fromCql(USER_COLUMN_FIRSTNAME);
    CqlIdentifier USER_COLUMN_LASTNAME_       = CqlIdentifier.fromCql(USER_COLUMN_LASTNAME);
    CqlIdentifier USER_COLUMN_EMAIL_          = CqlIdentifier.fromCql(USER_COLUMN_EMAIL);
    CqlIdentifier USER_COLUMN_CREATE_         = CqlIdentifier.fromCql(USER_COLUMN_CREATE);
    
    // videos
    CqlIdentifier TABLENAME_VIDEOS            = CqlIdentifier.fromCql("videos");
    
    String VIDEOS_COLUMN_VIDEOID              = "videoid";
    String VIDEOS_COLUMN_USERID               = "userid";
    String VIDEOS_COLUMN_NAME                 = "name";
    String VIDEOS_COLUMN_DESCRIPTION          = "description";
    String VIDEOS_COLUMN_LOCATION             = "location";
    String VIDEOS_COLUMN_LOCATIONTYPE         = "location_type";
    String VIDEOS_COLUMN_PREVIEW              = "preview_image_location";
    String VIDEOS_COLUMN_TAGS                 = "tags";
    String VIDEOS_COLUMN_ADDED_DATE           = "added_date";
    
    CqlIdentifier VIDEOS_COLUMN_VIDEOID_      = CqlIdentifier.fromCql(VIDEOS_COLUMN_VIDEOID);
    CqlIdentifier VIDEOS_COLUMN_USERID_       = CqlIdentifier.fromCql(VIDEOS_COLUMN_USERID);
    CqlIdentifier VIDEOS_COLUMN_NAME_         = CqlIdentifier.fromCql(VIDEOS_COLUMN_NAME);
    CqlIdentifier VIDEOS_COLUMN_DESCRIPTION_  = CqlIdentifier.fromCql(VIDEOS_COLUMN_DESCRIPTION);
    CqlIdentifier VIDEOS_COLUMN_LOCATION_     = CqlIdentifier.fromCql(VIDEOS_COLUMN_LOCATION);
    CqlIdentifier VIDEOS_COLUMN_LOCATIONTYPE_ = CqlIdentifier.fromCql(VIDEOS_COLUMN_LOCATIONTYPE);
    CqlIdentifier VIDEOS_COLUMN_PREVIEW_      = CqlIdentifier.fromCql(VIDEOS_COLUMN_PREVIEW);
    CqlIdentifier VIDEOS_COLUMN_TAGS_         = CqlIdentifier.fromCql(VIDEOS_COLUMN_TAGS);
    CqlIdentifier VIDEOS_COLUMN_ADDED_DATE_   = CqlIdentifier.fromCql(VIDEOS_COLUMN_ADDED_DATE);
    
    // user_videos
    CqlIdentifier TABLENAME_USERS_VIDEO       = CqlIdentifier.fromCql("user_videos");    
    
    // latest_videos 
    CqlIdentifier TABLENAME_LATEST_VIDEO      = CqlIdentifier.fromCql("latest_videos");
    
    String LATESTVIDEOS_COLUMN_YYYYMMDD          = "yyyymmdd";
    
    CqlIdentifier LATESTVIDEOS_COLUMN_YYYYMMDD_  = CqlIdentifier.fromCql(LATESTVIDEOS_COLUMN_YYYYMMDD);
    
    // comments_by_video + comments_by_user
    String TABLENAME_COMMENTS_BY_USER            = "comments_by_user";
    String TABLENAME_COMMENTS_BY_VIDEO           = "comments_by_video";
    String COMMENTS_COLUMN_VIDEOID                = "videoid";
    String COMMENTS_COLUMN_USERID                 = "userid";
    String COMMENTS_COLUMN_COMMENTID              = "commentid";
    String COMMENTS_COLUMN_COMMENT                = "comment";
    
    CqlIdentifier TABLENAME_COMMENTS_BY_USER_    = CqlIdentifier.fromCql(TABLENAME_COMMENTS_BY_USER);
    CqlIdentifier TABLENAME_COMMENTS_BY_VIDEO_   = CqlIdentifier.fromCql(TABLENAME_COMMENTS_BY_VIDEO);
    CqlIdentifier COMMENTS_COLUMN_VIDEOID_        = CqlIdentifier.fromCql(COMMENTS_COLUMN_VIDEOID);
    CqlIdentifier COMMENTS_COLUMN_USERID_         = CqlIdentifier.fromCql(COMMENTS_COLUMN_USERID);
    CqlIdentifier COMMENTS_COLUMN_COMMENTID_      = CqlIdentifier.fromCql(COMMENTS_COLUMN_COMMENTID);
    CqlIdentifier COMMENTS_COLUMN_COMMENT_        = CqlIdentifier.fromCql(COMMENTS_COLUMN_COMMENT);
    
}
