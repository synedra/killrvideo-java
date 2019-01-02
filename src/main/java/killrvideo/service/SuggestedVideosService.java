package killrvideo.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.Subscribe;

import io.grpc.stub.StreamObserver;
import killrvideo.ratings.events.RatingsEvents.UserRatedVideo;
import killrvideo.suggested_videos.SuggestedVideoServiceGrpc.SuggestedVideoServiceImplBase;
import killrvideo.suggested_videos.SuggestedVideosService.GetRelatedVideosRequest;
import killrvideo.suggested_videos.SuggestedVideosService.GetRelatedVideosResponse;
import killrvideo.suggested_videos.SuggestedVideosService.GetSuggestedForUserRequest;
import killrvideo.suggested_videos.SuggestedVideosService.GetSuggestedForUserResponse;
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

    }

    @Override
    public void getSuggestedForUser(GetSuggestedForUserRequest request, StreamObserver<GetSuggestedForUserResponse> responseObserver) {

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

    }
}
