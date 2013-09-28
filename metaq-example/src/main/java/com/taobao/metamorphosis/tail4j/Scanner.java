package com.taobao.metamorphosis.tail4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.diamond.io.FileSystem;
import com.taobao.diamond.io.Path;
import com.taobao.diamond.io.watch.StandardWatchEventKind;
import com.taobao.diamond.io.watch.WatchEvent;
import com.taobao.diamond.io.watch.WatchKey;
import com.taobao.diamond.io.watch.WatchService;
import com.taobao.metamorphosis.tail4j.Config.LogConfig;


public class Scanner {

    private final WatchService watchService;

    private final LogConfig logConfig;

    private final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    private volatile boolean isRun = true;

    private final BlockingDeque<String> fifoQueue = new LinkedBlockingDeque<String>();

    private FileChannel fc;

    private File currentLogFile;

    static final Log log = LogFactory.getLog(Scanner.class);

    private final Pattern logNamePat;


    public void close() throws IOException {
        this.isRun = false;
        this.singleExecutor.shutdown();
        if (this.fc != null && this.fc.isOpen()) {
            this.fc.close();
        }
        this.watchService.close();
    }


    public Scanner(LogConfig logConfig) {
        super();
        this.logConfig = logConfig;
        this.logNamePat = Pattern.compile(logConfig.logNamePattern);
        this.watchService = FileSystem.getDefault().newWatchService();
        this.watchService
            .register(new Path(new File(logConfig.logBasePath)), true, StandardWatchEventKind.ENTRY_CREATE);
        // ��һ�����У�����check
        this.checkAtFirst(this.watchService);
        this.singleExecutor.execute(new Runnable() {
            public void run() {
                log.info(">>>>>>�Ѿ���ʼ���Ŀ¼" + Scanner.this.logConfig.logBasePath + "<<<<<<");
                // ����ѭ���ȴ��¼�
                while (Scanner.this.isRun) {
                    // ƾ֤
                    WatchKey key;
                    try {
                        key = Scanner.this.watchService.take();
                    }
                    catch (InterruptedException x) {
                        continue;
                    }
                    // reset�������Ч������ѭ��,��Ч�����Ǽ�����Ŀ¼��ɾ��
                    if (!Scanner.this.processEvents(key)) {
                        log.error("reset unvalid,��ط���ʧЧ");
                        break;
                    }
                }
                log.info(">>>>>>�˳����Ŀ¼" + Scanner.this.logConfig.logBasePath + "<<<<<<");
                Scanner.this.watchService.close();
            }

        });
    }


    private void checkAtFirst(final WatchService watcher) {
        watcher.check();
        WatchKey key = null;
        while ((key = watcher.poll()) != null) {
            this.processEvents(key);
        }
    }


    public File getCurrLogFile() {
        return this.currentLogFile;
    }


    public synchronized void closeChannel() throws IOException {
        if (this.fc == null) {
            throw new NoOpenFileChannelException();
        }
        String path = this.fifoQueue.poll();
        log.info("Closing file " + this.currentLogFile.getAbsolutePath());
        this.fc.close();
        this.fc = null;
        this.currentLogFile = null;
    }


    public int getQueueSize() {
        return this.fifoQueue.size();
    }


    public synchronized FileChannel getCurrChannel(long lastModify) throws IOException {
        if (this.fc == null) {
            // ��������һ�α����checkpoint���ɵ��ļ�
            if (lastModify > 0) {
                String path = null;
                while ((path = this.fifoQueue.peek()) != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        // �ɵ��ļ����Թ�
                        if (file.lastModified() < lastModify) {
                            this.fifoQueue.poll();
                        }
                        else {
                            break;
                        }
                    }
                    // ��ʱ�ļ���ʱ�����ڣ����Խ���
                    else if (path.equals(this.logConfig.tmpLogFullPath)) {
                        break;
                    }
                    else {
                        // �����ڵ��ļ�������
                        this.fifoQueue.poll();
                    }
                }
            }
            String path = this.fifoQueue.peek();
            if (path == null) {
                return null;
            }
            this.currentLogFile = new File(path);
            if (!this.currentLogFile.exists()) {
                return null;
            }
            this.fc = new RandomAccessFile(this.currentLogFile, "r").getChannel();
            log.info("Opening file " + path);
        }
        return this.fc;
    }


    /**
     * ���������¼�
     * 
     * @param key
     * @return
     */
    @SuppressWarnings( { "unchecked" })
    private synchronized boolean processEvents(WatchKey key) {
        List<Path> newList = new ArrayList<Path>();
        /**
         * ��ȡ�¼�����
         */
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path eventPath = ev.context();
            String realPath = eventPath.getAbsolutePath();
            if (ev.kind() == StandardWatchEventKind.ENTRY_CREATE || ev.kind() == StandardWatchEventKind.ENTRY_MODIFY) {
                if (this.logNamePat.matcher(realPath).matches()) {
                    newList.add(eventPath);
                }
            }

        }
        // �Ӿɵ�������
        Collections.sort(newList, new Comparator<Path>() {
            public int compare(Path o1, Path o2) {
                long result = o1.lastModified() - o2.lastModified();
                if (result == 0) {
                    return 0;
                }
                else {
                    return result > 0 ? 1 : -1;
                }
            }

        });
        File tail = null;
        if (!this.fifoQueue.isEmpty()) {
            tail = new File(this.fifoQueue.peekLast());
        }
        // �������,�Ӿɵ���
        for (Path path : newList) {
            String absolutePath = path.getAbsolutePath();

            // watch service���ܸ�֪�����tmp�ļ����¼�������������Ѿ�����ʱ�ļ��������
            if (absolutePath.equals(this.logConfig.tmpLogFullPath)
                    && this.fifoQueue.contains(this.logConfig.tmpLogFullPath)) {
                continue;
            }

            // tmp mode,��ǰ���ڶ���ʱ�ļ����¼�����ļ���rename����ļ�����������Ϊ��ʱ�ļ�����
            if (this.currentLogFile != null
                    && this.currentLogFile.getAbsolutePath().equals(this.logConfig.tmpLogFullPath)) {
                log.info("�����ļ�" + this.logConfig.tmpLogFullPath);
                this.fifoQueue.offer(this.logConfig.tmpLogFullPath);
            }
            else {
                // ֻ�б�β�����ļ����ɵ��ļ��ż���
                if (tail == null || path.lastModified() > tail.lastModified()) {
                    log.info("�����ļ�" + absolutePath);
                    this.fifoQueue.offer(absolutePath);
                }
                else {
                    log.info("�����ļ�" + absolutePath);
                }
            }
        }
        return key.reset();
    }

}
