package killrvideo.service;

import static java.util.UUID.fromString;
import static killrvideo.utils.ExceptionUtils.mergeStackTrace;
import static killrvideo.utils.FutureUtils.buildCompletableFuture;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.eventbus.EventBus;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import killrvideo.comments.CommentsServiceGrpc.CommentsServiceImplBase;
import killrvideo.comments.CommentsServiceOuterClass.CommentOnVideoRequest;
import killrvideo.comments.CommentsServiceOuterClass.CommentOnVideoResponse;
import killrvideo.comments.CommentsServiceOuterClass.GetUserCommentsRequest;
import killrvideo.comments.CommentsServiceOuterClass.GetUserCommentsResponse;
import killrvideo.comments.CommentsServiceOuterClass.GetVideoCommentsRequest;
import killrvideo.comments.CommentsServiceOuterClass.GetVideoCommentsResponse;
import killrvideo.comments.events.CommentsEvents.UserCommentedOnVideo;
import killrvideo.common.CommonTypes.TimeUuid;
import killrvideo.common.CommonTypes.Uuid;
import killrvideo.entity.CommentsByUser;
import killrvideo.entity.CommentsByVideo;
import killrvideo.entity.Schema;
import killrvideo.events.CassandraMutationError;
import killrvideo.utils.TypeConverter;
import killrvideo.validation.KillrVideoInputValidator;

/**
 * Handling Comments. Create and search by user and videos.
 *
 * @author DataStax evangelist team.
 */
@Service
public class CommentService extends CommentsServiceImplBase {

    /** Loger for that class. */
    private static Logger LOGGER = LoggerFactory.getLogger(CommentService.class);
    
    /**
     * Preparing statement before aueries allow signifiant performance improvements.
     * This can only be done it the statement is 'static', mean the number of parameter
     * to bind() is fixed. If not the case you can find sample in method buildStatement*() in this class.
     */
    @PostConstruct
    private void initializeStatements () {
    }

    /** {@inheritDoc} */
    @Override
    public void commentOnVideo(
            final CommentOnVideoRequest request, 
            StreamObserver<CommentOnVideoResponse> responseObserver) {
        // LOGGER.info("commentOnVideo");
    }
    
    /** {@inheritDoc} */
    @Override
    public void getUserComments(
            final GetUserCommentsRequest request, 
            StreamObserver<GetUserCommentsResponse> responseObserver) {
        // LOGGER.info("getUserComments");
    }

    /** {@inheritDoc} */
    @Override
    public void getVideoComments(
                final GetVideoCommentsRequest request, 
                StreamObserver<GetVideoCommentsResponse> responseObserver) {
         // LOGGER.info("getVideoComments");
         final GetVideoCommentsResponse.Builder builder = GetVideoCommentsResponse.newBuilder();
         responseObserver.onNext(builder.build());
         responseObserver.onCompleted();
         // LOGGER.info("END getVideoComments");
    }
}
