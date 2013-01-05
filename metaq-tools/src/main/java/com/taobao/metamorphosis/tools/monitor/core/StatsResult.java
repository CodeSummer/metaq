package com.taobao.metamorphosis.tools.monitor.core;


/**
 *
 * @author �޻�
 * @since 2011-5-27 ����11:39:00
 */

public class StatsResult {

    private boolean success;
    private String statsInfo;
    private final String serverUrl;//���ĸ�����������
    private Exception e;
    public StatsResult(String serverUrl) {
        super();
        this.serverUrl = serverUrl;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getStatsInfo() {
        return statsInfo;
    }
    public void setStatsInfo(String statsInfo) {
        this.statsInfo = statsInfo;
    }
    public Exception getException() {
        return e;
    }
    public void setException(Exception e) {
        this.e = e;
    }
    public String getServerUrl() {
        return serverUrl;
    }
    
    
}
