/*
 * $Id: test1.cpp 3 2011-08-19 02:25:45Z  $
 */
/*
 * ����LWPR::Socket���Ƿ�����������
 * �䵱Server
 */
#include "Socket.h"
#include <iostream>
#include <stdio.h>

#include <sys/select.h>
#include <sys/time.h>
#include <unistd.h>

using namespace std;

#define LISTEN_PORT		10086

int main(int argc, char *argv[])
{
	LWPR::UINT16 port = LISTEN_PORT;
	LWPR::SOCKET_FD_T nLinstenFd = LWPR::Socket::CreateServerUDPSocket(port);
	if(nLinstenFd == -1)
	{
		cout << "CreateServerUDPSocket Failed." << endl;
		return -1;
	}

	while(LWPR::Socket::IsSocketReadable(nLinstenFd) == LWPR::SOCKET_RET_OK)
	{
		cout << nLinstenFd
		     << " become readable." << endl;

		LWPR::Socket::ClearUDPSocket(nLinstenFd);
	}
	return 0;
}




