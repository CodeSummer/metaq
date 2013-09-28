package com.taobao.metamorphosis.server.store;

import java.io.File;


/**
 * ����һ��ʱ���ɾ������
 * 
 * @author boyan
 * @Date 2011-4-29
 * 
 */
public class DiscardDeletePolicy implements DeletePolicy {
    public static final String NAME = "delete";
    // �����ʱ�䣬��λ����
    protected long maxReservedTime;


    void setMaxReservedTime(final long maxReservedTime) {
        this.maxReservedTime = maxReservedTime;
    }


    long getMaxReservedTime() {
        return this.maxReservedTime;
    }


    /**
     * ɾ���ļ�
     */
    @Override
    public void process(final File file) {
        file.delete();
    }


    @Override
    public boolean canDelete(final File file, final long checkTimestamp) {
        return checkTimestamp - file.lastModified() > this.maxReservedTime;
    }


    @Override
    public void init(final String... values) {
        if (values[0].endsWith("m")) {
            // minutes
            final int minutes = this.getValue(values[0]);
            this.maxReservedTime = minutes * 60L * 1000L;
        }
        else if (values[0].endsWith("s")) {
            // seconds
            final int seconds = this.getValue(values[0]);
            this.maxReservedTime = seconds * 1000L;
        }
        else if (values[0].endsWith("h")) {
            // hours
            final int hours = this.getValue(values[0]);
            this.maxReservedTime = hours * 3600L * 1000L;
        }
        else {
            // default is hours
            final int hours = Integer.parseInt(values[0]);
            this.maxReservedTime = hours * 3600L * 1000L;
        }
    }


    private int getValue(final String v) {
        return Integer.valueOf(v.substring(0, v.length() - 1));
    }


    @Override
    public String name() {
        return NAME;
    }

}
