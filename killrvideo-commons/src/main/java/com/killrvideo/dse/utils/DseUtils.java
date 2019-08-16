package com.killrvideo.dse.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.dse.driver.api.core.DseSession;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.servererrors.InvalidQueryException;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Utility class for DSE.
 *
 * @author DataStax Developer Advocates Team
 */
public class DseUtils {
    
    /** Internal logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DseUtils.class);
    private static final String UTF8_ENCODING         = "UTF-8";
    private static final String NEW_LINE              = System.getProperty("line.separator");
    private static final long   INT_SINCE_UUID_EPOCH  = 0x01b21dd213814000L;
    
    public static long getTimeFromUUID(UUID uuid) {
      return (uuid.timestamp() - INT_SINCE_UUID_EPOCH) / 10000;
    }
    
    /**
     * Helper to create a KeySpace.
     *
     * @param keyspacename
     *      target keyspaceName
     */
    public static void createKeySpaceSimpleStrategy(DseSession dseSession, String keyspacename, int replicationFactor) {
        dseSession.execute(SchemaBuilder.createKeyspace(keyspacename)
                  .ifNotExists()
                  .withSimpleStrategy(replicationFactor)
                  .build());
        useKeySpace(dseSession, keyspacename);
    }
    
    public static boolean isTableEmpty(DseSession dseSession, CqlIdentifier keyspace, CqlIdentifier tablename) {
        return 0 == dseSession.execute(QueryBuilder.selectFrom(keyspace, tablename).all().build()).getAvailableWithoutFetching();
    }
    
    public static void useKeySpace(DseSession dseSession, String keyspacename) {
        dseSession.execute("USE " + keyspacename);
    }
    
    public static void dropKeyspace(DseSession dseSession, String keyspacename) {
        dseSession.executeAsync(SchemaBuilder.dropKeyspace(keyspacename).ifExists().build());
    }
    
    public static void truncateTable(DseSession dseSession, CqlIdentifier keyspace, CqlIdentifier tableName) {
        dseSession.execute(QueryBuilder.truncate(keyspace, tableName).build());
    }
    
    /**
     * Allows to execute a CQL File.
     *
     * @param dseSession
     *      current dse Session
     * @param fileName
     *      cql file name to execute
     * @throws FileNotFoundException
     *      cql file has not been found.
     */
    public static void executeCQLFile(DseSession dseSession, String fileName)
    throws FileNotFoundException {
        long top = System.currentTimeMillis();
        LOGGER.info("Processing file: " + fileName);
        Arrays.stream(loadFileAsString(fileName).split(";")).forEach(statement -> {
            String query = statement.replaceAll(NEW_LINE, "").trim();
            try {
                if (query.length() > 0) {
                    dseSession.execute(query);
                    LOGGER.info(" + Executed. " + query);
                }
            } catch (InvalidQueryException e) {
                LOGGER.warn(" + Query Ignore. " + query, e);
            }
        });
        LOGGER.info("Execution done in {} millis.", System.currentTimeMillis() - top);
    }
    
    /**
     * Utils method to load a file as String.
     *
     * @param fileName
     *            target file Name.
     * @return target file content as String
     * @throws FileNotFoundException 
     */
    private static String loadFileAsString(String fileName)
    throws FileNotFoundException {
        InputStream in = DseUtils.class.getResourceAsStream(fileName);
        if (in == null) {
            // Fetch absolute classloader path
            in =  DseUtils.class.getClassLoader().getResourceAsStream(fileName);
        }
        if (in == null) {
            // Thread
            in =  Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        }
        if (in == null) {
            throw new FileNotFoundException("Cannot load file " + fileName + " please check");
        }
        Scanner currentScan = null;
        StringBuilder strBuilder = new StringBuilder();
        try {
            currentScan = new Scanner(in, UTF8_ENCODING);
            while (currentScan.hasNextLine()) {
                strBuilder.append(currentScan.nextLine());
                strBuilder.append(NEW_LINE);
            }
        } finally {
            if (currentScan != null) {
                currentScan.close();
            }
        }
        return strBuilder.toString();
    }
    
    /**
     * From Future<ResultSet> to completableFuture<ResultSet>, also useful for 
     * 
     * @param listenableFuture
     * @return
     */
    public static <T> CompletableFuture<T> buildCompletableFuture(final ListenableFuture<T> listenableFuture) {
        CompletableFuture<T> completable = new CompletableFuture<T>();
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            public void onSuccess(T result)    { completable.complete(result); }
            public void onFailure(Throwable t) { completable.completeExceptionally(t);}
        });
        return completable;
    }
}
