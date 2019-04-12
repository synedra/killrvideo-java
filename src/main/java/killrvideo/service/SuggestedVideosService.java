package killrvideo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.Subscribe;

import io.grpc.stub.StreamObserver;
import killrvideo.common.CommonTypes.Uuid;
import killrvideo.ratings.events.RatingsEvents.UserRatedVideo;
import killrvideo.suggested_videos.SuggestedVideoServiceGrpc.SuggestedVideoServiceImplBase;
import killrvideo.suggested_videos.SuggestedVideosService.GetRelatedVideosRequest;
import killrvideo.suggested_videos.SuggestedVideosService.GetRelatedVideosResponse;
import killrvideo.suggested_videos.SuggestedVideosService.GetSuggestedForUserRequest;
import killrvideo.suggested_videos.SuggestedVideosService.GetSuggestedForUserResponse;
import killrvideo.suggested_videos.SuggestedVideosService.SuggestedVideoPreview;
import killrvideo.user_management.events.UserManagementEvents.UserCreated;
import killrvideo.video_catalog.events.VideoCatalogEvents.YouTubeVideoAdded;

@Service
public class SuggestedVideosService extends SuggestedVideoServiceImplBase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestedVideosService.class);

    @PostConstruct
    public void init(){

    }

    @Override
    public void getRelatedVideos(GetRelatedVideosRequest request, StreamObserver<GetRelatedVideosResponse> responseObserver) {
        // LOGGER.info("getRelatedVideos");
        final Uuid videoIdUuid = request.getVideoId();

        final GetRelatedVideosResponse.Builder builder = GetRelatedVideosResponse.newBuilder()
                .setVideoId(videoIdUuid);
                
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        // LOGGER.info("END getRelatedVideos");
    }

    @Override
    public void getSuggestedForUser(GetSuggestedForUserRequest request, StreamObserver<GetSuggestedForUserResponse> responseObserver) {
        // LOGGER.info("getSuggestedForUser");
        
        final Uuid userId = request.getUserId();
        final GetSuggestedForUserResponse.Builder builder = GetSuggestedForUserResponse.newBuilder();
        builder.setUserId(userId);
        
        final List<SuggestedVideoPreview> result = new ArrayList<SuggestedVideoPreview>();
        builder.addAllVideos(result);
               
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        // LOGGER.info("END getSuggestedForUser");
    }

    /**
     * Make @Subscribe subscriber magic happen anytime a youTube video is added from
     * VideoCatalogService.submitYouTubeVideo() with a call to eventBus.post().
     * We use this to create entries in our graph database for use with our
     * SuggestedVideos recommendation service which is why this exists here.
     * @param youTubeVideoAdded
     */
    @Subscribe
    public void handle(YouTubeVideoAdded youTubeVideoAdded) {
        // LOGGER.info("XXXXXXXXXXXXX handle");
    }

    /**
     * Make @Subscribe subscriber magic happen anytime a user is created from
     * UserManagementService.createUser() with a call to eventBus.post().
     * We use this to create entries in our graph database for use with our
     * SuggestedVideos recommendation service which is why this exists here.
     * @param userCreated
     */
    @Subscribe
    public void handle(UserCreated userCreated) {
        // LOGGER.info("XXXXXXXXXXXXXX handle(UserCreated)");
    }

    /**
     * Make @Subscribe subscriber magic happen anytime a user rates a video
     * RatingsService.rateVideo() with a call to eventBus.post().
     * We use this to create entries in our graph database for use with our
     * SuggestedVideos recommendation service which is why this exists here.
     * @param userRatedVideo
     */
    @Subscribe
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void handle(UserRatedVideo userRatedVideo) {
        // LOGGER.info("XXXXXXXXXXXXXXX handle(UserRatedVideo");
    }
}
