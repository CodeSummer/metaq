package com.taobao.metamorphosis.client.consumer.storage;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.diamond.utils.JSONUtils;
import com.taobao.metamorphosis.client.consumer.TopicPartitionRegInfo;
import com.taobao.metamorphosis.cluster.Partition;


/**
 * ����offset�洢���洢�ڴ��̣�Ĭ�ϴ洢��$HOME/.meta_offsets�ļ���
 * 
 * @author boyan
 * @Date 2011-5-4
 * 
 */
public class LocalOffsetStorage implements OffsetStorage {
    private String filePath;

    static final Log log = LogFactory.getLog(LocalOffsetStorage.class);

    private final Map<String/* group */, List<TopicPartitionRegInfo>> groupInfoMap =
            new HashMap<String, List<TopicPartitionRegInfo>>();
    private final FileChannel channel;


    public LocalOffsetStorage() throws IOException {
        this(System.getProperty("user.home") + File.separator + ".meta_offsets");
    }


    public LocalOffsetStorage(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            this.loadGroupInfo(file);
        }
        else {
            file.createNewFile();
        }
        this.channel = new RandomAccessFile(file, "rw").getChannel();
    }


    private void loadGroupInfo(File file) {
        String line = null;
        BufferedReader reader = null;
        FileReader fileReader = null;
        StringBuilder jsonSB = new StringBuilder();
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                jsonSB.append(line);
            }
        }
        catch (IOException e) {
            log.error("��ȡ�ļ�" + file + "����", e);
        }
        finally {
            this.close(reader);
            this.close(fileReader);
        }
        try {
            // ��ֹ�ļ�����Ϊ��ʱ������json�����л��쳣,add by wuhua
            if (jsonSB.length() <= 0) {
                log.warn(file.getAbsolutePath() + "�ļ�����Ϊ��,��ʱδ���ص�offset��Ϣ,����ǵ�һ�η���������������");
                return;
            }

            Map<String/* group */, List<Map<String, Object>>> groupInfoStringMap =
                    (Map<String/* group */, List<Map<String, Object>>>) JSONUtils.deserializeObject(jsonSB.toString(),
                        ConcurrentHashMap.class);

            for (Map.Entry<String, List<Map<String, Object>>> entry1 : groupInfoStringMap.entrySet()) {
                final String group = entry1.getKey();
                final List<Map<String, Object>> infos = entry1.getValue();
                List<TopicPartitionRegInfo> infoList = new ArrayList<TopicPartitionRegInfo>();
                if (infos != null) {
                    for (Map<String, Object> infoMap : infos) {
                        final String topic = (String) infoMap.get("topic");
                        final long offset = Long.valueOf(String.valueOf(infoMap.get("offset")));
                        Map<String, Integer> partMap = (Map<String, Integer>) infoMap.get("partition");
                        infoList.add(new TopicPartitionRegInfo(topic, new Partition(partMap.get("brokerId"), partMap
                            .get("partition")), offset));
                    }
                }
                this.groupInfoMap.put(group, infoList);
            }
        }
        catch (Exception e) {
            log.error("�����л�jsonʧ��", e);
        }
    }


    private void close(Closeable closeable) {
        try {
            closeable.close();
        }
        catch (IOException e) {
            // ignore
        }
    }


    @Override
    public void close() {
        this.close(this.channel);
    }


    @Override
    public void commitOffset(String group, Collection<TopicPartitionRegInfo> infoList) {
        if (infoList == null || infoList.isEmpty()) {
            return;
        }
        this.groupInfoMap.put(group, (List<TopicPartitionRegInfo>) infoList);
        try {
            String json = JSONUtils.serializeObject(this.groupInfoMap);
            this.channel.position(0);
            final ByteBuffer buf = ByteBuffer.wrap(json.getBytes());
            while (buf.hasRemaining()) {
                this.channel.write(buf);
            }
            this.channel.truncate(this.channel.position());
        }
        catch (Exception e) {
            log.error("commitOffset failed ", e);
        }

    }


    @Override
    public void initOffset(String topic, String group, Partition partition, long offset) {
        // do nothing
    }


    @Override
    public TopicPartitionRegInfo load(String topic, String group, Partition partition) {
        Collection<TopicPartitionRegInfo> topicPartitionRegInfos = this.groupInfoMap.get(group);
        if (topicPartitionRegInfos == null || topicPartitionRegInfos.isEmpty()) {
            return null;
        }
        for (TopicPartitionRegInfo info : topicPartitionRegInfos) {
            if (info.getTopic().equals(topic) && info.getPartition().equals(partition)) {
                return info;
            }
        }
        return null;
    }

}
