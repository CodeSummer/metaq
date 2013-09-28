/*
 * $Id: test2.cpp 3 2011-08-19 02:25:45Z  $
 */
/*
 * ����LWPR::Mutex���Ƿ�����������
 */
#include "Thread.h"
#include "Synchronized.h"
#include <iostream>
#include <stdio.h>

using namespace std;

LWPR::Mutex g_Mutex;
int i = 0;

class ThreadTest : public LWPR::Thread
{
public:
	ThreadTest()
	{
		this->Start();
	}

	~ThreadTest()
	{

	}

	void Run()
	{
		while(true)
		{
			LWPR::Synchronized syn(g_Mutex);
			printf("�߳�[%ld] �������С����� %d\n", GetId(), i++);
			Sleep(0.1);
		}
	}

private:
};

int main(int argc, char *argv[])
{
	ThreadTest t[10];

	sleep(1000000000);
	return 0;
}
