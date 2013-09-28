/*
 * $Id: Object.h 3 2011-08-19 02:25:45Z  $
 */
#ifndef LWPR_OBJECT_H__
#define LWPR_OBJECT_H__
#include "LWPRType.h"
#include "AtomicInteger.h"

namespace LWPR
{
	class Object
	{
	public:
		/**
		 * �������ø�����1
		 */
		static Object* Duplicate(Object *ref);
		/**
		 * �������ø�����1������Ϊ0ʱ���ͷŶ���
		 */
		static void Release(Object *ref);

		void IncRef();
		void DecRef();

	protected:
		Object() : m_nRefCount(1) {}
		virtual ~Object();

	private:
		AtomicInteger m_nRefCount;
	};
};

#endif // end of LWPR_OBJECT_H__
