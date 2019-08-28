package com.killrvideo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.killrvideo.service.comment.grpc.CommentsServiceGrpc;
import com.killrvideo.service.rating.grpc.RatingsServiceGrpc;
import com.killrvideo.service.search.grpc.SearchServiceGrpc;
import com.killrvideo.service.statistic.grpc.StatisticsServiceGrpc;
import com.killrvideo.service.sugestedvideo.grpc.SuggestedVideosServiceGrpc;
import com.killrvideo.service.user.grpc.UserManagementServiceGrpc;
import com.killrvideo.service.video.grpc.VideoCatalogServiceGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Startup a GRPC server on expected port and register all services.
 *
 * @author DataStax advocates team.
 */
@Component
public class KillrvideoServicesGrpcServer {

    /** Some logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(KillrvideoServicesGrpcServer.class);
    
    /** Listening Port for GRPC. */
    @Value("${killrvideo.grpc-server.port: 50101}")
    private int grpcPort;
    
    @Autowired
    private CommentsServiceGrpc commentService;
    
    @Value("${killrvideo.grpc-server.services.comment: true}")
    private boolean commentServiceEnabled = true;
    
    @Autowired
    private RatingsServiceGrpc ratingService;
    
    @Value("${killrvideo.grpc-server.services.rating: true}")
    private boolean ratingServiceEnabled = true;
    
    @Autowired
    private SearchServiceGrpc searchService;
    
    @Value("${killrvideo.grpc-server.services.search: true}")
    private boolean searchServiceEnabled = true;
    
    @Autowired
    private StatisticsServiceGrpc statisticsService;
    
    @Value("${killrvideo.grpc-server.services.statistic: true}")
    private boolean statisticServiceEnabled = true;
    
    @Autowired
    private VideoCatalogServiceGrpc videoCatalogService;
 
    @Value("${killrvideo.grpc-server.services.videoCatalog: true}")
    private boolean videoCatalogServiceEnabled = true;
    
    @Autowired
    private UserManagementServiceGrpc userService;
    
    @Value("${killrvideo.grpc-server.services.user: true}")
    private boolean userServiceEnabled = true;
    
    @Autowired
    private SuggestedVideosServiceGrpc suggestedVideosService;
    
    @Value("${killrvideo.grpc-server.services.suggestedVideo: true}")
    private boolean suggestedVideoServiceEnabled = true;
  
    /**
     * GRPC Server to set up.
     */
    private Server grpcServer;
    
    @PostConstruct
    public void start() throws Exception {
        LOGGER.info("Starting Grpc Server...");
        // Create GRPC server referencing only enabled services
        ServerBuilder<?> builder = ServerBuilder.forPort(grpcPort);
        if (commentServiceEnabled) {
            builder.addService(this.commentService.bindService());
            LOGGER.info(" + Enable service 'Comment");
        }
        if (ratingServiceEnabled) {
            builder.addService(this.ratingService.bindService());
            LOGGER.info(" + Enable service 'Rating'");
        }
        if (searchServiceEnabled) {
            builder.addService(this.searchService.bindService());
            LOGGER.info(" + Enable service 'Search'");
        }
        if (statisticServiceEnabled) {
            builder.addService(this.statisticsService.bindService());
            LOGGER.info(" + Enable service 'Statistics'");
        }
        if (videoCatalogServiceEnabled) {
            builder.addService(this.videoCatalogService.bindService());
            LOGGER.info(" + Enable service 'VideoCatalog'");
        }
        if (suggestedVideoServiceEnabled) {
            builder.addService(this.suggestedVideosService.bindService());
            LOGGER.info(" + Enable service 'SuggestedVideo'");
        }
        if (userServiceEnabled) {
            builder.addService(this.userService.bindService());
            LOGGER.info(" + Enable Service 'User'");
        }
        grpcServer = builder.build();
        
        // Declare a shutdown hook otherwise JVM is listening on  a port forever
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                stopGrpcServer();
            }
        });
        // Start Grpc listener
        grpcServer.start();
        LOGGER.info("[OK] Grpc Server started on port: '{}'", grpcPort);
    }
    
    @PreDestroy
    public void stopGrpcServer() {
        LOGGER.info("Calling shutdown for GrpcServer");
        grpcServer.shutdown();
    }
    
}
