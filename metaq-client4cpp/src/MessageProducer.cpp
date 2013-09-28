/*
 * $Id$
 */
#include "MessageProducer.h"
#include <lwpr.h>

namespace META
{
	//-------------------------------------------------------------------------------
	// class SendResult
	//-------------------------------------------------------------------------------
	const int META_SENDRESULT_ERRCODE_OK = LWPR::SOCKET_INVOKE_OK;
	const int META_SENDRESULT_ERRCODE_CONNECT_FAILED = LWPR::SOCKET_INVOKE_CONNECT_FAILED;
	const int META_SENDRESULT_ERRCODE_SEND_DATA_FAILED = LWPR::SOCKET_INVOKE_SEND_DATA_FAILED;
	const int META_SENDRESULT_ERRCODE_SEND_DATA_TIMEOUT = LWPR::SOCKET_INVOKE_SEND_DATA_TIMEOUT;
	const int META_SENDRESULT_ERRCODE_RECEIVE_DATA_FAILED = LWPR::SOCKET_INVOKE_RECEIVE_DATA_FAILED;
	const int META_SENDRESULT_ERRCODE_RECEIVE_DATA_TIMEOUT = LWPR::SOCKET_INVOKE_RECEIVE_DATA_TIMEOUT;
	const int META_SENDRESULT_ERRCODE_POST_PROCESSING_FAILED = LWPR::SOCKET_INVOKE_POST_PROCESSING_FAILED;

	const int META_SENDRESULT_ERRCODE_NO_META_PARTITION = 0x00FFFF01;
	SendResult::SendResult(bool success, const char* errorMessage)
		: m_success(success)
		, m_errcode(META_SENDRESULT_ERRCODE_OK)
		, m_errorMessage(errorMessage)
	{
	}

	bool SendResult::isSuccess()
	{
		return m_success;
	}

	int SendResult::getErrorCode()
	{
		return m_errcode;
	}

	std::string SendResult::getErrorMessage()
	{
		return m_errorMessage;
	}

	void SendResult::enableSuccess()
	{
		m_success = true;
		m_errcode = META_SENDRESULT_ERRCODE_OK;
	}

	void SendResult::enableFailed(int code, const char* errorMessage)
	{
		assert(NULL != errorMessage);
		m_success = false;
		m_errcode = code;
		m_errorMessage = errorMessage;
	}

	//-------------------------------------------------------------------------------
	// class SimpleMessageProducer
	//-------------------------------------------------------------------------------

	int SimpleMessageProducer::MAX_RETRY = 3;

	SimpleMessageProducer::SimpleMessageProducer(MetaMessageSessionFactory* factory)
		: m_vFactory(META::MetaMessageSessionFactory_var::Duplicate(factory))
		, m_vFetchConfigThread(factory->getFetchConfigThread())
		, m_vRemotingClientWrapper(factory->getRemotingClientWrapper())
		, m_nSelectFactor(0)
	{
		DEBUG_FUNCTION();
		assert(factory != NULL);
	}

	SimpleMessageProducer::~SimpleMessageProducer()
	{
		DEBUG_FUNCTION();
	}

	void SimpleMessageProducer::publish(const char* topic)
	{
		DEBUG_FUNCTION();

		if(!topic)
		{
			logger->error(LTRACE, "topic is null");
			return;
		}

		m_vFetchConfigThread->publish(topic);
	}

	SendResult SimpleMessageProducer::sendMessage(Message& message)
	{
		DEBUG_FUNCTION();

		SendResult result(false, "");

		BrokerParttionVector bpv;

		m_vFetchConfigThread->GetBrokerParttionByTopic(message.getTopic().c_str(), bpv);

		if(!bpv.empty())
		{
			LWPR::Buffer bufRequest(1024);

			bool retry = true;
			for(int i = 0; i < MAX_RETRY && retry; i++)
			{
				LWPR::UINT32 index = m_nSelectFactor++;

				BrokerParttion bp = bpv[index % bpv.size()];

				message.encode(bp.partition, index, bufRequest);

				MetaResponseData responseData = {0};

				LWPR::SOCKET_INVOKE_RET_E ret = m_vRemotingClientWrapper->invokeToGroup(bp.broker.c_str(),
				                                (const char*)bufRequest.Inout(), bufRequest.Size(), index, 10, responseData);

				switch(ret)
				{
				case LWPR::SOCKET_INVOKE_OK://RPC: ���óɹ�
					// ��Ϣ�ɹ����ͳ�ȥ�����߷������޷���������
					if(responseData.repCode != 200)
					{
						retry = true;
						result.enableFailed(responseData.repCode, "META���������ش���");
					}
					else
					{
						retry = false;
						result.enableSuccess();
					}
					break;
				case LWPR::SOCKET_INVOKE_CONNECT_FAILED: //RPC: ��������ʧ��
				case LWPR::SOCKET_INVOKE_SEND_DATA_FAILED://RPC: ��������ʧ��
				case LWPR::SOCKET_INVOKE_SEND_DATA_TIMEOUT://RPC: �������ݳ�ʱ
					// ��Ϣδ���ͳ�ȥ
					retry = true;
					result.enableFailed(ret, LWPR::GetSocketInvokeRetDesc(ret));
					break;
				case LWPR::SOCKET_INVOKE_RECEIVE_DATA_FAILED://RPC: ��������ʧ��
				case LWPR::SOCKET_INVOKE_RECEIVE_DATA_TIMEOUT://RPC: �������ݳ�ʱ
				case LWPR::SOCKET_INVOKE_POST_PROCESSING_FAILED://RPC: �������ݴ���ʧ��
					// ��Ϣ���ͳ�ȥ�����ǽ��ս׶γ���������󣬻���Э�����
					retry = false;
					result.enableFailed(ret, LWPR::GetSocketInvokeRetDesc(ret));
					break;
				default:
					assert(0);
					break;
				}

				if(retry)
				{
					logger->warn(LTRACE, "���Ե� %d �Σ���META������ [%s] PARTTION = [%d] ����Ϣ����"
					             , i + 1, bp.broker.c_str(), bp.partition);
				}
			}
		}
		else
		{
			char buf[128] = {0};
			sprintf(buf, "û�е�ǰTOPIC[%s]��Ӧ�ķ���", message.getTopic().c_str());
			result.enableFailed(META_SENDRESULT_ERRCODE_NO_META_PARTITION, buf);
		}

		return result;
	}

	SendResult SimpleMessageProducer::sendMessage(Message* message)
	{
		DEBUG_FUNCTION();

		if(!message)
		{
			SendResult r(false, "message is null");
			logger->error(LTRACE, "message is null");
			return r;
		}

		return sendMessage(*message);
	}

	void SimpleMessageProducer::shutdown()
	{
		DEBUG_FUNCTION();

		if(hasShutdown()) return;

		Shutdownable::shutdown();

		m_vFactory->removeChild(this);

		m_vFactory = NULL;

		m_vFetchConfigThread = NULL;
	}
}
