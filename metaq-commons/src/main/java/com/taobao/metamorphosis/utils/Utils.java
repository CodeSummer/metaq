package com.taobao.metamorphosis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;


/**
 * @author �޻�
 * @since 2011-5-27 ����05:07:00
 */

public class Utils {

    public static Properties getResourceAsProperties(String resource, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = ResourceUtils.getResourceAsStream(resource);
        }
        catch (IOException e) {
            File file = new File(resource);
            if (!file.exists()) {
                throw e;
            }
            in = new FileInputStream(file);
        }

        Properties props = new Properties();
        try {
            Reader reader = new InputStreamReader(in, encoding);
            props.load(reader);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ignore) {
                // we have finished to use this InputStream, so just ignore it
                ignore.printStackTrace();
            }
        }

        return props;

    }


    public static File getResourceAsFile(String resource) throws IOException {
        try {
            return new File(ResourceUtils.getResourceURL(resource).getFile());
        }
        catch (IOException e) {
            return new File(resource);
        }
    }

    public abstract static class Action {
        public abstract void process(String line);


        public boolean isBreak() {
            return false;
        }
    }


    public static void processEachLine(String string, Action action) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(string));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                action.process(line);
                if (action.isBreak()) {
                    break;
                }
            }

        }
        finally {
            br.close();
            br = null;
        }
    }

}
