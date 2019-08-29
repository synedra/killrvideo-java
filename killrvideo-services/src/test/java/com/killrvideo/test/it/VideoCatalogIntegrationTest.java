package com.killrvideo.test.it;

import com.killrvideo.KillrvideoServicesGrpcClient;

import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsRequest;
import killrvideo.video_catalog.VideoCatalogServiceOuterClass.GetLatestVideoPreviewsResponse;

public class VideoCatalogIntegrationTest {
    
    public static void main(String[] args) throws Exception {
        
        KillrvideoServicesGrpcClient client = new KillrvideoServicesGrpcClient("localhost", 50101);
        callLastestVideos(client);
        
        /*
        SubmitYouTubeVideoRequest myNewVideoYoutube = SubmitYouTubeVideoRequest.newBuilder()
                .addTags("Cassandra")
                .setDescription("MyVideo")
                .setName(" My Sample Video")
                .setUserId(GrpcMappingUtils.uuidToUuid(UUID.randomUUID()))
                .setVideoId(GrpcMappingUtils.uuidToUuid(UUID.randomUUID()))
                .setYouTubeVideoId("EBMriswzd94")
                .build();
        client.videoCatalogServiceGrpcClient.submitYouTubeVideo(myNewVideoYoutube);
       
        
        VerifyCredentialsRequest creRequest = VerifyCredentialsRequest.newBuilder()
                .setEmail("a.a@a.com")
                .setPassword("aaa")
                .build();
               
        VerifyCredentialsResponse res = client.userServiceGrpcClient.verifyCredentials(creRequest);
        System.out.println(res.getUserId());
        */
        
    }
    
    private static void callLastestVideos(KillrvideoServicesGrpcClient client)
    throws Exception {
        GetLatestVideoPreviewsRequest req = GetLatestVideoPreviewsRequest.newBuilder()
                .setPageSize(8)
                .build();
        GetLatestVideoPreviewsResponse res = client.videoCatalogServiceGrpcClient.getLatestVideoPreviews(req);
        Thread.sleep(1000);
        System.out.println(res.getVideoPreviewsList());
    }

}
