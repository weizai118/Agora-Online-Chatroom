package io.agora.agorachat.media;

public class EngineConfig {

    private int mUid;

    private String mChannel;

    public int getUid() {
        return mUid;
    }

    public void setUid(int mUid) {
        this.mUid = mUid;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String mChannel) {
        this.mChannel = mChannel;
    }

    public void reset() {
        mChannel = null;
    }
}
