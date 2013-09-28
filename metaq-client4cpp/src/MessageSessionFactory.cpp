/*
 * $Id$
 */
#include "MessageSessionFactory.h"
#include "MessageProducer.h"
#include "FetchConfigThread.h"
#include <assert.h>

namespace META
{
	MetaMessageSessionFactory::MetaMessageSessionFactory(MetaClientConfig_ptr config /*= NULL*/)
	{
		// ��������
		if(config)
		{
			m_vMetaClientConfig = MetaClientConfig_var::Duplicate(config);
		}
		else
		{
			m_vMetaClientConfig = new MetaClientConfig();
		}

		// ��ʼ������
		m_vMetaClientConfig->ConfigInit();

		// ��ʼ����־ģ��
		LWPR::LOGGER_OPTION_T loggerOption;
		loggerOption.bPrintConsole = false;
		loggerOption.bPrintFile = true;
		loggerOption.bPrintWhere = true;
		loggerOption.nLogLevel = m_vMetaClientConfig->getLogLevel();
		loggerOption.strLogFileName = "libmcli.log";
		logger->Initialize(loggerOption);

		// ����ͨ��ģ��
		m_vRemotingClientWrapper = new RemotingClientWrapper();

		// �������û�ȡ�߳�
		m_vFetchConfigThread = new FetchConfigThread(m_vMetaClientConfig);
		m_vFetchConfigThread->Start();
	}

	MetaMessageSessionFactory::~MetaMessageSessionFactory()
	{
		DEBUG_FUNCTION();

		shutdown();

		m_vFetchConfigThread->Join();
	}

	META::MessageProducer_ptr MetaMessageSessionFactory::createProducer()
	{
		DEBUG_FUNCTION();

		if(hasShutdown()) return NULL;

		MessageProducer_var producer = new SimpleMessageProducer(this);

		addChild(producer);

		return producer.retn();
	}

	void MetaMessageSessionFactory::shutdown()
	{
		DEBUG_FUNCTION();

		if(hasShutdown()) return;

		Shutdownable::shutdown();

		{
			LWPR::Synchronized syn(m_Mutex);

			for(SessionList::iterator it = m_SessionList.begin();
			    it != m_SessionList.end(); it++)
			{
				(*it)->shutdown();
				(*it)->DecRef();
			}

			m_SessionList.clear();
		}

		m_vFetchConfigThread->shutdown();
	}

	void MetaMessageSessionFactory::addChild(MessageProducer_ptr child)
	{
		DEBUG_FUNCTION();

		assert(NULL != child);

		LWPR::Synchronized syn(m_Mutex);
		m_SessionList.push_back(MessageProducer_var::Duplicate(child));
	}

	void MetaMessageSessionFactory::removeChild(MessageProducer_ptr child)
	{
		DEBUG_FUNCTION();

		assert(NULL != child);

		LWPR::Synchronized syn(m_Mutex);

		for(SessionList::iterator it = m_SessionList.begin();
		    it != m_SessionList.end();)
		{
			if(child == (*it))
			{
				child->DecRef();
				it = m_SessionList.erase(it);
			}
			else
			{
				it++;
			}
		}
	}

	META::FetchConfigThread_ptr MetaMessageSessionFactory::getFetchConfigThread()
	{
		return FetchConfigThread_var::Duplicate(m_vFetchConfigThread);
	}

	META::RemotingClientWrapper_ptr MetaMessageSessionFactory::getRemotingClientWrapper()
	{
		return RemotingClientWrapper_var::Duplicate(m_vRemotingClientWrapper);
	}
}
