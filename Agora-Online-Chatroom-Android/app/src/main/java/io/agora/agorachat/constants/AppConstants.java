package io.agora.agorachat.constants;

import io.agora.rtc.RtcEngine;

public class AppConstants {
    // permissions
    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;

    // intent extra values
    public static final String ACTION_KEY_CHANNEL_NAME = "CHANNEL_NAME";
    public static final String ACTION_KEY_CHAT_TOPIC = "CHAT_TOPIC";
    public static final String ACTION_KEY_CLIENT_ROLE = "CLIENT_ROLE";

    // client role
    public static final int CLIENT_ROLE_OWNER = 0;

    public static final String MEDIA_SDK_VERSION = RtcEngine.getSdkVersion();
}
