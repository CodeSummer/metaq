package com.taobao.metamorphosis.client.consumer.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import com.taobao.metamorphosis.client.consumer.TopicPartitionRegInfo;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * ����mysql���ݿ��offset�洢��
 * 
 * @author boyan
 * @Date 2011-4-28
 * 
 */
// TODO ʵ���Զ�����
public class MysqlOffsetStorage implements OffsetStorage {

    public static final String DEFAULT_TABLE_NAME = "meta_topic_partition_group_offset";

    private DataSource dataSource;

    private String tableName = DEFAULT_TABLE_NAME;


    /**
     * offset����ı���
     * 
     * @return
     */
    public String getTableName() {
        return this.tableName;
    }


    /**
     * ���ñ�����Ĭ��Ϊmeta_topic_partiton_group_offset
     * 
     * @param tableName
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }


    public MysqlOffsetStorage(final DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }


    @Override
    public void commitOffset(final String group, final Collection<TopicPartitionRegInfo> infoList) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        final Connection conn = JDBCUtils.getConnection(this.dataSource);

        JDBCUtils.execute(conn, new JDBCUtils.ConnectionCallback() {
            @Override
            public Object doInConnection(final Connection conn) throws SQLException {
                final String updateSQL =
                        "update " + MysqlOffsetStorage.this.tableName
                                + " set offset=?,msg_id=? where topic=? and  partition=?  and group_id=?";
                final PreparedStatement preparedStatement = conn.prepareStatement(updateSQL);
                JDBCUtils.execute(preparedStatement, new JDBCUtils.PreparedStatementCallback() {
                    @Override
                    public Object doInPreparedStatement(final PreparedStatement pstmt) throws SQLException {
                        for (final TopicPartitionRegInfo info : infoList) {
                            long newOffset = -1;
                            long msgId = -1;
                            // ��������֤msgId��offsetһ��
                            synchronized (info) {
                                // ֻ�����б����
                                if (!info.isModified()) {
                                    continue;
                                }
                                newOffset = info.getOffset().get();
                                msgId = info.getMessageId();
                                // ������ϣ�����Ϊfalse
                                info.setModified(false);
                            }
                            pstmt.setLong(1, newOffset);
                            pstmt.setLong(2, msgId);
                            pstmt.setString(3, info.getTopic());
                            pstmt.setString(4, info.getPartition().toString());
                            pstmt.setString(5, group);
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                        return null;
                    }
                });
                return null;
            }
        });
    }


    @Override
    public void close() {
        this.dataSource = null;
    }


    @Override
    public void initOffset(final String topic, final String group, final Partition partition, final long offset) {
        final Connection conn = JDBCUtils.getConnection(this.dataSource);
        JDBCUtils.execute(conn, new JDBCUtils.ConnectionCallback() {
            @Override
            public Object doInConnection(final Connection conn) throws SQLException {
                final String insertSQL =
                        "insert into " + MysqlOffsetStorage.this.tableName
                                + " (topic,partition,group_id,offset,msg_id) values(?,?,?,?,?)";
                final PreparedStatement preparedStatement = conn.prepareStatement(insertSQL);
                JDBCUtils.execute(preparedStatement, new JDBCUtils.PreparedStatementCallback() {
                    @Override
                    public Object doInPreparedStatement(final PreparedStatement pstmt) throws SQLException {
                        pstmt.setString(1, topic);
                        pstmt.setString(2, partition.toString());
                        pstmt.setString(3, group);
                        pstmt.setLong(4, offset);
                        pstmt.setLong(5, -1L);
                        pstmt.executeUpdate();
                        return null;
                    }
                });
                return null;
            }
        });
    }


    @Override
    public TopicPartitionRegInfo load(final String topic, final String group, final Partition partition) {
        final Connection conn = JDBCUtils.getConnection(this.dataSource);
        return (TopicPartitionRegInfo) JDBCUtils.execute(conn, new JDBCUtils.ConnectionCallback() {
            @Override
            public Object doInConnection(final Connection conn) throws SQLException {
                final String selectSQL =
                        "select offset,msg_id from " + MysqlOffsetStorage.this.tableName
                                + " where topic=? and partition=? and group_id=?";
                final PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
                return JDBCUtils.execute(preparedStatement, new JDBCUtils.PreparedStatementCallback() {
                    @Override
                    public Object doInPreparedStatement(final PreparedStatement pstmt) throws SQLException {
                        pstmt.setString(1, topic);
                        pstmt.setString(2, partition.toString());
                        pstmt.setString(3, group);
                        final ResultSet rs = pstmt.executeQuery();
                        return JDBCUtils.execute(rs, new JDBCUtils.ResultSetCallback() {

                            @Override
                            public Object doInResultSet(final ResultSet rs) throws SQLException {
                                if (rs.next()) {
                                    final long offset = rs.getLong(1);
                                    final long msgId = rs.getLong(2);
                                    return new TopicPartitionRegInfo(topic, partition, offset, msgId);
                                }
                                else {
                                    return null;
                                }
                            }
                        });
                    }
                });

            }
        });

    }

}
