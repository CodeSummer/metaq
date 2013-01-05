/**
 * $Id: RunningFlags.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

/**
 * �洢������״̬λ
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class RunningFlags {
    // ��ֹ��Ȩ��
    private static final int NotReadableBit = 0x1;
    // ��ֹдȨ��
    private static final int NotWriteableBit = 0x2;
    // �߼������Ƿ�������
    private static final int WriteLogicsQueueErrorBit = 0x4;
    // ���̿ռ䲻��
    private static final int DiskFullBit = 0x8;

    private volatile int flagBits = 0;


    public int getFlagBits() {
        return flagBits;
    }


    public RunningFlags() {
    }


    public boolean isReadable() {
        if ((this.flagBits & NotReadableBit) == 0) {
            return true;
        }

        return false;
    }


    public boolean isWriteable() {
        if ((this.flagBits & (NotWriteableBit | WriteLogicsQueueErrorBit | DiskFullBit)) == 0) {
            return true;
        }

        return false;
    }


    public boolean getAndMakeReadable() {
        boolean result = this.isReadable();
        if (!result) {
            this.flagBits &= ~NotReadableBit;
        }
        return result;
    }


    public boolean getAndMakeNotReadable() {
        boolean result = this.isReadable();
        if (result) {
            this.flagBits |= NotReadableBit;
        }
        return result;
    }


    public boolean getAndMakeWriteable() {
        boolean result = this.isWriteable();
        if (!result) {
            this.flagBits &= ~NotWriteableBit;
        }
        return result;
    }


    public boolean getAndMakeNotWriteable() {
        boolean result = this.isWriteable();
        if (result) {
            this.flagBits |= NotWriteableBit;
        }
        return result;
    }


    public void makeLogicsQueueError() {
        this.flagBits |= WriteLogicsQueueErrorBit;
    }


    public boolean isLogicsQueueError() {
        if ((this.flagBits & WriteLogicsQueueErrorBit) == WriteLogicsQueueErrorBit) {
            return true;
        }

        return false;
    }


    /**
     * ����Disk�Ƿ�����
     */
    public boolean getAndMakeDiskFull() {
        boolean result = !((this.flagBits & DiskFullBit) == DiskFullBit);
        this.flagBits |= DiskFullBit;
        return result;
    }


    /**
     * ����Disk�Ƿ�����
     */
    public boolean getAndMakeDiskOK() {
        boolean result = !((this.flagBits & DiskFullBit) == DiskFullBit);
        this.flagBits &= ~DiskFullBit;
        return result;
    }
}
