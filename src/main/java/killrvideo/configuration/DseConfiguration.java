package killrvideo.configuration;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.SpeculativeExecutionPolicy;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.core.Metadata;

import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.dse.DseCluster.Builder;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.dse.auth.DsePlainTextAuthProvider;
import com.datastax.driver.dse.graph.GraphOptions;
import com.datastax.driver.dse.graph.GraphProtocol;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.dse.graph.api.DseGraph;
import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

import killrvideo.dao.EtcdDao;
import killrvideo.graph.KillrVideoTraversalSource;

import io.netty.handler.ssl.SslContext;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.net.ssl.TrustManagerFactory;

/**
 * Connectivity to DSE (cassandra, graph, search, analytics).
 *
 * @author DataStax evangelist team.
 */
@Configuration
public class DseConfiguration {

	/** Internal logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DseConfiguration.class);
    
    @Value("${killrvideo.cassandra.clustername: 'killrvideo'}")
    public String dseClusterName;
    
    @Value("${killrvideo.graph.timeout: 30000}")
    public Integer graphTimeout;
    
    @Value("${killrvideo.graph.recommendation.name: 'killrvideo_video_recommendations'}")
    public String graphRecommendationName;
    
    @Value("#{environment.KILLRVIDEO_DSE_USERNAME}")
    public Optional < String > dseUsername;
   
    @Value("#{environment.KILLRVIDEO_DSE_PASSWORD}")
    public Optional < String > dsePassword;

    @Value("#{environment.KILLRVIDEO_ENABLE_SSL}")
    public Optional < Boolean > dseEnableSSL;

    @Value("${killrvideo.cassandra.ssl.CACertFileLocation: 'cassandra.cert'}")
    private String sslCACertFileLocation;
   
    @Value("${killrvideo.cassandra.maxNumberOfTries: 10}")
    private int maxNumberOfTries;
    
    @Value("${killrvideo.cassandra.delayBetweenTries: 2}")
    private int delayBetweenTries;
  
    @Autowired
    private EtcdDao etcdDao;

    @Inject DseSession dseSession;
    
    @Bean
    public DseSession initializeDSE() {
        long top = System.currentTimeMillis();
        LOGGER.info("Initializing connection to DSE");
        Builder clusterConfig = new Builder();
        clusterConfig.withClusterName(dseClusterName);

       // Grab contact points from ETCD and pass those into our clusterConfig
        populateContactPoints(clusterConfig);

        // Check if a username and password are present and if they are populate clusterConfig
        final String username = dseUsername.orElse("");
        final String password = dsePassword.orElse("");
        if (username.length() > 0 && password.length() > 0) {
            LOGGER.info(" + Both username and password have values > 0, assuming authentication is intended");
            populateAuthentication(clusterConfig, username, password);

        } else {
            LOGGER.info(" + Connection is not authenticated (no username/password)");
        }

        // Populate clusterConfig with graph options for recommendation engine
        populateGraphOptions(clusterConfig);

        // Check if SSL is enabled and populate clusterConfig if it is
        final boolean enableSSL = dseEnableSSL.orElse(false);
        if (enableSSL) {
            LOGGER.info(" + SSL is enabled, using supplied SSL certificate: '{}'", sslCACertFileLocation);
            populateSSL(clusterConfig);

        } else {
            LOGGER.info(" + SSL encryption is not enabled");
        }

        final AtomicInteger atomicCount = new AtomicInteger(1);
        Callable<DseSession> connectionToDse = () -> {
            DseSession session = clusterConfig.build().connect();
            Metadata metadata = session.getCluster().getMetadata();

            Set<com.datastax.driver.core.Host> nodes = metadata.getAllHosts();
            LOGGER.debug("Nodes are: " + nodes);

            return session;
    };

        RetryConfig config = new RetryConfigBuilder()
                .retryOnAnyException()
                .withMaxNumberOfTries(maxNumberOfTries)
                .withDelayBetweenTries(delayBetweenTries, ChronoUnit.SECONDS)
                .withFixedBackoff()
                .build();
        return new CallExecutor<DseSession>(config)
                .afterFailedTry(s -> {
                    LOGGER.info("Attempt #{}/{} failed.. trying in {} seconds.", atomicCount.getAndIncrement(),
                            maxNumberOfTries,  delayBetweenTries); })
                .onFailure(s -> {
                    final String errorString = "Cannot connect to DSE after " + maxNumberOfTries + " attempts, exiting now.";
                    exitOnError(errorString, 500, null);
                })
                .onSuccess(s -> {
                    long timeElapsed = System.currentTimeMillis() - top;
                    LOGGER.info("Connection etablished to DSE Cluster in {} millis.", timeElapsed);
                })
                .execute(connectionToDse).getResult();
    }

    /**
     * The destroy() method handles cases where the application is
     * shutdown via Spring and ensures any TCP connections,
     * thread pools, etc... to our cluster are freed up.
     */
    @PreDestroy
    public void destroy() {
        DseCluster dseCluster = dseSession.getCluster();
        LOGGER.info("Closing cluster connection: " + dseClusterName);
        dseCluster.close();
    }

    /**
     * The exitOnError() method handles cases of abrupt application
     * exit BEFORE any cluster connection has been established and
     * allows the application to fully exit as compared to hanging in
     * a "strange" state with incomplete cluster configuration.
     * @param errorString
     * @param status
     * @param e
     */
    private void exitOnError(String errorString, Integer status, Exception e) {
        LOGGER.error(errorString, e);
        System.exit(status);
    }

    @Bean
    public MappingManager initializeMappingManager(DseSession session) {
        return new MappingManager(session);
    }

    @Bean
    public KillrVideoTraversalSource initialGraphTraversalSource(DseSession session) {
        return DseGraph.traversal(session, KillrVideoTraversalSource.class);
    }
    
    /**
     * Retrieve server name from ETCD and update the contact points.
     *
     * @param clusterConfig
     *      current configuration
     */
    private void populateContactPoints(Builder clusterConfig)  {
        LOGGER.info(" + Reading node addresses from ETCD.");
        List<InetSocketAddress> clusterNodeAdresses = 
                    etcdDao.read("/killrvideo/services/cassandra", true).stream()
                    .map(this::asSocketInetAdress)
                    .filter(node -> node.isPresent())
                    .map(node -> node.get())
                    .collect(Collectors.toList());            
        clusterConfig.withPort(clusterNodeAdresses.get(0).getPort());
        clusterNodeAdresses.stream()
                           .map(address -> address.getHostName())
                           .forEach(clusterConfig::addContactPoint);

        clusterConfig.withLoadBalancingPolicy(
                DCAwareRoundRobinPolicy.builder()
                        .withLocalDc("AWS")
                        .withUsedHostsPerRemoteDc(3)
                        .allowRemoteDCsForLocalConsistencyLevel()
                        .build());

        clusterConfig.withSpeculativeExecutionPolicy(
                new ConstantSpeculativeExecutionPolicy(
                        500,20
                )
        ).build();
    }

    /**
     * If SSL is enabled use the supplied CA cert file to create
     * an SSL context and use to configure our cluster.
     *
     * @param clusterConfig
     *      current configuration
     */
    private void populateSSL(Builder clusterConfig) {
        try {
            FileInputStream fis = new FileInputStream(sslCACertFileLocation);
            X509Certificate caCert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new BufferedInputStream(fis));

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setCertificateEntry(Integer.toString(1), caCert);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(tmf)
                    .build();

            clusterConfig.withSSL(new RemoteEndpointAwareNettySSLOptions(sslContext));

        } catch (FileNotFoundException fne) {
            final String errorString = "SSL cert file not found. You must provide a valid certification file when using SSL encryption option.";
            exitOnError(errorString, 500, fne);

        } catch (CertificateException ce) {
            final String errorString = "Your CA certificate looks invalid. You must provide a valid certification file when using SSL encryption option.";
            exitOnError(errorString, 500, ce);

        } catch (Exception e) {
            final String errorString = "General exception in SSL configuration, I don't really know what's wrong. Take a look at the stack trace.";
            exitOnError(errorString, 500, e);
        }
    }
    
    /**
     * Check to see if we have username and password from the environment
     * This is here because we have a dual use scenario.  One for developers and others
     * who download KillrVideo and run within a local Docker container and the other
     * who might need (like us for example) to connect KillrVideo up to an external
     * cluster that requires authentication.
     */
    private void populateAuthentication(Builder clusterConfig, String username, String password) {
        AuthProvider cassandraAuthProvider = new DsePlainTextAuthProvider(username, password);
        clusterConfig.withAuthProvider(cassandraAuthProvider);
        String obfuscatedPassword = new String(new char[password.length()]).replace("\0", "*");
        LOGGER.info(" + Using supplied DSE username: '{}' and password: '{}' from environment variables",
                username, obfuscatedPassword);
    }
    
    private void populateGraphOptions(Builder clusterConfig) {
        GraphOptions go = new GraphOptions();
        go.setGraphName(graphRecommendationName);
        go.setReadTimeoutMillis(graphTimeout);
        go.setGraphSubProtocol(GraphProtocol.GRAPHSON_2_0);
        clusterConfig.withGraphOptions(go);
    }
    
    /**
     * Convert information in ETCD as real adress {@link InetSocketAddress} if possible.
     *
     * @param contactPoint
     *      network node adress information like hostname:port
     * @return
     *      java formatted inet adress
     */
    private Optional<InetSocketAddress> asSocketInetAdress(String contactPoint) {
        Optional<InetSocketAddress> target = Optional.empty();
        try {
            if (contactPoint != null && contactPoint.length() > 0) {
                String[] chunks = contactPoint.split(":");
                if (chunks.length == 2) {
                    LOGGER.info(" + Adding node '{}' to the Cassandra cluster definition", contactPoint);
                    return Optional.of(new InetSocketAddress(InetAddress.getByName(chunks[0]), Integer.parseInt(chunks[1])));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.warn(" + Cannot read contactPoint - "
                    + "Invalid Port Number, entry '" + contactPoint + "' will be ignored", e);
        } catch (UnknownHostException e) {
            LOGGER.warn(" + Cannot read contactPoint - "
                    + "Invalid Hostname, entry '" + contactPoint + "' will be ignored", e);
        }
        return target;
    }
}
