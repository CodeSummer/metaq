package com.taobao.metamorphosis.network;

/**
 * ��Ӧ״̬�룬��ѭhttp����
 * 
 * @author boyan
 * @Date 2011-4-21
 * 
 */
public class HttpStatus {
    public static final int BadRequest = 400;
    public static final int NotFound = 404;
    public static final int Forbidden = 403;
    public static final int Unauthorized = 401;

    public static final int InternalServerError = 500;
    public static final int ServiceUnavilable = 503;
    public static final int GatewayTimeout = 504;

    public static final int Success = 200;

    public static final int Moved = 301;
    
    public static final int Continue= 100;//����������metaq2.0��Э��֧����Ϣ���͹��ˣ�������Ҫ�ͻ��˻㱨��Ҫ�����ݣ����û�л㱨����ȡ���ݻ�����������
}
