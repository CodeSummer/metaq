package com.taobao.metamorphosis.client.extension.producer;

import java.util.concurrent.TimeUnit;

import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.producer.SendResult;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.exception.MetaClientException;


/**
 * 
 * @author �޻�
 * @since 2011-8-9 ����6:12:01
 */

class OrderedMessageSender {
    private final OrderedMessageProducer producer;


    OrderedMessageSender(OrderedMessageProducer producer) {
        this.producer = producer;
    }


    SendResult sendMessage(Message message, long timeout, TimeUnit unit) throws MetaClientException,
            InterruptedException {
        int maxRecheck = 3;
        int check = 1;
        for (;;) {
            SelectPartitionResult result = this.trySelectPartition(message);

            // ���÷���������
            if (!result.isPartitionWritable()) {
                return this.producer.saveMessageToLocal(message, result.getSelectedPartition(), timeout, unit);
            }
            // ���÷�������
            else {
                int localMessageCount =
                        this.producer.getLocalMessageCount(message.getTopic(), result.getSelectedPartition());
                if (localMessageCount > 0) {
                    this.producer.tryRecoverMessage(message.getTopic(), result.getSelectedPartition());
                }

                if (localMessageCount <= 0) {
                    // ���ش����Ϣ����Ϊ��,������Ϣ���͵������
                    return this.producer.sendMessageToServer(message, timeout, unit, true);
                }
                else if (localMessageCount > 0 && localMessageCount <= 20) {
                    // ���ش����Ϣֻ����������,ͣ��һ�µȴ�������Ϣ���ָ�,`�ټ������״̬,
                    // �����maxRecheck�� ������Ϣ��û���ָ���,�汾�ز��˳�.
                    if (check >= maxRecheck) {
                        return this.producer.saveMessageToLocal(message, result.getSelectedPartition(), timeout, unit);
                    }
                    Thread.sleep(100L);

                }
                else {
                    // ���ش����Ϣ���кܶ�,����д����
                    return this.producer.saveMessageToLocal(message, result.getSelectedPartition(), timeout, unit);
                }

            }
            check++;
        }// end for

    }


    private SelectPartitionResult trySelectPartition(Message message) throws MetaClientException {
        SelectPartitionResult result = new SelectPartitionResult();
        try {
            Partition partition = this.producer.selectPartition(message);
            if (partition == null) {
                throw new MetaClientException("selected null partition");
            }
            result.setSelectedPartition(partition);
            result.setPartitionWritable(true);
        }
        catch (AvailablePartitionNumException e) {
            String msg = e.getMessage();
            String partitionStr = msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
            result.setSelectedPartition(new Partition(partitionStr));
            result.setPartitionWritable(false);
        }
        catch (MetaClientException e) {
            throw e;
        }
        return result;
    }

    private static class SelectPartitionResult {
        private boolean partitionWritable;
        private Partition selectedPartition;


        public boolean isPartitionWritable() {
            return this.partitionWritable;
        }


        public void setPartitionWritable(boolean partitionWritable) {
            this.partitionWritable = partitionWritable;
        }


        public Partition getSelectedPartition() {
            return this.selectedPartition;
        }


        public void setSelectedPartition(Partition selectedPartition) {
            this.selectedPartition = selectedPartition;
        }

    }

}
