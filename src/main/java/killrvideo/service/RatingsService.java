package killrvideo.service;

import static killrvideo.utils.ExceptionUtils.mergeStackTrace;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import killrvideo.entity.Schema;
import killrvideo.entity.VideoRating;
import killrvideo.entity.VideoRatingByUser;
import killrvideo.events.CassandraMutationError;
import killrvideo.ratings.RatingsServiceGrpc.RatingsServiceImplBase;
import killrvideo.ratings.RatingsServiceOuterClass.GetRatingRequest;
import killrvideo.ratings.RatingsServiceOuterClass.GetRatingResponse;
import killrvideo.ratings.RatingsServiceOuterClass.GetUserRatingRequest;
import killrvideo.ratings.RatingsServiceOuterClass.GetUserRatingResponse;
import killrvideo.ratings.RatingsServiceOuterClass.RateVideoRequest;
import killrvideo.ratings.RatingsServiceOuterClass.RateVideoResponse;
import killrvideo.ratings.events.RatingsEvents.UserRatedVideo;
import killrvideo.utils.FutureUtils;
import killrvideo.utils.TypeConverter;
import killrvideo.validation.KillrVideoInputValidator;

@Service
public class RatingsService extends RatingsServiceImplBase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingsService.class);

    @PostConstruct
    public void init(){
    }

    @Override
    public void rateVideo(RateVideoRequest request, StreamObserver<RateVideoResponse> responseObserver) {
        // LOGGER.debug("-----Start rate video request-----");
    }

    @Override
    public void getRating(GetRatingRequest request, StreamObserver<GetRatingResponse> responseObserver) {
        // LOGGER.debug("-----Start get video rating request-----");
        responseObserver.onNext(GetRatingResponse.newBuilder()
            .setVideoId(request.getVideoId())
            .setRatingsCount(0L)
            .setRatingsTotal(0L)
            .build());

        responseObserver.onCompleted();
        // LOGGER.debug("-----End get video rating request-----");
    }

    @Override
    public void getUserRating(GetUserRatingRequest request, StreamObserver<GetUserRatingResponse> responseObserver) {
        // LOGGER.debug("-----Start get user rating request-----");
        responseObserver.onNext(GetUserRatingResponse
                                    .newBuilder()
                                    .setUserId(request.getUserId())
                                    .setVideoId(request.getVideoId())
                                    .setRating(0)
                                    .build());
        responseObserver.onCompleted();
    }

}
