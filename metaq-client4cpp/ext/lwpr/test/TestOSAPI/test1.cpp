/*
 * $Id: test1.cpp 3 2011-08-19 02:25:45Z  $
 */
/*
 * ���Բ���ϵͳAPI
 */
#include <iostream>
#include <stdio.h>
#include <unistd.h>

using namespace std;

int main(int argc, char *argv[])
{
	int a = alarm(30);
	cout << "a = " << a << endl;

	sleep(5);

	int b = alarm(90);
	cout << "b = " << b << endl;

	sleep(1000000);
	return 0;
}

