package com.taobao.metamorphosis.server.assembly;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ά���ͻ��˵���Ϣ���͵Ĺ�ϵ ���ÿͻ��˵�����ʱ����Ϊ�汾��Ϣ�����ͻ���Ӧ�����õ���Ϣ�ж���汾��ʱ���������������ϢΪ׼
 * 
 * @author pingwei
 * 
 */
public class MessageTypeManager {
	
	static final Log log = LogFactory.getLog(MessageTypeManager.class);

	private final class MessageTypeSet {
		final Set<String> messageTypes;
		final long version;

		public MessageTypeSet(Set<String> messageTypes, long version) {
			this.messageTypes = messageTypes;
			this.version = version;
		}

		public Set<String> getMessageTypes() {
			return messageTypes;
		}

		public long getVersion() {
			return version;
		}

	}

	Object obj = new Object();

	ConcurrentHashMap<String/* group */, ConcurrentHashMap<String/* topic */, MessageTypeSet>> consumerTypeMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, MessageTypeSet>>();
	ConcurrentHashMap<String/*group topic*/, Set<Integer>> hashCodeMap = new ConcurrentHashMap<String, Set<Integer>>();
	
	/**
	 * ����˶���Ϣ���ˣ�ͨ��hashcode�ķ�ʽ�Ƚϣ���ͻ�ĳ���ͨ���ͻ��˽��
	 * @param group
	 * @param topic
	 * @return
	 */
	public Set<Integer> getMessageTypeHash(String group, String topic){
		MessageTypeSet value = getMessageType(group, topic);
		if (value == null) {
			return null;
		}
		StringBuilder keyBuilder = new StringBuilder(group.length() + 1 + topic.length());
		keyBuilder.append(group).append(" ").append(topic);
		String key = keyBuilder.toString();
		Set<Integer> hashList = this.hashCodeMap.get(key);
		if(hashList == null){
			hashList = new HashSet<Integer>();
			for(String type : value.getMessageTypes()){
				hashList.add(type.hashCode());
			}
			this.hashCodeMap.put(key, hashList);
		}
		return hashList;
	}

	public Set<String> getMessageType(String group, String topic, long version) {
		MessageTypeSet value = getMessageType(group, topic);
		if (value == null) {
			log.info("group["+group+"],topic["+topic+"]'s messageType has not found");
			return null;
		}
		if (value.getVersion() < version) {//����汾�Ŵ��ڵ�ǰ��Ϣ����Ҫ�ͻ��˻㱨������
			log.info("group["+group+"],topic["+topic+"]'s messageType out of date, need new data");
			return null;
		}
		return value.getMessageTypes();
	}

	/**
	 * ������Ϣ���͹�ϵ��ִ�и��²������������ֳ���������1.����İ汾�Ŵ��ڵ�ǰ�İ汾�ţ�2.��һ������
	 * ���ؽ���Ǹ���֮�����µ���Ϣ�����б�
	 * @param group
	 * @param topic
	 * @param messageTypes
	 * @param version
	 */
	public Set<String> updateMessageType(String group, String topic, Set<String> messageTypes, long version) {
		MessageTypeSet value = getMessageType(group, topic);
		if (value != null && value.getVersion() >= version) {// ��������version�汾�ȵ�ǰ��С������
			return value.getMessageTypes();
		}
		MessageTypeSet typeValue = new MessageTypeSet(messageTypes, version);
		ConcurrentHashMap<String, MessageTypeSet> topicMap = this.consumerTypeMap.get(group);
		if (topicMap == null) {
			ConcurrentHashMap<String, MessageTypeSet> tmp = new ConcurrentHashMap<String, MessageTypeManager.MessageTypeSet>();
			topicMap = this.consumerTypeMap.putIfAbsent(group, tmp);
			if (topicMap == null) {
				topicMap = tmp;
			}
		}
		synchronized (obj) {//�������ĸ��£�����Ҫ�������
			log.info("server received new messageType, group[" + group + "],topic[" + topic + "], content["
					+ messageTypes +"]");
			MessageTypeSet oldValue = topicMap.putIfAbsent(topic, typeValue);
			if (oldValue != null && oldValue.getVersion() < version) {
				topicMap.put(topic, typeValue);
			}
		}
		//������޸�ɾ��hashCodeMap������
		StringBuilder keyBuilder = new StringBuilder(1 + group.length() + topic.length());
		keyBuilder.append(group).append(" ").append(topic);
		this.hashCodeMap.remove(keyBuilder.toString());
		return getMessageType(group, topic).getMessageTypes();
	}

	private MessageTypeSet getMessageType(String group, String topic) {
		ConcurrentHashMap<String, MessageTypeSet> topicMap = this.consumerTypeMap.get(group);
		if (topicMap == null) {
			return null;
		}
		MessageTypeSet value = topicMap.get(topic);
		return value;
	}
}
