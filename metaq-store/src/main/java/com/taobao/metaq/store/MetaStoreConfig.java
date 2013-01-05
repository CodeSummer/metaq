/**
 * $Id: MetaStoreConfig.java 3 2013-01-05 08:20:46Z shijia $
 */
package com.taobao.metaq.store;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.metamorphosis.utils.MetaMBeanServer;

/**
 * �洢������
 * 
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class MetaStoreConfig implements MetaStoreConfigMBean {
    static final Log log = LogFactory.getLog(MetaStoreConfig.class);
    // �Ƿ���Master��ɫ�������Slave�������ó�false
    private boolean master = true;
    // ������д洢Ŀ¼
    private String storePathPhysic = System.getProperty("user.home") + File.separator + "metastore"
            + File.separator + "physic";
    // �߼����д洢Ŀ¼
    private String storePathLogics = System.getProperty("user.home") + File.separator + "metastore"
            + File.separator + "logics";
    // �쳣�˳��������ļ�
    private String storeCheckpoint = System.getProperty("user.home") + File.separator + "metastore"
            + File.separator + "metaStoreCheckpoint";
    // �쳣�˳��������ļ�
    private String abortFile = System.getProperty("user.home") + File.separator + "metastore" + File.separator
            + "metaStoreAbort";
    // �������ÿ���ļ���С 1G
    private int mapedFileSizePhysic = 1024 * 1024 * 1024;
    // �߼�����ÿ���ļ���С 2M
    private int mapedFileSizeLogics = 1024 * 1024 * 2;
    // �������ˢ�̼��ʱ�䣨��λ���룩
    private int flushIntervalPhysic = 1000;
    // �߼�����ˢ�̼��ʱ�䣨��λ���룩
    private int flushIntervalLogics = 1000;
    // ������Դ���ʱ�䣨��λ���룩
    private int cleanResourceInterval = 10000;
    // ɾ����������ļ��ļ��ʱ�䣨��λ���룩
    private int deletePhysicFilesInterval = 100;
    // ɾ������߼��ļ��ļ��ʱ�䣨��λ���룩
    private int deleteLogicsFilesInterval = 100;
    // ǿ��ɾ���ļ����ʱ�䣨��λ���룩
    private int destroyMapedFileIntervalForcibly = 1000 * 120;
    // ���ڼ��Hanged�ļ����ʱ�䣨��λ���룩
    private int redeleteHangedFileInterval = 1000 * 120;
    // ��ʱ����ɾ���ļ�, Ĭ���賿4��ɾ���ļ�
    private String deleteWhen = "04";
    // ���̿ռ����ʹ����
    private int diskMaxUsedSpaceRatio = 75;
    // �ļ�����ʱ�䣨��λСʱ��
    private int fileReservedTime = 12;
    // �Ƿ���GrouCommit����
    private boolean groupCommitEnable = false;
    // GrouCommit �ȴ���ʱʱ�䣨��λ���룩
    private int groupCommitTimeout = 1000 * 5;
    // д��Ϣ�������߼����У���������ˮλ��������ʼ����
    private int putMsgIndexHightWater = 400000;
    // �����Ϣ��С��Ĭ��512K
    private int maxMessageSize = 1024 * 512;
    // ����ʱ���Ƿ�У��CRC
    private boolean checkCRCOnRecover = true;
    // ˢ������У�����ˢ����PAGE
    private int flushPhysicQueueLeastPages = 4;
    // ˢ�߼����У�����ˢ����PAGE
    private int flushLogicsQueueLeastPages = 2;
    // ˢ������У�����ˢ�̼��ʱ��
    private int flushPhysicQueueThoroughInterval = 1000 * 10;
    // ˢ�߼����У�����ˢ�̼��ʱ��
    private int flushLogicsQueueThoroughInterval = 1000 * 60;
    // �����ȡ����Ϣ�ֽ�������Ϣ���ڴ�
    private int maxTransferBytesOnMessageInMemory = 1024 * 256;
    // �����ȡ����Ϣ��������Ϣ���ڴ�
    private int maxTransferCountOnMessageInMemory = 32;
    // �����ȡ����Ϣ�ֽ�������Ϣ�ڴ���
    private int maxTransferBytesOnMessageInDisk = 1024 * 64;
    // �����ȡ����Ϣ��������Ϣ�ڴ���
    private int maxTransferCountOnMessageInDisk = 8;
    // ��ǰ���̿��������ڴ��С����λG
    private int totalPhysicMemory = 5;


    public int getMapedFileSizePhysic() {
        return mapedFileSizePhysic;
    }


    public void setMapedFileSizePhysic(int mapedFileSizePhysic) {
        this.mapedFileSizePhysic = mapedFileSizePhysic;
    }


    public int getMapedFileSizeLogics() {
        // �˴���Ҫ����ȡ��
        int factor = (int) Math.ceil(this.mapedFileSizeLogics / (MetaQueueLogistic.StoreUnitSize * 1.0));
        return (int) (factor * MetaQueueLogistic.StoreUnitSize);
    }


    public void setMapedFileSizeLogics(int mapedFileSizeLogics) {
        this.mapedFileSizeLogics = mapedFileSizeLogics;
    }


    public int getFlushIntervalPhysic() {
        return flushIntervalPhysic;
    }


    public void setFlushIntervalPhysic(int flushIntervalPhysic) {
        this.flushIntervalPhysic = flushIntervalPhysic;
    }


    public int getFlushIntervalLogics() {
        return flushIntervalLogics;
    }


    public void setFlushIntervalLogics(int flushIntervalLogics) {
        this.flushIntervalLogics = flushIntervalLogics;
    }


    public boolean isGroupCommitEnable() {
        return groupCommitEnable;
    }


    public void setGroupCommitEnable(boolean groupCommitEnable) {
        this.groupCommitEnable = groupCommitEnable;
    }


    public int getGroupCommitTimeout() {
        return groupCommitTimeout;
    }


    public boolean getGroupCommitEnable() {
        return groupCommitEnable;
    }


    public void setGroupCommitTimeout(int groupCommitTimeout) {
        this.groupCommitTimeout = groupCommitTimeout;
    }


    public int getPutMsgIndexHightWater() {
        return putMsgIndexHightWater;
    }


    public void setPutMsgIndexHightWater(int putMsgIndexHightWater) {
        this.putMsgIndexHightWater = putMsgIndexHightWater;
    }


    public int getCleanResourceInterval() {
        return cleanResourceInterval;
    }


    public void setCleanResourceInterval(int cleanResourceInterval) {
        this.cleanResourceInterval = cleanResourceInterval;
    }


    public int getMaxMessageSize() {
        return maxMessageSize;
    }


    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }


    public boolean isCheckCRCOnRecover() {
        return checkCRCOnRecover;
    }


    public boolean getCheckCRCOnRecover() {
        return checkCRCOnRecover;
    }


    public void setCheckCRCOnRecover(boolean checkCRCOnRecover) {
        this.checkCRCOnRecover = checkCRCOnRecover;
    }


    public String getStorePathPhysic() {
        return storePathPhysic;
    }


    public void setStorePathPhysic(String storePathPhysic) {
        this.storePathPhysic = storePathPhysic;
    }


    public String getStorePathLogics() {
        return storePathLogics;
    }


    public void setStorePathLogics(String storePathLogics) {
        this.storePathLogics = storePathLogics;
    }


    public String getAbortFile() {
        return abortFile;
    }


    public void setAbortFile(String abortFile) {
        this.abortFile = abortFile;
    }


    public String getDeleteWhen() {
        return deleteWhen;
    }


    public void setDeleteWhen(String deleteWhen) {
        this.deleteWhen = deleteWhen;
    }


    public int getDiskMaxUsedSpaceRatio() {
        if (this.diskMaxUsedSpaceRatio < 10)
            return 10;

        if (this.diskMaxUsedSpaceRatio > 95)
            return 95;

        return diskMaxUsedSpaceRatio;
    }


    public void setDiskMaxUsedSpaceRatio(int diskMaxUsedSpaceRatio) {
        this.diskMaxUsedSpaceRatio = diskMaxUsedSpaceRatio;
    }


    public int getDeletePhysicFilesInterval() {
        return deletePhysicFilesInterval;
    }


    public void setDeletePhysicFilesInterval(int deletePhysicFilesInterval) {
        this.deletePhysicFilesInterval = deletePhysicFilesInterval;
    }


    public int getDeleteLogicsFilesInterval() {
        return deleteLogicsFilesInterval;
    }


    public void setDeleteLogicsFilesInterval(int deleteLogicsFilesInterval) {
        this.deleteLogicsFilesInterval = deleteLogicsFilesInterval;
    }


    public int getMaxTransferBytesOnMessageInMemory() {
        return maxTransferBytesOnMessageInMemory;
    }


    public void setMaxTransferBytesOnMessageInMemory(int maxTransferBytesOnMessageInMemory) {
        this.maxTransferBytesOnMessageInMemory = maxTransferBytesOnMessageInMemory;
    }


    public int getMaxTransferCountOnMessageInMemory() {
        return maxTransferCountOnMessageInMemory;
    }


    public void setMaxTransferCountOnMessageInMemory(int maxTransferCountOnMessageInMemory) {
        this.maxTransferCountOnMessageInMemory = maxTransferCountOnMessageInMemory;
    }


    public int getMaxTransferBytesOnMessageInDisk() {
        return maxTransferBytesOnMessageInDisk;
    }


    public void setMaxTransferBytesOnMessageInDisk(int maxTransferBytesOnMessageInDisk) {
        this.maxTransferBytesOnMessageInDisk = maxTransferBytesOnMessageInDisk;
    }


    public int getMaxTransferCountOnMessageInDisk() {
        return maxTransferCountOnMessageInDisk;
    }


    public void setMaxTransferCountOnMessageInDisk(int maxTransferCountOnMessageInDisk) {
        this.maxTransferCountOnMessageInDisk = maxTransferCountOnMessageInDisk;
    }


    public int getTotalPhysicMemory() {
        return totalPhysicMemory;
    }


    public void setTotalPhysicMemory(int totalPhysicMemory) {
        this.totalPhysicMemory = totalPhysicMemory;
    }


    public int getFlushPhysicQueueLeastPages() {
        return flushPhysicQueueLeastPages;
    }


    public void setFlushPhysicQueueLeastPages(int flushPhysicQueueLeastPages) {
        this.flushPhysicQueueLeastPages = flushPhysicQueueLeastPages;
    }


    public int getFlushLogicsQueueLeastPages() {
        return flushLogicsQueueLeastPages;
    }


    public void setFlushLogicsQueueLeastPages(int flushLogicsQueueLeastPages) {
        this.flushLogicsQueueLeastPages = flushLogicsQueueLeastPages;
    }


    public int getFlushPhysicQueueThoroughInterval() {
        return flushPhysicQueueThoroughInterval;
    }


    public void setFlushPhysicQueueThoroughInterval(int flushPhysicQueueThoroughInterval) {
        this.flushPhysicQueueThoroughInterval = flushPhysicQueueThoroughInterval;
    }


    public int getFlushLogicsQueueThoroughInterval() {
        return flushLogicsQueueThoroughInterval;
    }


    public void setFlushLogicsQueueThoroughInterval(int flushLogicsQueueThoroughInterval) {
        this.flushLogicsQueueThoroughInterval = flushLogicsQueueThoroughInterval;
    }


    public int getDestroyMapedFileIntervalForcibly() {
        return destroyMapedFileIntervalForcibly;
    }


    public void setDestroyMapedFileIntervalForcibly(int destroyMapedFileIntervalForcibly) {
        this.destroyMapedFileIntervalForcibly = destroyMapedFileIntervalForcibly;
    }


    public String getStoreCheckpoint() {
        return storeCheckpoint;
    }


    public void setStoreCheckpoint(String storeCheckpoint) {
        this.storeCheckpoint = storeCheckpoint;
    }


    public boolean isMaster() {
        return master;
    }


    public void setMaster(boolean master) {
        this.master = master;
    }


    public boolean getMaster() {
        return this.master;
    }


    public int getFileReservedTime() {
        return fileReservedTime;
    }


    public void setFileReservedTime(int fileReservedTime) {
        this.fileReservedTime = fileReservedTime;
    }


    public int getRedeleteHangedFileInterval() {
        return redeleteHangedFileInterval;
    }


    public void setRedeleteHangedFileInterval(int redeleteHangedFileInterval) {
        this.redeleteHangedFileInterval = redeleteHangedFileInterval;
    }


    @Override
    public void reload(String configPath) {
        MetaStoreConfig msc = MetaStoreConfig.createMetaStoreConfig(configPath, false);
        Method[] methods = MetaStoreConfig.class.getMethods();
        for (Method setMethod : methods) {
            String setName = setMethod.getName();
            if (setName.startsWith("set")) {
                String getName = setName.replaceFirst("set", "get");
                try {
                    Method getMethod = MetaStoreConfig.class.getMethod(getName, new Class[] {});
                    setMethod.invoke(this, new Object[] { getMethod.invoke(msc, new Object[] {}) });
                }
                catch (NoSuchMethodException e) {

                }
                catch (Exception e) {
                    log.error("invoke method error. method=set/get" + setName.substring(3), e);
                }
            }
        }
        log.info("reload metastoreconfig " + configPath + "success at " + new Date());
    }


    public static MetaStoreConfig createMetaStoreConfig(String configPath, boolean registerMBean) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(configPath);
        MetaStoreConfig msc = (MetaStoreConfig) ctx.getBean("metaStoreConfig");
        if (registerMBean) {
            MetaMBeanServer.registMBean(msc, null);
        }
        return msc;
    }

}
