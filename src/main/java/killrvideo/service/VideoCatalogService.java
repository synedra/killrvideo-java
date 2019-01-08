package killrvideo.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import io.grpc.Status;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import killrvideo.validation.KillrVideoInputValidator;
import killrvideo.video_catalog.VideoCatalogServiceGrpc.VideoCatalogServiceImplBase;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetUserVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetUserVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoPreviewsResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetVideoResponse;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.SubmitYouTubeVideoRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.SubmitYouTubeVideoResponse;

import java.util.ArrayList;
import java.util.List;

import static killrvideo.utils.ExceptionUtils.mergeStackTrace;


@Service
//public class VideoCatalogService extends AbstractVideoCatalogService {
public class VideoCatalogService extends VideoCatalogServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoCatalogService.class);

    @Inject
    KillrVideoInputValidator validator;

    @PostConstruct
    public void init(){
    }

    @Override
    public void submitYouTubeVideo(SubmitYouTubeVideoRequest request, StreamObserver<SubmitYouTubeVideoResponse> responseObserver) {

    }

    @Override
    public void getVideo(GetVideoRequest request, StreamObserver<GetVideoResponse> responseObserver) {

    }

    @Override
    public void getVideoPreviews(GetVideoPreviewsRequest request, StreamObserver<GetVideoPreviewsResponse> responseObserver) {

    }

    @Override
    public void getLatestVideoPreviews(GetLatestVideoPreviewsRequest request, StreamObserver<GetLatestVideoPreviewsResponse> responseObserver) {
        LOGGER.debug("-----Start getting latest video preview-----");

        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        final List<VideoCatalogServiceOuterClass.VideoPreview> results = new ArrayList<>();

        try {
            responseObserver.onNext(GetLatestVideoPreviewsResponse
                    .newBuilder()
                    .addAllVideoPreviews(results)
                    .build());
            responseObserver.onCompleted();

        } catch (Throwable throwable) {
            LOGGER.error("Exception when getting latest preview videos : " + mergeStackTrace(throwable));
            responseObserver.onError(Status.INTERNAL.withCause(throwable).asRuntimeException());
        }

        LOGGER.debug("End getting latest video preview");
    }


    @Override
    public void getUserVideoPreviews(GetUserVideoPreviewsRequest request, StreamObserver<GetUserVideoPreviewsResponse> responseObserver) {

    }

}