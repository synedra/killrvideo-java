package com.killrvideo.conf;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.dse.driver.api.core.DseSessionBuilder;
import com.datastax.dse.driver.api.core.config.DseDriverConfigLoader;
import com.datastax.dse.driver.internal.core.auth.DsePlainTextAuthProvider;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.killrvideo.dse.graph.KillrVideoTraversalSource;

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
 */
@Configuration
@Profile("!unit-test & !integration-test")
public class DseDriverConfiguration {

    /** Initialize dedicated connection to ETCD system. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DseDriverConfiguration.class);
  
    @Value("#{'${killrvideo.dse.contactPoints}'.split(',')}")
    private List<String> contactPoints;
    
    @Value("#{environment.KILLRVIDEO_DSE_CONTACT_POINTS}")
    private Optional<String> contactPointsEnvironmentVar;
   
    @Value("${killrvideo.dse.port:9042}")
    private int port;

    @Value("${killrvideo.dse.keyspace:killrvideo}")
    private String keyspaceName;

    @Value("${killrvideo.dse.localdc:dc1}")
    private String localDc;

    @Value("${killrvideo.dse.username}")
    public Optional<String> dseUsername;

    @Value("${killrvideo.dse.password}")
    public Optional<String> dsePassword;

    @Value("${killrvideo.dse.consistency:LOCAL_QUORUM}")
    protected String consistency;

    /**
     * Returns the keyspace to connect to. The keyspace specified here must exist.
     *
     * @return The {@linkplain CqlIdentifier keyspace} bean.
     */
    @Bean("killrvideo.keyspace")
    public CqlIdentifier keyspace() {
        return CqlIdentifier.fromCql(keyspaceName);
    }

    /**
     * Returns a {@link ProgrammaticDriverConfigLoaderBuilder} to load driver options.
     *
     * <p>
     * Use this loader if you need to programmatically override default values for any driver setting. In this example, we
     * manually set the default consistency level to use, and, if a username and password are present, we define a basic
     * authentication scheme using {@link DsePlainTextAuthProvider}.
     *
     * <p>
     * Any value explicitly set through this loader will take precedence over values found in the driver's standard
     * application.conf file.
     *
     * @return The {@link ProgrammaticDriverConfigLoaderBuilder} bean.
     */
    @Bean
    public ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder() {
        LOGGER.info("Initializing Connection to DSE Cluster");
        ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder = DseDriverConfigLoader
                .programmaticBuilder()
                .withString(DefaultDriverOption.REQUEST_CONSISTENCY, consistency);
        if (!dseUsername.isEmpty() && !dsePassword.isEmpty()) {
            configLoaderBuilder = configLoaderBuilder
                    .withString(DefaultDriverOption.AUTH_PROVIDER_CLASS, DsePlainTextAuthProvider.class.getName())
                    .withString(DefaultDriverOption.AUTH_PROVIDER_USER_NAME, dseUsername.get())
                    .withString(DefaultDriverOption.AUTH_PROVIDER_PASSWORD, dsePassword.get());
        }
        return configLoaderBuilder;
    }

    /**
     * Returns a {@link DseSessionBuilder} that will configure sessions using the provided
     * {@link ProgrammaticDriverConfigLoaderBuilder config loader builder}, as well as the contact points and local datacenter
     * name found in application.yml, merged with other options found in application.conf.
     *
     * @param driverConfigLoaderBuilder
     *            The {@link ProgrammaticDriverConfigLoaderBuilder} bean to use.
     * @return The {@link DseSessionBuilder} bean.
     */
    @Bean
    public DseSessionBuilder sessionBuilder(@NonNull ProgrammaticDriverConfigLoaderBuilder driverConfigLoaderBuilder) {
        DseSessionBuilder sessionBuilder = new DseSessionBuilder().withConfigLoader(driverConfigLoaderBuilder.build());
        if (!contactPointsEnvironmentVar.isEmpty() && !contactPointsEnvironmentVar.get().isBlank()) {
            contactPoints = Arrays.asList(contactPointsEnvironmentVar.get().split(","));
            LOGGER.info(" + Reading contactPoints from KILLRVIDEO_DSE_CONTACT_POINTS");
        }
        LOGGER.info("+ Contact Points {}", contactPoints);
        for (String contactPoint : contactPoints) {
            InetSocketAddress address = InetSocketAddress.createUnresolved(contactPoint, port);
            sessionBuilder = sessionBuilder.addContactPoint(address);
        }
        LOGGER.info("+ Local Data Center '{}'", localDc);
        return sessionBuilder.withLocalDatacenter(localDc);
    }

    /**
     * Returns the {@link DseSession} to use, configured with the provided {@link DseSessionBuilder session builder}. The returned
     * session will be automatically connected to the given keyspace.
     *
     * @param sessionBuilder
     *            The {@link DseSessionBuilder} bean to use.
     * @param keyspace
     *            The {@linkplain CqlIdentifier keyspace} bean to use.
     * @return The {@link DseSession} bean.
     */
    @Bean
    public DseSession session(@NonNull DseSessionBuilder dseSessionBuilder) {
        LOGGER.info("+ KeySpace '{}'", keyspaceName);
        DseSession session = dseSessionBuilder.withKeyspace(keyspaceName).build();
        LOGGER.info("[OK] Dse Session established on port: '{}'", port);
        return session;
    }
    
    /**
     * Graph Traversal for suggested videos.
     *
     * @param session
     *      current dse session.
     * @return
     *      traversal
     */
    @Bean
    public KillrVideoTraversalSource initializeGraphTraversalSource(DseSession dseSession) {
        return EmptyGraph.instance().traversal(KillrVideoTraversalSource.class);
        //return new KillrVideoTraversalSource(DseGraph.g.getGraph());
    }
}
