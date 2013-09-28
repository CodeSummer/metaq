/*
 * $Id: Mutex.h 3 2011-08-19 02:25:45Z  $
 */
#ifndef LWPR_MUTEX_H__
#define LWPR_MUTEX_H__
#include "LWPRType.h"
#include "Exception.h"

namespace LWPR
{

	typedef pthread_mutex_t  PTHREAD_MUTEX_T;
	typedef pthread_mutexattr_t  PTHREAD_MUTEXATTR_T;

	class Mutex
	{
	public:

		/**
		 * ���캯��
		 */
		Mutex();

		/**
		 * ��������
		 */
		~Mutex();

		/**
		 * ����
		 */
		void Lock();

		/**
		 * ����������
		 * ��������ɹ�����true�����򷵻�false
		 */
		bool TryLock();

		/**
		 * �ͷ���
		 */
		void Unlock();

		/**
		 * ��ȡ����Ϊ��������׼��
		 */
		PTHREAD_MUTEX_T* GetMutexRef();

	private:

		PTHREAD_MUTEX_T m_mutex;
	};

	class RecursiveMutex
	{
	public:

		/**
		 * ���캯��
		 */
		RecursiveMutex();

		/**
		 * ��������
		 */
		~RecursiveMutex();

		/**
		 * ����
		 */
		void Lock();

		/**
		 * ����������
		 * ��������ɹ�����true�����򷵻�false
		 */
		bool TryLock();

		/**
		 * �ͷ���
		 */
		void Unlock();

		/**
		 * ��ȡ����Ϊ��������׼��
		 */
		PTHREAD_MUTEX_T* GetMutexRef();

	private:
		PTHREAD_MUTEXATTR_T m_mutexattr;
		PTHREAD_MUTEX_T m_mutex;
	};
};

#endif // end of LWPR_MUTEX_H__
