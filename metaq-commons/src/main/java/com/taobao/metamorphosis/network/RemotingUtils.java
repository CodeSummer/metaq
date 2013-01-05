package com.taobao.metamorphosis.network;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.gecko.core.command.ResponseCommand;
import com.taobao.gecko.service.Connection;
import com.taobao.gecko.service.exception.NotifyRemotingException;


public class RemotingUtils {
    static final Log log = LogFactory.getLog(RemotingUtils.class);


    public static void response(final Connection conn, final ResponseCommand response) {
        if (response == null || conn == null) {
            return;
        }
        try {
            conn.response(response);
        }
        catch (final NotifyRemotingException e) {
            log.error("������Ӧʧ��", e);
        }
    }


    public static String getLocalAddress() throws Exception {
        // ��������������һ���ǻ�·ip��ַ������
        final Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
        InetAddress ipv6Address = null;
        while (enumeration.hasMoreElements()) {
            final NetworkInterface networkInterface = enumeration.nextElement();
            final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
            while (en.hasMoreElements()) {
                final InetAddress address = en.nextElement();
                if (!address.isLoopbackAddress()) {
                    if (address instanceof Inet6Address) {
                        ipv6Address = address;
                    }
                    else {
                        // ����ʹ��ipv4
                        return normalizeHostAddress(address);
                    }
                }

            }

        }
        // û��ipv4����ʹ��ipv6
        if (ipv6Address != null) {
            return normalizeHostAddress(ipv6Address);
        }
        final InetAddress localHost = InetAddress.getLocalHost();
        return normalizeHostAddress(localHost);
    }


    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        }
        else {
            return localHost.getHostAddress();
        }
    }

}
