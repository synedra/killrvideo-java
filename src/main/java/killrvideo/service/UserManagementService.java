package killrvideo.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import killrvideo.user_management.UserManagementServiceGrpc.UserManagementServiceImplBase;
import killrvideo.user_management.UserManagementServiceOuterClass.CreateUserRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.CreateUserResponse;
import killrvideo.user_management.UserManagementServiceOuterClass.GetUserProfileRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.GetUserProfileResponse;
import killrvideo.user_management.UserManagementServiceOuterClass.VerifyCredentialsRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.VerifyCredentialsResponse;
import killrvideo.utils.TypeConverter;


@Service
//public class UserManagementService extends AbstractUserManagementService {
public class UserManagementService extends UserManagementServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    @PostConstruct
    public void init(){
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {

        LOGGER.debug("-----Start creating user-----");

    }

    private static UUID getAuthenticatedUserId(String username, String password) throws Exception {
        return null;
    }

    @Override
    public void verifyCredentials(VerifyCredentialsRequest request, StreamObserver<VerifyCredentialsResponse> responseObserver) {

       LOGGER.debug("------Start verifying user credentials------");

        try
        {        
            String email = request.getEmail();
            String passwordFromRequest = request.getPassword();
            UUID userId = getAuthenticatedUserId(email, passwordFromRequest);
            
            if (userId == null){
                LOGGER.info("Invalid credentials");
                responseObserver.onError(new Throwable("Invalid credentials"));
            } else {
                    LOGGER.info("Boom shakalaka we in!");
                    LOGGER.info("User ID: " + userId);
                    
                    responseObserver.onNext(VerifyCredentialsResponse
                                .newBuilder()
                                .setUserId(TypeConverter.uuidToUuid(userId))
                                .build());
                    responseObserver.onCompleted();
                    LOGGER.info("Response complete.");
            }
        }
        catch(Throwable e)
        {
            LOGGER.debug("Error: " + e);
        }
    }

    @Override
    public void getUserProfile(GetUserProfileRequest request, StreamObserver<GetUserProfileResponse> responseObserver) {

        LOGGER.debug("------Start getting user profile------");

    }
}