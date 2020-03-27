package com.killrvideo.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * The DSE (DataStax Enterprise) Driver configuration.
 *
 * <p>
 * Driver options should be specified in the usual way, that is, through an <code>
 * application.conf</code> file accessible on the application classpath. See the
 * <a href="https://docs.datastax.com/en/developer/java-driver-dse/2.0/manual/core/configuration/">driver configuration</a>
 * section in the online docs for more information.
 *
 * <p>
 * To illustrate how to integrate the driver configuration with Spring, a few driver options should be configured through Spring's
 * own configuration mechanism:
 *
 * <ol>
 * <li><code>killrvideo.dse.contactPoints</code>: this property will override the driver's {@code
 *       datastax-java-driver.basic.contact-points} option; it will default to <code>127.0.0.1
 *       </code> if unspecified;
 * <li><code>killrvideo.dse.port</code>: this property will be combined with the previous one to create initial contact points; it will
 * default to <code>9042</code> if unspecified;
 * <li><code>driver.localdc</code>: this property will override the driver's {@code
 *       datastax-java-driver.basic.load-balancing-policy.local-datacenter} option; it has no default value and must be specified;
 * <li><code>driver.keyspace</code>: this property will override the driver's {@code
 *       datastax-java-driver.basic.session-keyspace} option; it has no default value and must be specified;
 * <li><code>driver.consistency</code>: this property will override the driver's {@code
 *       datastax-java-driver.basic.request.consistency}; it will default to <code>LOCAL_QUORUM
 *       </code> if unspecified;
 * <li><code>driver.pageSize</code>: this property will override the driver's {@code
 *       datastax-java-driver.basic.request.page-size}; it will default to <code>10</code> if unspecified;
 * <li><code>driver.username</code>: this property will override the driver's {@code
 *       datastax-java-driver.advanced.auth-provider.username} option; if unspecified, it will be assumed that no authentication
 * is required;
 * <li><code>driver.password</code>: this property will override the driver's {@code
 *       datastax-java-driver.advanced.auth-provider.password} option; if unspecified, it will be assumed that no authentication
 * is required;
 * </ol>
 *
 * The above properties should be typically declared in an {@code application.yml} file.
 * 
 * @author DataStax Developer Advocates team.
 * @param <RegularImmutableSet>
 */
@Configuration
@Profile("!unit-test & !integration-test")
public class DseDriverConfiguration {

//    /** Initialize dedicated connection to ETCD system. */
//    private static final Logger LOGGER = LoggerFactory.getLogger(DseDriverConfiguration.class);
//    
//    /** Execution Profile. */
//    public static final String EXECUTION_PROFILE_SEARCH = "search";
//  
//    @Value("#{'${killrvideo.dse.contactPoints}'.split(',')}")
//    private List<String> contactPoints;
//    
//    @Value("#{environment.KILLRVIDEO_DSE_CONTACT_POINTS}")
//    private Optional<String> contactPointsEnvironmentVar;
//   
//    @Value("${killrvideo.application.name:killrvideo}")
//    private String applicationName;
//    
//    @Value("${killrvideo.dse.port:9042}")
//    private int port;
//
//    @Value("${killrvideo.dse.keyspace:killrvideo}")
//    private String keyspaceName;
//
//    @Value("${killrvideo.dse.localdc:dc1}")
//    private String localDc;
//
//    @Value("${killrvideo.dse.username}")
//    public Optional<String> dseUsername;
//
//    @Value("${killrvideo.dse.password}")
//    public Optional<String> dsePassword;
//    
//    // --- Retries ---
//    
//    @Value("${killrvideo.dse.maxNumberOfTries:50}")
//    protected int maxNumberOfTries;
//    
//    @Value("${killrvideo.dse.delayBetweenTries:5}")
//    protected int delayBetweenTries;
//    
//    // --- Request ---
//    
//    @Value("${killrvideo.dse.consistency:LOCAL_QUORUM}")
//    protected String consistency;
//    
//    @Value("${killrvideo.dse.timeout:5 seconds}")
//    protected String timeout;
//    
//    // --- Search Request ---
//    
//    @Value("${killrvideo.dse.search.consistency:LOCAL_ONE}")
//    protected String searchConsistency;
//    
//    @Value("${killrvideo.dse.search.timeout:5 seconds}")
//    protected String searchTimeout;
//    
//    // -- Graph --
//    
//    @Value("${killrvideo.dse.graph.timeout:5 seconds}")
//    protected String graphTimeout;
//    
//    @Value("${killrvideo.dse.graph.recommendation.name:5}")
//    protected String graphName;
//    
//    // -- Apollo Specifics --
//    
//    @Value("${killrvideo.apollo.override-local-dse:false}")
//    private boolean connectApollo = false;
//    
//    @Value("${killrvideo.apollo.keyspace:killrvideo}")
//    private String apolloKeyspace;
//
//    @Value("${killrvideo.apollo.username:KVUser}")
//    private String apolloUserName;
//
//    @Value("${killrvideo.apollo.password:KVPassword}")
//    public String apolloPassword;
//    
//    @Value("${killrvideo.apollo.secure-connect-bundle:/tmp/creds.zip}")
//    public String apolloSecureConnectoBundleZipPath;
//    
//    /**
//     * Returns the keyspace to connect to. The keyspace specified here must exist.
//     *
//     * @return The {@linkplain CqlIdentifier keyspace} bean.
//     */
//    @Bean("killrvideo.keyspace")
//    public CqlIdentifier keyspace() {
//        return CqlIdentifier.fromCql(keyspaceName);
//    }
//
//    /**
//     * Returns a {@link ProgrammaticDriverConfigLoaderBuilder} to load driver options.
//     *
//     * <p>
//     * Use this loader if you need to programmatically override default values for any driver setting. In this example, we
//     * manually set the default consistency level to use, and, if a username and password are present, we define a basic
//     * authentication scheme using {@link DsePlainTextAuthProvider}.
//     *
//     * <p>
//     * Any value explicitly set through this loader will take precedence over values found in the driver's standard
//     * application.conf file.
//     *
//     * @return The {@link ProgrammaticDriverConfigLoaderBuilder} bean.
//     */
//    @Bean
//    public ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder() {
//        LOGGER.info("Initializing connectivity to Apollo or DDAC or DSE or OSS Cassandra");
//        ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder = DseDriverConfigLoader.programmaticBuilder();
//        
//        // Set BASICS
//        configLoaderBuilder.withString(DefaultDriverOption.REQUEST_TIMEOUT, timeout);
//        configLoaderBuilder.withString(DefaultDriverOption.REQUEST_CONSISTENCY, consistency);         
//        return configLoaderBuilder;
//    }
//
//    /**
//     * Returns a {@link DseSessionBuilder} that will configure sessions using the provided
//     * {@link ProgrammaticDriverConfigLoaderBuilder config loader builder}, as well as the contact points and local datacenter
//     * name found in application.yml, merged with other options found in application.conf.
//     *
//     * @param driverConfigLoaderBuilder
//     *            The {@link ProgrammaticDriverConfigLoaderBuilder} bean to use.
//     * @return The {@link DseSessionBuilder} bean.
//     */
//    @Bean
//    public DseSessionBuilder sessionBuilder(@NonNull ProgrammaticDriverConfigLoaderBuilder driverConfigLoaderBuilder) {
//        DseSessionBuilder sessionBuilder;
//         // Appollo ?
//        if (connectApollo) {
//            LOGGER.info("+ Connecting to Apollo using {}", apolloSecureConnectoBundleZipPath);
//            LOGGER.info("+ Apollo username {}", apolloUserName);
//            LOGGER.info("+ Apollo password {}", new String(apolloPassword).replaceAll(".?", "x"));
//            LOGGER.info("+ Apollo Keyspace {}", apolloKeyspace);
//            sessionBuilder = DseSession.builder()
//                    .withConfigLoader(driverConfigLoaderBuilder.build())
//                    .withCloudSecureConnectBundle(Paths.get(apolloSecureConnectoBundleZipPath))
//                    .withAuthCredentials(apolloUserName, apolloPassword)
//                    .withKeyspace(apolloKeyspace);
//        } else {
//            LOGGER.info("+ Connecting to Dse {}", apolloSecureConnectoBundleZipPath);
//            
//            // Set Search Consistency with a 'Profile'
//            driverConfigLoaderBuilder
//             .startProfile(EXECUTION_PROFILE_SEARCH)
//               .withString(DefaultDriverOption.REQUEST_CONSISTENCY, searchConsistency)
//               .withString(DefaultDriverOption.REQUEST_TIMEOUT, searchTimeout)
//             .endProfile();
//            
//            // Graph
//            driverConfigLoaderBuilder.withString(KillrvideoDriverOption.GRAPH_NAME, graphName);
//            driverConfigLoaderBuilder.withString(KillrvideoDriverOption.GRAPH_TIMEOUT, graphTimeout);
//            
//            sessionBuilder = new DseSessionBuilder().withConfigLoader(driverConfigLoaderBuilder.build());
//            if (!contactPointsEnvironmentVar.isEmpty() && !contactPointsEnvironmentVar.get().isBlank()) {
//                contactPoints = Arrays.asList(contactPointsEnvironmentVar.get().split(","));
//                LOGGER.info(" + Reading contactPoints from KILLRVIDEO_DSE_CONTACT_POINTS");
//            }
//            
//            // Authentication
//            if (!dseUsername.isEmpty() && !dsePassword.isEmpty()) {
//                sessionBuilder.withAuthCredentials(dseUsername.get(), dsePassword.get());
//            }
//            
//            LOGGER.info("+ Contact Points {}", contactPoints);
//            for (String contactPoint : contactPoints) {
//                InetSocketAddress address = InetSocketAddress.createUnresolved(contactPoint, port);
//                sessionBuilder = sessionBuilder.addContactPoint(address);
//            }
//            LOGGER.info("+ Port '{}'", port);
//            LOGGER.info("+ Local Data Center '{}'", localDc);
//            sessionBuilder.withLocalDatacenter(localDc);
//            LOGGER.info("+ Application name '{}'", applicationName);
//            sessionBuilder.withApplicationName(applicationName);
//            LOGGER.info("+ KeySpace name '{}'", keyspaceName);
//            sessionBuilder.withKeyspace(keyspaceName);
//        }
//        return sessionBuilder;
//    }
//
//    /**
//     * Returns the {@link DseSession} to use, configured with the provided {@link DseSessionBuilder session builder}. The returned
//     * session will be automatically connected to the given keyspace.
//     *
//     * @param sessionBuilder
//     *            The {@link DseSessionBuilder} bean to use.
//     * @param keyspace
//     *            The {@linkplain CqlIdentifier keyspace} bean to use.
//     * @return The {@link DseSession} bean.
//     */
//    @Bean
//    public DseSession session(@NonNull DseSessionBuilder dseSessionBuilder) {
//        
//        final AtomicInteger atomicCount = new AtomicInteger(1);
//        Callable<DseSession> connectionToDse = () -> {
//            return dseSessionBuilder.build();
//        };
//        
//        RetryConfig config = new RetryConfigBuilder()
//                .retryOnAnyException()
//                .withMaxNumberOfTries(maxNumberOfTries)
//                .withDelayBetweenTries(delayBetweenTries, ChronoUnit.SECONDS)
//                .withFixedBackoff()
//                .build();
//        long top = System.currentTimeMillis();
//        return new CallExecutor<DseSession>(config)
//                .afterFailedTry(s -> { 
//                    LOGGER.info("Attempt #{}/{} [KO] -> waiting {} seconds for Cluster to start", atomicCount.getAndIncrement(),
//                            maxNumberOfTries,  delayBetweenTries); })
//                .onFailure(s -> {
//                    LOGGER.error("Cannot connection to Cluster after {} attempts, exiting", maxNumberOfTries);
//                    System.err.println("Can not conenction to Cluster after " + maxNumberOfTries + " attempts, exiting");
//                    System.exit(500);
//                 })
//                .onSuccess(s -> {   
//                    long timeElapsed = System.currentTimeMillis() - top;
//                    LOGGER.info("[OK] Connection etablished to Cluster in {} millis.", timeElapsed);})
//                .execute(connectionToDse).getResult();
//    }
//    
//    /**
//     * Graph Traversal for suggested videos.
//     *
//     * @param session
//     *      current dse session.
//     * @return
//     *      traversal
//     */
//    @Bean
//    public KillrVideoTraversalSource initializeGraphTraversalSource(DseSession dseSession) {
//        //System.out.println(dseSession.getMetadata().getNodes().values().iterator().next().getExtras().get("DSE_WORKLOADS"));
//        //return new KillrVideoTraversalSource(DseGraph.g.getGraph());
//        return EmptyGraph.instance().traversal(KillrVideoTraversalSource.class);
//    }
//    
//    /** 
//     * Active workloads on DSE Nodes. 
//     * Each node can have different workloads, as such we would need to return a Map<nodeId, dseWorkloads>
//     * FOR KILLRVIDEO we assume all nodes have same Workloads as such we use the first.
//     */
//    @Bean
//    @SuppressWarnings("unchecked")
//    public Set< DseWorkload> dseWorkloads(DseSession dseSession) {
//        return ((Set<String>) dseSession.getMetadata().getNodes()  // Access Nodes
//                  .values().iterator().next()               // Pick First Node
//                  .getExtras().get("DSE_WORKLOADS"))        // Set <String>
//                  .stream().map(DseWorkload::valueOf)       // Valid and convert to enum
//                  .collect(Collectors.toSet());
//    }
//    
//    public static enum DseWorkload { Graph, Search, Cassandra, Analytics, SearchAnalytics };
//
    
}
