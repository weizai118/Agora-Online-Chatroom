package io.agora.agorachat.media;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.rtc.IRtcEngineEventHandler;

public class EngineEventHandlerManager {

    private final Context mContext;

    private final EngineConfig mEngineConfig;

    private final ConcurrentHashMap<SubEngineEventHandler, Integer> mEventHandlerList = new ConcurrentHashMap<>();

    private final IRtcEngineEventHandler mRtcEngineEventHandler = new IRtcEngineEventHandler() {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public void onUserJoined(int uid, int elapsed) {

        }

        @Override
        public void onUserOffline(int uid, int reason) {
            logger.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        }

        @Override
        public void onRtcStats(RtcStats stats) {

        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakerInfos, int totalVolume) {

        }

        @Override
        public void onLeaveChannel(RtcStats stats) {

        }

        @Override
        public void onLastmileQuality(int quality) {
            logger.debug("onLastmileQuality " + quality);
        }

        @Override
        public void onError(int error) {
            logger.debug("onError " + error);
        }

        @Override
        public void onAudioQuality(int uid, int quality, short delay, short lost) {

        }

        @Override
        public void onConnectionLost() {
            logger.debug("onConnectionLost");
        }

        @Override
        public void onConnectionInterrupted() {
            logger.debug("onConnectionInterrupted");
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);

            mEngineConfig.setUid(uid);

            Iterator<SubEngineEventHandler> it = mEventHandlerList.keySet().iterator();
            while (it.hasNext()) {
                SubEngineEventHandler handler = it.next();
                handler.onJoinChannelSuccess(channel, uid, elapsed);
            }
        }

        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            logger.debug("onRejoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
        }

        public void onWarning(int warn) {
            logger.debug("onWarning " + warn);
        }

        @Override
        public void onAudioRouteChanged(int routing) {
            logger.debug("onAudioRouteChanged " + routing);
        }

        @Override
        public void onClientRoleChanged(int oldRole, int newRole) {
            super.onClientRoleChanged(oldRole, newRole);
        }
    };

    public EngineEventHandlerManager(Context context, EngineConfig engineConfig) {
        this.mContext = context;
        this.mEngineConfig = engineConfig;
    }

    public void addEventHandler(SubEngineEventHandler handler) {
        this.mEventHandlerList.put(handler, 0);
    }

    public void removeEventHandler(SubEngineEventHandler handler) {
        this.mEventHandlerList.remove(handler);
    }

    public IRtcEngineEventHandler getRtcEngineEventHandler() {
        return mRtcEngineEventHandler;
    }
}
