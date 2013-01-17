/*
 * $Id$
 */
#include "RemotingClientWrapper.h"

namespace META
{
	RemotingClientWrapper::RemotingClientWrapper()
	{
		DEBUG_FUNCTION();
	}

	RemotingClientWrapper::~RemotingClientWrapper()
	{
		DEBUG_FUNCTION();

		for(AddressSocketMap::iterator it = m_AddressSocketMap.begin();
		    it != m_AddressSocketMap.end(); it++)
		{
			logger->info(LTRACE, "�Ͽ���META������[%s]����������", it->first.c_str());
			LWPR::Socket::CloseSocket(it->second);
		}

		m_AddressSocketMap.clear();
	}


	LWPR::SOCKET_INVOKE_RET_E RemotingClientWrapper::invokeToGroup(const char* address, const char* requestData, LWPR::INT32 size, LWPR::UINT32 opaque, LWPR::INT32 timeout, MetaResponseData& responseData)
	{
		DEBUG_FUNCTION();

		assert(NULL != address);
		assert(NULL != requestData);

		LWPR::Buffer_var bufResponse = new LWPR::Buffer(1024);

		LWPR::Synchronized syn(m_Mutex);

		LWPR::SOCKET_FD_T fd = GetAndCreateConnection(address);
		if(fd == -1)
		{
			logger->error(LTRACE, "���ӷ�����[%s]ʧ��", address);
			return LWPR::SOCKET_INVOKE_CONNECT_FAILED;
		}

		LWPR::SOCKET_RET_TYPE_E result = LWPR::Socket::WriteSocket(fd, requestData, size, timeout);
		switch(result)
		{
		case LWPR::SOCKET_RET_CONNECT_FAILED:
		case LWPR::SOCKET_RET_FAILED:
		case LWPR::SOCKET_RET_FREE:
		case LWPR::SOCKET_RET_NOT_WRABLE:
			logger->error(LTRACE, "������������ʧ��[%s]", address);
			CloseConnection(address);
			return LWPR::SOCKET_INVOKE_SEND_DATA_FAILED;
		case LWPR::SOCKET_RET_TIMEOUT:
			logger->error(LTRACE, "�����������ݳ�ʱ[%s]", address);
			CloseConnection(address);
			return LWPR::SOCKET_INVOKE_SEND_DATA_TIMEOUT;
		case LWPR::SOCKET_RET_OK:
		default:
			break;
		}

		while(result == LWPR::SOCKET_RET_OK)
		{
			result = LWPR::Socket::ReadSocketAsPossible(fd, *bufResponse, timeout);
			if(result == LWPR::SOCKET_RET_OK)
			{
				std::string tmp = bufResponse->Inout();
				std::string::size_type pos = tmp.find("\r\n", 0);
				if(std::string::npos != pos)
				{
					// ����Ӧ��ͷ
					memset(bufResponse->Inout() + pos, 0, 2);

					logger->debug(LTRACE, "response header = [%s]", bufResponse->Inout());

					char headerItem[4][32];
					memset(headerItem, 0, sizeof(headerItem));
					int ret = sscanf(bufResponse->Inout(), "%s %s %s %s", headerItem[0]
					                 , headerItem[1]
					                 , headerItem[2]
					                 , headerItem[3]);
					if(4 != ret)
					{
						logger->error(LTRACE, "����Ӧ����ͷ���� sscanf return %d", ret);
						CloseConnection(address);
						return LWPR::SOCKET_INVOKE_POST_PROCESSING_FAILED;
					}

					responseData.repCode = atol(headerItem[1]);
					LWPR::INT64 length = atol(headerItem[2]);
					LWPR::INT64 repOpaque = atol(headerItem[3]);

					// У��opaqueЭ������к�
					if(opaque != (LWPR::UINT32)repOpaque)
					{
						logger->error(LTRACE, "Ӧ��Э�����к�������Э�����кŲ�ͬ request opaque = [%u] response opaque = [%u]"
						              , opaque, (LWPR::UINT32)repOpaque);
						CloseConnection(address);
						return LWPR::SOCKET_INVOKE_POST_PROCESSING_FAILED;
					}

					// ���Ӧ����û�������������������
					LWPR::INT32 remainDataSize = pos + 2 + length - bufResponse->Size();
					if(remainDataSize > 0)
					{
						result = LWPR::Socket::ReadSocket(fd, bufResponse->Inout() + bufResponse->Size()
						                                  , remainDataSize, timeout);
						if(result != LWPR::SOCKET_RET_OK)
						{
							logger->error(LTRACE, "��ʣ��Ӧ��������� ReadSocket return %d", result);
							CloseConnection(address);
							return LWPR::SOCKET_INVOKE_RECEIVE_DATA_FAILED;
						}
					}

					// ����Ӧ���壨��Ϊ������UTF-8������ַ�����ANSII�Ǽ��ݵģ�
					char bodyItem[3][32];
					memset(bodyItem, 0, sizeof(bodyItem));
					ret = sscanf(bufResponse->Inout() + pos + 2, "%s %s %s"
					             , bodyItem[0]
					             , bodyItem[1]
					             , bodyItem[2]);
					if(3 != ret)
					{
						logger->error(LTRACE, "����Ӧ���ͷ����� sscanf return %d", ret);
						CloseConnection(address);
						return LWPR::SOCKET_INVOKE_POST_PROCESSING_FAILED;
					}

					logger->debug(LTRACE, "response body = [%s]", bufResponse->Inout() + pos + 2);

					responseData.msgId = atol(bodyItem[0]);
					responseData.partitionId = atoi(bodyItem[1]);

					return LWPR::SOCKET_INVOKE_OK;
				}
				else
				{
					continue;
				}
			}
		}

		return LWPR::SOCKET_INVOKE_RECEIVE_DATA_FAILED;
	}

	LWPR::SOCKET_FD_T RemotingClientWrapper::GetAndCreateConnection(const char* address)
	{
		DEBUG_FUNCTION();

		assert(NULL != address);

		AddressSocketMap::iterator it = m_AddressSocketMap.find(address);
		if(m_AddressSocketMap.end() != it) return it->second;

		LWPR::SOCKET_FD_T fd = LWPR::Socket::ConnectRemoteHost(address);
		if(fd != LWPR::SOCKET_RET_FAILED)
		{
			logger->info(LTRACE, "������META������[%s]����������", address);
			m_AddressSocketMap[address] = fd;
		}

		return fd;
	}

	void RemotingClientWrapper::CloseConnection(const char* address)
	{
		DEBUG_FUNCTION();

		assert(NULL != address);

		AddressSocketMap::iterator it = m_AddressSocketMap.find(address);
		if(m_AddressSocketMap.end() != it)
		{
			LWPR::Socket::CloseSocket(it->second);
			m_AddressSocketMap.erase(it);
		}
	}
}
