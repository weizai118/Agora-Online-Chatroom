package io.agora.agorachat.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import io.agora.agorachat.R;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;

public class WorkerThread extends Thread {
    private static final int ACTION_WORKER_THREAD_QUIT = 0x1000;
    private static final int ACTION_WORKER_JOIN_CHANNEL = 0x1001;
    private static final int ACTION_WORKER_LEAVE_CHANNEL = 0x1002;

    private static final Logger log = LoggerFactory.getLogger(WorkerThread.class);

    private final Context mContext;

    private WorkerThreadHandler mWorkerHandler;

    private boolean mReady;

    private RtcEngine mRtcEngine;
    private EngineConfig mEngineConfig;
    private final EngineEventHandlerManager mEngineEventHandlerManager;

    public WorkerThread(Context context) {
        this.mContext = context;

        this.mEngineConfig = new EngineConfig();

        this.mEngineConfig.setUid(0);

        this.mEngineEventHandlerManager = new EngineEventHandlerManager(mContext, this.mEngineConfig);
    }

    @Override
    public void run() {
        log.trace("start to run");

        Looper.prepare();

        mWorkerHandler = new WorkerThreadHandler(this);

        ensureRtcEngineReadyLock();

        mReady = true;

        Looper.loop();
    }

    public final void waitForReady() {
        while (!mReady) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("wait for " + WorkerThread.class.getSimpleName());
        }
    }

    public final void joinChannel(final String channel, int uid) {
        if (Thread.currentThread() != this) {
            log.warn("joinChannel() - worker thread asynchronously " + channel + " " + uid);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_JOIN_CHANNEL;
            envelop.obj = new String[]{channel};
            envelop.arg1 = uid;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        ensureRtcEngineReadyLock();

        mRtcEngine.joinChannel(null, channel, "", 0);

        mEngineConfig.setChannel(channel);

        log.debug("joinChannel " + channel + " " + uid);
    }

    public final void leaveChannel(String channel) {
        if (Thread.currentThread() != this) {
            log.warn("leaveChannel() - worker thread asynchronously " + channel);
            Message envelop = new Message();
            envelop.what = ACTION_WORKER_LEAVE_CHANNEL;
            envelop.obj = channel;
            mWorkerHandler.sendMessage(envelop);
            return;
        }

        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }

        mEngineConfig.reset();
        log.debug("leaveChannel " + channel);
    }

    public final EngineConfig getEngineConfig() {
        return mEngineConfig;
    }

    private RtcEngine ensureRtcEngineReadyLock() {
        if (mRtcEngine == null) {
            String appId = mContext.getString(R.string.private_app_id);
            if (TextUtils.isEmpty(appId)) {
                throw new RuntimeException("NEED TO use your App ID, get your own ID at https://dashboard.agora.io/");
            }
            try {
                mRtcEngine = RtcEngine.create(mContext, appId, mEngineEventHandlerManager.getRtcEngineEventHandler());
            } catch (Exception e) {
                log.error(Log.getStackTraceString(e));
                throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
            }
            mRtcEngine.setParameters("{\"rtc.log_filter\":65535}");
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableAudioVolumeIndication(200, 3); // 200 ms
            mRtcEngine.setLogFile(Environment.getExternalStorageDirectory()
                    + File.separator + mContext.getPackageName() + "/log/agora-rtc.log");
        }
        return mRtcEngine;
    }

    public EngineEventHandlerManager getEngineEventHandlerManager() {
        return mEngineEventHandlerManager;
    }

    public RtcEngine getRtcEngine() {
        return mRtcEngine;
    }

    /**
     * call this method to exit
     * should ONLY call this method when this thread is running
     */
    public final void exit() {
        if (Thread.currentThread() != this) {
            log.warn("exit() - exit app thread asynchronously");
            mWorkerHandler.sendEmptyMessage(ACTION_WORKER_THREAD_QUIT);
            return;
        }

        log.debug("exit() > start");

        mReady = false;

        mWorkerHandler.removeCallbacksAndMessages(null);

        Looper.myLooper().quit();

        mWorkerHandler.release();

        log.debug("exit() > end");
    }

    private static final class WorkerThreadHandler extends Handler {

        private WorkerThread mWorkerThread;

        public WorkerThreadHandler(WorkerThread workerThread) {
            this.mWorkerThread = workerThread;
        }

        public void release() {
            mWorkerThread = null;
        }

        @Override
        public void handleMessage(Message msg) {
            if (this.mWorkerThread == null) {
                log.warn("handler is already released! " + msg.what);
                return;
            }

            switch (msg.what) {
                case ACTION_WORKER_THREAD_QUIT:
                    mWorkerThread.exit();
                    break;
                case ACTION_WORKER_JOIN_CHANNEL:
                    String[] data = (String[]) msg.obj;
                    mWorkerThread.joinChannel(data[0], msg.arg1);
                    break;
                case ACTION_WORKER_LEAVE_CHANNEL:
                    String channel = (String) msg.obj;
                    mWorkerThread.leaveChannel(channel);
                    break;
            }
        }
    }
}
