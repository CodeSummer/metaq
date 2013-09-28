/*
 * $Id$
 */
#ifndef _LIBMCLI_MESSAGE_PRODUCER_H__
#define _LIBMCLI_MESSAGE_PRODUCER_H__

#include <lwpr.h>
#include "Message.h"
#include "MessageSessionFactory.h"

namespace META
{
	class SendResult
	{
		bool m_success;
		int m_errcode;
		std::string m_errorMessage;

	public:

		SendResult(bool success, const char* errorMessage);

		bool isSuccess();

		void enableSuccess();

		void enableFailed(int code, const char* errorMessage);

		int getErrorCode();

		std::string getErrorMessage();
	};

	class MessageProducer : public LWPR::Object, virtual public Shutdownable
	{
	public:

		MessageProducer() {};

		virtual ~MessageProducer() {};

		/**
		 * ����topic���Ա�producer��zookeeper��ȡbroker�б����ӣ��ڷ�����Ϣǰ�����ȵ��ô˷���
		 */
		virtual void publish(const char* topic) = 0;

		/**
		 * ������Ϣ
		 */
		virtual SendResult sendMessage(Message& message) = 0;

		/**
		 * ������Ϣ
		 */
		virtual SendResult sendMessage(Message* message) = 0;
	};

	DECLAREVAR(MessageProducer);

	class SimpleMessageProducer : public MessageProducer
	{
	public:

		SimpleMessageProducer(MetaMessageSessionFactory* factory);
		~SimpleMessageProducer();

		virtual void publish(const char* topic);

		virtual SendResult sendMessage(Message& message);

		virtual SendResult sendMessage(Message* message);

		virtual void shutdown();

	private:
		MetaMessageSessionFactory_var m_vFactory;
		FetchConfigThread_var m_vFetchConfigThread;
		RemotingClientWrapper_var m_vRemotingClientWrapper;
		LWPR::AtomicInteger m_nSelectFactor;
		static int MAX_RETRY;
	};
}
#endif // end of _LIBMCLI_MESSAGE_PRODUCER_H__
