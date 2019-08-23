package com.killrvideo.service.video.dao;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;
import static com.killrvideo.dse.utils.DseUtils.bind;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.DefaultBatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.killrvideo.dse.dao.DseSchema;
import com.killrvideo.dse.dto.CustomPagingState;
import com.killrvideo.dse.dto.ResultListPage;
import com.killrvideo.dse.dto.Video;
import com.killrvideo.service.video.dto.LatestVideo;
import com.killrvideo.service.video.dto.LatestVideosPage;
import com.killrvideo.service.video.dto.UserVideo;
import com.killrvideo.utils.IOUtils;

/**
 * Implementation of specific queries for {@link VideoCatalogDseDao} interface.
 *
 * @author DataStax Developer Advocates team.
 */
public class VideoCatalogDseDaoQueryProvider implements DseSchema {

    /** Logger for this class. */
    private static Logger LOGGER = LoggerFactory.getLogger(VideoCatalogDseDao.class);
    
    /** Formatting date. */
    public static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    private CqlSession dseSession;
    
    private EntityHelper<Video>       entityHelperVideo;
    private EntityHelper<LatestVideo> entityHelperVideoLatest;
    private EntityHelper<UserVideo>   entityHelperVideoUser;
    
    private PreparedStatement         psInsertVideo;
    private PreparedStatement         psInsertVideoUser;
    private PreparedStatement         psInsertVideoLatest;
    private PreparedStatement         psSelectLatestVideoDefault;
    private PreparedStatement         psSelectLatestVideoPageX;
    private PreparedStatement         psSelectUserVideoDefault;
    private PreparedStatement         psSelectUserVideoPageX;
    
    /**
     * Constructor invoked by the DataStax driver based on Annotation {@link QueryProvider} 
     * set on class {@link VideoCatalogDseDao}.
     * 
     * @param context
     *      context to extrat dse session
     * @param helperVideo
     *      entity helper to interact with bean {@link Video}
     * @param helperVideoLatest
     *      entity helper to interact with bean {@link LatestVideo}
     * @param helperVideoUser
     *      entity helper to interact with bean {@link UserVideo}      
     */
    public VideoCatalogDseDaoQueryProvider(MapperContext context,
            EntityHelper<Video>       helperVideo,
            EntityHelper<LatestVideo> helperVideoLatest,
            EntityHelper<UserVideo>   helperVideoUser) {
        this.dseSession              = context.getSession();
        this.entityHelperVideo       = helperVideo;
        this.entityHelperVideoLatest = helperVideoLatest;
        this.entityHelperVideoUser   = helperVideoUser;
        
        psSelectLatestVideoDefault = dseSession.prepare(
                selectFrom(TABLENAME_LATEST_VIDEO_).all()
                .where(column(LATESTVIDEOS_COLUMN_YYYYMMDD_).isEqualTo(bindMarker(LATESTVIDEOS_COLUMN_YYYYMMDD_)))
                .build());
        psSelectLatestVideoPageX = dseSession.prepare(
                selectFrom(TABLENAME_LATEST_VIDEO_).all()
                .where(column(LATESTVIDEOS_COLUMN_YYYYMMDD_).isEqualTo(bindMarker(LATESTVIDEOS_COLUMN_YYYYMMDD_)),
                       column(VIDEOS_COLUMN_ADDED_DATE_).isLessThanOrEqualTo(bindMarker(VIDEOS_COLUMN_ADDED_DATE_)),
                       column(LATESTVIDEOS_COLUMN_VIDEOID_).isLessThanOrEqualTo(bindMarker(LATESTVIDEOS_COLUMN_VIDEOID_)))
                .build()); 
        
        psSelectUserVideoDefault = dseSession.prepare(
                selectFrom(TABLENAME_USERS_VIDEO_).all()
                .where(column(VIDEOS_COLUMN_USERID_).isEqualTo(bindMarker(VIDEOS_COLUMN_USERID_)))
                .build());
        psSelectUserVideoPageX = dseSession.prepare(
                selectFrom(TABLENAME_USERS_VIDEO_).all()
                .where(column(VIDEOS_COLUMN_USERID_).isEqualTo(bindMarker(VIDEOS_COLUMN_USERID_)),
                        column(VIDEOS_COLUMN_ADDED_DATE_).isLessThanOrEqualTo(bindMarker(VIDEOS_COLUMN_ADDED_DATE_)),
                        column(VIDEOS_COLUMN_VIDEOID_).isLessThanOrEqualTo(bindMarker(VIDEOS_COLUMN_VIDEOID_)))
                .build());     
    }
    
    /** Javadoc in {@link VideoCatalogDseDao} */
    public CompletionStage<Void> insertVideoAsync(Video video) {
        video.setAddedDate(new Date());
        return dseSession.executeAsync(BatchStatement.builder(DefaultBatchType.LOGGED)
                .addStatement(bind(psInsertVideo,       video,                   entityHelperVideo))
                .addStatement(bind(psInsertVideoLatest, new LatestVideo(video),  entityHelperVideoLatest))
                .addStatement(bind(psInsertVideoUser,   new UserVideo(video),    entityHelperVideoUser))
                .build()).thenApply(rs -> null);
    }
    
    /** Javadoc in {@link VideoCatalogDseDao} */
    public CompletionStage< ResultListPage <UserVideo> > getUserVideosPreview(UUID userId, 
            Optional<UUID> startingVideoId, 
            Optional<Date> startingAddedDate,
            Optional<Integer> pageSize,
            Optional<String>  pagingState) {
        
        // Build expected Bound Statement based on Parameters
        BoundStatement bsSelectUserVideo;
        if (startingVideoId.isPresent() && startingAddedDate.isPresent()) {
            bsSelectUserVideo = psSelectUserVideoPageX.bind()
                     .setUuid(VIDEOS_COLUMN_USERID_, userId)
                     .setInstant(VIDEOS_COLUMN_ADDED_DATE_, Instant.ofEpochMilli(startingAddedDate.get().getTime()))
                     .setUuid(VIDEOS_COLUMN_VIDEOID_, startingVideoId.get());
        } else {
            bsSelectUserVideo = psSelectUserVideoDefault.bind()
                    .setUuid(VIDEOS_COLUMN_USERID_, userId);
        }
        LOGGER.debug("searchUserVideo query: {}", bsSelectUserVideo.getPreparedStatement().getQuery());
        pageSize.ifPresent(bsSelectUserVideo::setPageSize);
        pagingState.ifPresent(x -> {
            bsSelectUserVideo.setPagingState(IOUtils.fromString2ByteBuffer(x));
            LOGGER.debug("searchUserVideo pagingState:{}", x);
        });
        
        // Execute Query Asynchronously
        return dseSession.executeAsync(bsSelectUserVideo)
                  .thenApply(ars  -> ars.map(entityHelperVideoUser::get))
                  .thenApply(ResultListPage::new);
    }
    
    /** Javadoc in {@link VideoCatalogDseDao} */
    public LatestVideosPage getLatestVideoPreviews(CustomPagingState cpState, 
            int pageSize, 
            Optional<Date> startDate, 
            Optional<UUID> startVid) {
        
        LatestVideosPage returnedPage = new LatestVideosPage();
        LOGGER.debug("Looking for {} latest video(s)", pageSize);
      
        // Flag to syncrhonize usage of cassandra paging state
        final AtomicBoolean isCassandraPageState = new AtomicBoolean(false);
       
        do {
          
          // (1) - Paging state (custom or cassandra)
          final Optional<String> pagingState = 
                  Optional.ofNullable(cpState.getCassandraPagingState())  // Only if present .get()
                          .filter(StringUtils::isNotBlank)                // ..and not empty
                          .filter(pg -> !isCassandraPageState.get());     // ..and cassandra paging is off
          
          // (2) - Build Bound Statement
          BoundStatement bsSelectLatestVideo;
          if (startDate.isPresent() && startVid.isPresent()) {
              bsSelectLatestVideo = psSelectLatestVideoPageX.bind()
                      .setString(LATESTVIDEOS_COLUMN_YYYYMMDD_, cpState.getCurrentBucketValue())
                      .setInstant(VIDEOS_COLUMN_ADDED_DATE_, Instant.ofEpochMilli(startDate.get().getTime()))
                      .setUuid(LATESTVIDEOS_COLUMN_VIDEOID_, startVid.get());        
          } else {
              bsSelectLatestVideo = psSelectLatestVideoDefault.bind()
                       .setString(LATESTVIDEOS_COLUMN_YYYYMMDD_, cpState.getCurrentBucketValue());
          }
          LOGGER.debug("searchLatestVideo query: {}", bsSelectLatestVideo.getPreparedStatement().getQuery());
          bsSelectLatestVideo.setPageSize(pageSize);
          pagingState.ifPresent(x -> {
              bsSelectLatestVideo.setPagingState(IOUtils.fromString2ByteBuffer(x));
              isCassandraPageState.compareAndSet(false, true);
              LOGGER.debug("searchLatestVideo pagingState:{}", x);
          });
          
          // (3) - Execute Query Asynchronously
          LatestVideosPage currentPage = new LatestVideosPage();
          ResultSet resultSet = dseSession.execute(bsSelectLatestVideo);
          ByteBuffer pState = resultSet.getExecutionInfo().getPagingState();
          if (null != pState) {
              currentPage.setCassandraPagingState(IOUtils.fromByteBuffer2String(pState));
          }
          PagingIterable<LatestVideo> plv = resultSet.map(entityHelperVideoLatest::get);
          int remaining = plv.getAvailableWithoutFetching();
          for (LatestVideo latestVideo : plv) {
              currentPage.addLatestVideos(latestVideo);
              if (--remaining == 0) {
                  break;
              }
          }
          
          // (4) - Wait for result before triggering auery for page N+1
          returnedPage.getListOfPreview().addAll(currentPage.getListOfPreview());
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug(" + bucket:{}/{} with results:{}/{} and pagingState:{}",cpState.getCurrentBucket(), 
                      cpState.getListOfBucketsSize(), returnedPage.getResultSize(), pageSize, returnedPage.getCassandraPagingState());
          }
          
          // (5) Update NEXT PAGE BASE on current status
          if (returnedPage.getResultSize() == pageSize) {
              if (!StringUtils.isBlank(currentPage.getCassandraPagingState())) {
                  returnedPage.setNextPageState(createPagingState(cpState.getListOfBuckets(), 
                          cpState.getCurrentBucket(), currentPage.getCassandraPagingState()));
                  LOGGER.debug(" + Exiting because we got enought results.");
              }
          // --> Start from the beginning of the next bucket since we're out of rows in this one
          } else if (cpState.getCurrentBucket() == cpState.getListOfBucketsSize() - 1) {
              returnedPage.setNextPageState(createPagingState(cpState.getListOfBuckets(), cpState.getCurrentBucket() + 1, ""));
              LOGGER.debug(" + Exiting because we are out of Buckets even if not enough results");
          }
              
          // (6) Move to next BUCKET
          cpState.incCurrentBucketIndex();
            
        } while ( (returnedPage.getListOfPreview().size() < pageSize)               // Result has enough element to fill the page
                   && cpState.getCurrentBucket() < cpState.getListOfBucketsSize()); // No nore bucket available
        
        return returnedPage;
    }
    
    
    /**
     * Create a paging state string from the passed in parameters
     * @param buckets
     * @param bucketIndex
     * @param rowsPagingState
     * @return String
     */
    private String createPagingState(List<String> buckets, int bucketIndex, String rowsPagingState) {
        StringJoiner joiner = new StringJoiner("_");
        buckets.forEach(joiner::add);
        return joiner.toString() + "," + bucketIndex + "," + rowsPagingState;
    }
    
}
