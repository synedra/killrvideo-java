package killrvideo.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import killrvideo.common.CommonTypes.Uuid;
import killrvideo.statistics.StatisticsServiceGrpc.StatisticsServiceImplBase;
import killrvideo.statistics.StatisticsServiceOuterClass.GetNumberOfPlaysRequest;
import killrvideo.statistics.StatisticsServiceOuterClass.GetNumberOfPlaysResponse;
import killrvideo.statistics.StatisticsServiceOuterClass.PlayStats;
import killrvideo.statistics.StatisticsServiceOuterClass.RecordPlaybackStartedRequest;
import killrvideo.statistics.StatisticsServiceOuterClass.RecordPlaybackStartedResponse;

@Service
//public class StatisticsService extends AbstractStatisticsService {
public class StatisticsService extends StatisticsServiceImplBase {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    @PostConstruct
    public void init(){
    }

    @Override
    public void recordPlaybackStarted(RecordPlaybackStartedRequest request, StreamObserver<RecordPlaybackStartedResponse> responseObserver) {
    }

    @Override
    public void getNumberOfPlays(GetNumberOfPlaysRequest request, StreamObserver<GetNumberOfPlaysResponse> responseObserver) {
        // LOGGER.debug("-----Start getting number of plays------");
        final GetNumberOfPlaysResponse.Builder builder = GetNumberOfPlaysResponse.newBuilder();

        for (Uuid requestedVideoId : request.getVideoIdsList()) {
            builder.addStats(PlayStats
                .newBuilder()
                .setVideoId(requestedVideoId)
                .setViews(0L)
                .build());
        }
                                        
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        // LOGGER.debug("-----End getting number of plays------");
    }
}
