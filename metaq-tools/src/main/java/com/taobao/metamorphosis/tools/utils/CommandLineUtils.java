package com.taobao.metamorphosis.tools.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


/**
 * 
 * @author �޻�
 * @since 2011-8-23 ����11:01:10
 */

public class CommandLineUtils {

    public static CommandLine parseCmdLine(String[] args, Options options) {
        HelpFormatter hf = new HelpFormatter();
        try {
            return new PosixParser().parse(options, args);
        }
        catch (ParseException e) {
            hf.printHelp("className", options, true);
            throw new RuntimeException("Parse command line failed", e);
        }
    }
}
