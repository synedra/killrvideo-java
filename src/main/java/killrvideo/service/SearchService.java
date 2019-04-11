package killrvideo.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import killrvideo.search.SearchServiceGrpc.SearchServiceImplBase;
import killrvideo.search.SearchServiceOuterClass.GetQuerySuggestionsRequest;
import killrvideo.search.SearchServiceOuterClass.GetQuerySuggestionsResponse;
import killrvideo.search.SearchServiceOuterClass.SearchVideosRequest;
import killrvideo.search.SearchServiceOuterClass.SearchVideosResponse;

@Service
//public class SearchService extends AbstractSearchService {
public class SearchService extends SearchServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    @PostConstruct
    public void init() {
    }

    @Override
    public void searchVideos(SearchVideosRequest request, StreamObserver<SearchVideosResponse> responseObserver) {
        // LOGGER.debug("SEARCH VIDEOS");
    }

    @Override
    public void getQuerySuggestions(GetQuerySuggestionsRequest request, StreamObserver<GetQuerySuggestionsResponse> responseObserver) {
        // LOGGER.debug("Start getting query suggestions by name, tag, and description");
    }
}