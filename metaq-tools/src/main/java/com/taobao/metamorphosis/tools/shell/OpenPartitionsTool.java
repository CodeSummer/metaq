package com.taobao.metamorphosis.tools.shell;

import java.io.PrintStream;
import java.io.PrintWriter;

import javax.management.ObjectInstance;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.taobao.metamorphosis.tools.utils.CommandLineUtils;
import com.taobao.metamorphosis.tools.utils.JMXClient;


/**
 * <pre>
 * ���һ��topic�����з��� �Ĺرձ��
 * usage:
 *      OpenPartitionsTool -topic xxtopic
 *      OpenPartitionsTool -topic xxtopic -host 10.2.2.3
 *      OpenPartitionsTool -topic xxtopic -port 9999
 *      OpenPartitionsTool -topic xxtopic -host 10.2.2.3 -port 9999
 * </pre>
 * 
 * @author �޻�
 * @since 2011-8-23 ����5:44:21
 */

public class OpenPartitionsTool extends ShellTool {

    public OpenPartitionsTool(PrintStream out) {
        super(out);
    }


    public OpenPartitionsTool(PrintWriter writer) {
        super(writer);
    }


    public static void main(String[] args) throws Exception {
        new OpenPartitionsTool(System.out).doMain(args);
    }


    @Override
    public void doMain(String[] args) throws Exception {
        CommandLine commandLine = this.getCommandLine(args);

        String host = commandLine.getOptionValue("host", "127.0.0.1");
        int port = Integer.parseInt(commandLine.getOptionValue("port", "9999"));
        String topic = commandLine.getOptionValue("topic");

        JMXClient jmxClient = JMXClient.getJMXClient(host, port);
        this.println("connected to " + jmxClient.getAddressAsString());
        ObjectInstance metaConfigInstance = jmxClient.queryMBeanForOne(METACONFIG_NAME);

        if (metaConfigInstance != null) {
            jmxClient.invoke(metaConfigInstance.getObjectName(), "openPartitions", new Object[] { topic },
                new String[] { "java.lang.String" });
            jmxClient.close();
            this.println("invoke " + metaConfigInstance.getClassName() + "#openPartitions success");
        }
        else {
            this.println("û���ҵ� " + METACONFIG_NAME);
        }

    }


    private CommandLine getCommandLine(String[] args) {
        Option topicOption = new Option("topic", true, "topic");
        topicOption.setRequired(true);
        return CommandLineUtils.parseCmdLine(args, new Options().addOption(topicOption).addOption("host", true, "host")
            .addOption("port", true, "port"));
    }

}
