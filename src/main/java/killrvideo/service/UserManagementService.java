package killrvideo.service;

import javax.annotation.PostConstruct;

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

    @Override
    public void verifyCredentials(VerifyCredentialsRequest request, StreamObserver<VerifyCredentialsResponse> responseObserver) {

        LOGGER.debug("------Start verifying user credentials------");

    }

    @Override
    public void getUserProfile(GetUserProfileRequest request, StreamObserver<GetUserProfileResponse> responseObserver) {

        LOGGER.debug("------Start getting user profile------");

    }
}
