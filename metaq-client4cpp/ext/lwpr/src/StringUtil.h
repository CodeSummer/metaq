/*
 * $Id: StringUtil.h 3 2011-08-19 02:25:45Z  $
 */
#ifndef LWPR_STRING_UTIL_H__
#define LWPR_STRING_UTIL_H__
#include "LWPRType.h"
#include "Buffer.h"
#include <stdio.h>
#include <stdarg.h>
#include <vector>

namespace LWPR
{
	class StringUtil
	{
	public:
		/**
		 * ȥ��ǰ��ո��Ʊ�������з�
		 */
		static char* TrimAll(char *str);
		static char* TrimAll(char *buf, int num);

		/**
		 * ȥ��ǰ������, Ȼ��ȥ��ǰ��ո��Ʊ�������з�
		 */
		static char* TrimQuotationChar(char *str);

		/**
		 * �Ƿ��ǿհ��ַ����ո��Ʊ�������з���
		 */
		static bool IsBlankChar(char ch);

		static std::string& ToUpper(std::string& str);

		static std::string& ToLower(std::string& str);

		/**
		 * ��һ���ַ��������в����ַ�ch�״γ��ֵ�λ��
		 */
		static int Find(const char* buf, int num, char ch);

		static void VForm(Buffer& buf, const char* format, va_list args);

		static void SplitString(const std::string& src, char delimiter, std::vector< std::string >& vs);

		/**
		 * ����ת��
		 */
		static std::string IntToStr(int num);

		static void StringLinesToVector(const char* str, std::vector<std::string>& vct);
	};
};

#endif // end of LWPR_STRING_UTIL_H__
