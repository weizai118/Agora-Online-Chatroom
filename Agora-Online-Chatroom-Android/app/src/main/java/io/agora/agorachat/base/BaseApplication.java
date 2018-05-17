package io.agora.agorachat.base;

import android.app.Application;

import io.agora.AgoraAPIOnlySignal;
import io.agora.agorachat.R;
import io.agora.agorachat.media.WorkerThread;

public class BaseApplication extends Application {

    private WorkerThread mWorkerThread;

    private AgoraAPIOnlySignal mAgoraAPIOnlySignal;

    private static BaseApplication mInstance;

    public BaseApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAgoraAPIOnlySignal();
    }

    public static BaseApplication getInstance(){
        return mInstance;
    }

    public synchronized WorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    private void initAgoraAPIOnlySignal() {
        mAgoraAPIOnlySignal = AgoraAPIOnlySignal.getInstance(this, getString(R.string.private_app_id));
    }

    public AgoraAPIOnlySignal getAgoraAPIOnlySignal() {
        return mAgoraAPIOnlySignal;
    }
}
