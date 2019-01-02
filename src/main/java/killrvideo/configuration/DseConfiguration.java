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
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.datastax.dse.driver.api.core.DseSession;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

import killrvideo.dao.EtcdDao;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

/**
 * Connectivity to DSE (cassandra, graph, search, analytics).
 *
 * @author DataStax evangelist team.
 */
@Configuration
public class DseConfiguration {

	/** Internal logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DseConfiguration.class);
    
//    @Inject DseSession dseSession;
    
    @Bean
    public DseSession initializeDSE() {
        long top = System.currentTimeMillis();
        LOGGER.info("Initializing connection to DSE");
        
//        DseSession session = DseSession.builder().forClusterConfig(
//            new File("./config.yaml").toURI().toURL()).build();
            
        return null;
    }

    /**
     * The destroy() method handles cases where the application is
     * shutdown via Spring and ensures any TCP connections,
     * thread pools, etc... to our cluster are freed up.
     */
    @PreDestroy
    public void destroy() {
//        if(dseSession != null)
//          dseSession.close();
//        dseSession = null;
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
}
