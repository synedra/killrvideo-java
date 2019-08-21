package com.killrvideo.service.comment.dao;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createTable;
import static com.datastax.oss.driver.api.querybuilder.relation.Relation.column;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.killrvideo.dse.dao.DseSchema;

/**
 * Wrap request building in this utility class
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CommentDseDaoUtils implements DseSchema {
    
    /** Hide default constructor. */
    private CommentDseDaoUtils() {}
    
    public static SimpleStatement stmtFindCommentsForUser() {
        return selectFrom(TABLENAME_COMMENTS_BY_USER_)
                .column(COMMENTS_COLUMN_USERID).column(COMMENTS_COLUMN_COMMENTID)
                .column(COMMENTS_COLUMN_VIDEOID).column(COMMENTS_COLUMN_COMMENT)
                .function("toTimestamp", Selector.column(COMMENTS_COLUMN_COMMENTID)).as("comment_timestamp")
                .where(column(COMMENTS_COLUMN_USERID).isEqualTo(bindMarker(COMMENTS_COLUMN_USERID)))
                .build();
    }
    
    public static SimpleStatement stmtFindCommentsForUserWithCommentId() {
        return selectFrom(TABLENAME_COMMENTS_BY_VIDEO)
                .column(COMMENTS_COLUMN_USERID).column(COMMENTS_COLUMN_USERID)
                .column(COMMENTS_COLUMN_VIDEOID).column(COMMENTS_COLUMN_COMMENT)
                .column(COMMENTS_COLUMN_COMMENTID)
                .function("toTimestamp", Selector.column(COMMENTS_COLUMN_COMMENTID)).as("comment_timestamp")
                .where(column(COMMENTS_COLUMN_VIDEOID).isEqualTo(bindMarker(COMMENTS_COLUMN_VIDEOID)))
                .build();
    }
    
    public static SimpleStatement stmtFindCommentsForVideo() {
        return selectFrom(TABLENAME_COMMENTS_BY_VIDEO)
                .column(COMMENTS_COLUMN_USERID).column(COMMENTS_COLUMN_USERID)
                .column(COMMENTS_COLUMN_VIDEOID).column(COMMENTS_COLUMN_COMMENT)
                .column(COMMENTS_COLUMN_COMMENTID)
                .function("toTimestamp", Selector.column(COMMENTS_COLUMN_COMMENTID)).as("comment_timestamp")
                .where(column(COMMENTS_COLUMN_VIDEOID).isEqualTo(bindMarker(COMMENTS_COLUMN_VIDEOID)))
                .build();
    }
    
    public static SimpleStatement stmtFindCommentsForVideoWithCommentId() {
        return selectFrom(TABLENAME_COMMENTS_BY_VIDEO)
                .column(COMMENTS_COLUMN_USERID).column(COMMENTS_COLUMN_USERID)
                .column(COMMENTS_COLUMN_VIDEOID).column(COMMENTS_COLUMN_COMMENT)
                .column(COMMENTS_COLUMN_COMMENTID)
                .function("toTimestamp", Selector.column(COMMENTS_COLUMN_COMMENTID)).as("comment_timestamp")
                .where(column(COMMENTS_COLUMN_VIDEOID).isEqualTo(bindMarker(COMMENTS_COLUMN_VIDEOID)),
                       column(COMMENTS_COLUMN_COMMENTID).isLessThanOrEqualTo(bindMarker(COMMENTS_COLUMN_COMMENTID)))
                .build();
    }
    
    public static SimpleStatement stmtCreateTableCommentByUser(CqlIdentifier kspace) {
        return createTable(kspace, TABLENAME_COMMENTS_BY_USER_).ifNotExists()
                .withPartitionKey(COMMENTS_COLUMN_USERID, DataTypes.UUID)
                .withClusteringColumn(COMMENTS_COLUMN_COMMENTID, DataTypes.TIMEUUID)
                .withColumn(COMMENTS_COLUMN_COMMENT, DataTypes.TEXT)
                .withColumn(COMMENTS_COLUMN_VIDEOID, DataTypes.UUID)
                .withClusteringOrder(COMMENTS_COLUMN_COMMENTID, ClusteringOrder.DESC)
                .withComment("List comments on user page")
                .build();
    }
    
    public static SimpleStatement stmtCreateTableCommentByVideo(CqlIdentifier kspace) {
        return createTable(kspace, TABLENAME_COMMENTS_BY_VIDEO_).ifNotExists()
                .withPartitionKey(COMMENTS_COLUMN_VIDEOID, DataTypes.UUID)
                .withClusteringColumn(COMMENTS_COLUMN_COMMENTID, DataTypes.TIMEUUID)
                .withColumn(COMMENTS_COLUMN_COMMENT, DataTypes.TEXT)
                .withColumn(COMMENTS_COLUMN_USERID, DataTypes.UUID)
                .withClusteringOrder(COMMENTS_COLUMN_COMMENTID, ClusteringOrder.DESC)
                .withComment("List comments on user page")
                .build();
    }
    
    public static <T> BoundStatement bind(PreparedStatement preparedStatement, T entity, EntityHelper<T> entityHelper) {
        BoundStatementBuilder boundStatement = preparedStatement.boundStatementBuilder();
        entityHelper.set(entity, boundStatement, NullSavingStrategy.DO_NOT_SET);
        return boundStatement.build();
    }

}
