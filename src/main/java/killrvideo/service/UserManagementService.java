package killrvideo.service;

import static killrvideo.utils.ExceptionUtils.mergeStackTrace;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.mapping.Mapper;
import com.google.common.eventbus.EventBus;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import killrvideo.common.CommonTypes;
import killrvideo.entity.Schema;
import killrvideo.entity.User;
import killrvideo.entity.UserCredentials;
import killrvideo.events.CassandraMutationError;
import killrvideo.user_management.UserManagementServiceGrpc.UserManagementServiceImplBase;
import killrvideo.user_management.UserManagementServiceOuterClass.CreateUserRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.CreateUserResponse;
import killrvideo.user_management.UserManagementServiceOuterClass.GetUserProfileRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.GetUserProfileResponse;
import killrvideo.user_management.UserManagementServiceOuterClass.VerifyCredentialsRequest;
import killrvideo.user_management.UserManagementServiceOuterClass.VerifyCredentialsResponse;
import killrvideo.user_management.events.UserManagementEvents.UserCreated;
import killrvideo.utils.FutureUtils;
import killrvideo.utils.HashUtils;
import killrvideo.utils.TypeConverter;
import killrvideo.validation.KillrVideoInputValidator;

@Service
//public class UserManagementService extends AbstractUserManagementService {
public class UserManagementService extends UserManagementServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    @Inject
    Mapper<UserCredentials> userCredentialsMapper;

    @Inject
    Mapper<User> userMapper;

    @Inject
    DseSession dseSession;

    @Inject
    EventBus eventBus;

    @Inject
    KillrVideoInputValidator validator;

    private String usersTableName;
    private String userCredentialsTableName;
    private PreparedStatement createUser_checkEmailPrepared;
    // TODO: Declare variable for prepared statement
    // private PreparedStatement ...
    private PreparedStatement getUserProfile_getUsersPrepared;

    @PostConstruct
    public void init(){
        usersTableName = userMapper.getTableMetadata().getName();
        userCredentialsTableName = userCredentialsMapper.getTableMetadata().getName();

        createUser_checkEmailPrepared = dseSession.prepare(
                QueryBuilder
                        .insertInto(Schema.KEYSPACE, userCredentialsTableName)
                        .value("email", QueryBuilder.bindMarker())
                        .value("password", QueryBuilder.bindMarker())
                        .value("userid", QueryBuilder.bindMarker())
                        .ifNotExists() // use lightweight transaction
        ).setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);

        // TODO: Create prepared statement
        // createUser_insertUserPrepared = ...

        getUserProfile_getUsersPrepared = dseSession.prepare(
                QueryBuilder
                        .select()
                        .all()
                        .from(Schema.KEYSPACE, usersTableName)
                        .where(QueryBuilder.in("userid", QueryBuilder.bindMarker()))
        ).setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {

        //TODO: REMOVE COMMENT BEGIN
        /*

        LOGGER.debug("-----Start creating user-----");

        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        final Date now = new Date();
        final CommonTypes.Uuid userIdUuid = request.getUserId();
        final UUID userIdUUID = UUID.fromString(userIdUuid.getValue());
        final String firstName = request.getFirstName();
        final String lastName = request.getLastName();

        // Trim the password
        final String hashedPassword = HashUtils.hashPassword(request.getPassword().trim());
        final String email = request.getEmail();
        final String dupUserMessaage = String.format("Exception creating user because it already exists with email %s", email);

        final BoundStatement checkEmailQuery = createUser_checkEmailPrepared.bind()
                .setString("email", email)
                .setString("password", hashedPassword)
                .setUUID("userid", userIdUUID);

        ResultSet checkEmailResultSet = dseSession.execute(checkEmailQuery);
        if (!checkEmailResultSet.wasApplied()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
              .augmentDescription(dupUserMessaage).asRuntimeException());
            responseObserver.onCompleted();
            return;
        }

        // TODO: Create a BoundStatement
        final BoundStatement insertUser = ...;

        // TODO: Execute the statement
        ResultSet insertUserResultSet = ...

        if(!insertUserResultSet.wasApplied()) {
          responseObserver.onError(Status.INVALID_ARGUMENT
            .augmentDescription("User ID not unique").asRuntimeException());
          responseObserver.onCompleted();
          return;
        }

        responseObserver.onNext(CreateUserResponse.newBuilder().build());
        responseObserver.onCompleted();

        //TODO: REMOVE BLOCK COMMENT END
        */
    }

    @Override
    public void verifyCredentials(VerifyCredentialsRequest request, StreamObserver<VerifyCredentialsResponse> responseObserver) {

        LOGGER.debug("------Start verifying user credentials------");

        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        /**
         * Since email is the partitionKey for the UserCredentials
         * entity I can simply pass it to the mapper getAsync() method
         * to get my result
         */
        FutureUtils.buildCompletableFuture(userCredentialsMapper.getAsync(request.getEmail()))
                .handle((credential, ex) -> {
                    if (credential == null || !HashUtils.isPasswordValid(request.getPassword(), credential.getPassword())) {
                        final String errorMessage = "Email address or password are not correct.";

                        LOGGER.error(errorMessage);
                        responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(errorMessage).asRuntimeException());

                    } else {
                        responseObserver.onNext(VerifyCredentialsResponse
                                .newBuilder()
                                .setUserId(TypeConverter.uuidToUuid(credential.getUserid()))
                                .build());
                        responseObserver.onCompleted();

                        LOGGER.debug("End verifying user credentials");
                    }
                    return credential;
                });
    }

    @Override
    public void getUserProfile(GetUserProfileRequest request, StreamObserver<GetUserProfileResponse> responseObserver) {

        LOGGER.debug("------Start getting user profile------");

        if (!validator.isValid(request, responseObserver)) {
            return;
        }

        final GetUserProfileResponse.Builder builder = GetUserProfileResponse.newBuilder();

        if (request.getUserIdsCount() == 0 || CollectionUtils.isEmpty(request.getUserIdsList())) {
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

            LOGGER.debug("No user id provided");

            return;
        }

        final UUID[] userIds = request
                .getUserIdsList()
                .stream()
                .map(uuid -> UUID.fromString(uuid.getValue()))
                .toArray(size -> new UUID[size]);

        /**
         * Instead of firing multiple async SELECT, we can as well use
         * the IN(..) clause to fetch multiple user infos. It is recommended
         * to limit the number of values inside the IN clause to a dozen
         */
        BoundStatement getUsersQuery = getUserProfile_getUsersPrepared.bind()
                .setList(0, Arrays.asList(userIds), UUID.class);

        FutureUtils.buildCompletableFuture(userMapper.mapAsync(dseSession.executeAsync(getUsersQuery)))
                .handle((users, ex) -> {
                    if (users != null) {
                        users.forEach(user -> builder.addProfiles(user.toUserProfile()));
                        responseObserver.onNext(builder.build());
                        responseObserver.onCompleted();

                        LOGGER.debug("End getting user profile");

                    } else if (ex != null) {
                        LOGGER.error("Exception getting user profile : " + mergeStackTrace(ex));
                        responseObserver.onError(Status.INTERNAL.withCause(ex).asRuntimeException());

                    }
                    return users;
                });
    }
}
