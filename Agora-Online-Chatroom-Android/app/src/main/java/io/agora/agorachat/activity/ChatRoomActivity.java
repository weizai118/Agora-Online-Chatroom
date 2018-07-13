package io.agora.agorachat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.AgoraAPIOnlySignal;
import io.agora.agorachat.R;
import io.agora.agorachat.adapter.ImgSelectorRecyclerAdapter;
import io.agora.agorachat.adapter.MsgListRecyclerAdapter;
import io.agora.agorachat.adapter.MusicSelectorRecyclerAdapter;
import io.agora.agorachat.adapter.UsrListRecyclerAdapter;
import io.agora.agorachat.base.BaseActivity;
import io.agora.agorachat.base.BaseApplication;
import io.agora.agorachat.bean.Msg;
import io.agora.agorachat.bean.MsgListItem;
import io.agora.agorachat.bean.Usr;
import io.agora.agorachat.constants.AppConstants;
import io.agora.agorachat.constants.MsgType;
import io.agora.agorachat.media.SubEngineEventHandler;
import io.agora.agorachat.signal.AbstractICallBack;
import io.agora.agorachat.utils.BitmapUtils;

import static io.agora.rtc.Constants.AUDIO_PROFILE_DEFAULT;
import static io.agora.rtc.Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO;
import static io.agora.rtc.Constants.AUDIO_SCENARIO_DEFAULT;
import static io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE;
import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;

public class ChatRoomActivity extends BaseActivity implements SubEngineEventHandler {

    @BindView(R.id.img_portrait)
    CircleImageView imgPortrait;
    @BindView(R.id.txt_host_name)
    TextView txtHostName;
    @BindView(R.id.ll_profile)
    LinearLayout llProfile;
    @BindView(R.id.txt_topic)
    TextView txtTopic;
    @BindView(R.id.recycler_participants)
    RecyclerView recyclerParticipants;
    @BindView(R.id.txt_num)
    TextView txtNum;
    @BindView(R.id.ll_participants)
    LinearLayout llParticipants;
    @BindView(R.id.recycler_msg_list)
    RecyclerView recyclerMsgList;
    @BindView(R.id.btn_up)
    Button btnUp;
    @BindView(R.id.btn_mute)
    Button btnMute;
    @BindView(R.id.ll_controller)
    LinearLayout llController;
    @BindView(R.id.edt_msg)
    EditText edtMsg;
    @BindView(R.id.btn_msg_send)
    Button btnMsgSend;
    @BindView(R.id.ll_msg)
    LinearLayout llMsg;
    @BindView(R.id.ll_chat_room_bg)
    RelativeLayout llChatRoomBg;
    @BindView(R.id.btn_img)
    Button btnImg;
    @BindView(R.id.btn_music)
    Button btnMusic;

    private LayoutInflater mInflater;

    private int mRole = -1;
    private String mAccount = null; // 用户名

    private UsrListRecyclerAdapter mUsrListRecyclerAdapter;
    private List<Usr> mUsrListItems;

    private MsgListRecyclerAdapter mMsgListRecyclerAdapter;
    private List<MsgListItem> mMsgListItems;

    private MusicSelectorRecyclerAdapter mMusicSelectorRecyclerAdapter;
    private ImgSelectorRecyclerAdapter mImgSelectorRecyclerAdapter;

    private Gson mGson;

    private String mChannelName;

    private AgoraAPIOnlySignal mSignal;

    private int[] paths = {R.mipmap.portrait_0, R.mipmap.portrait_1, R.mipmap.portrait_2, R.mipmap.portrait_3};

    private int mBgPosition;

    // 这个 field 用于保存房主的 account
    private String mOwnerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mInflater = LayoutInflater.from(this);

        mGson = new Gson();

        mChannelName = getIntent().getStringExtra(AppConstants.ACTION_KEY_CHANNEL_NAME);

        mSignal = BaseApplication.getInstance().getAgoraAPIOnlySignal();
        addCallbackForSignal();

        setContentView(R.layout.activity_chat_room);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initUIandEvent() {
        getEngineEventHandleManager().addEventHandler(this);

        if (TextUtils.isEmpty(getIntent().getStringExtra(AppConstants.ACTION_KEY_CHAT_TOPIC))) {
            txtTopic.setText("春夜喜雨");
        } else {
            txtTopic.setText(getIntent().getStringExtra(AppConstants.ACTION_KEY_CHAT_TOPIC));
        }

        // 初始化头像
        imgPortrait.setImageBitmap(BitmapUtils.readBitMap(this, paths[new Random().nextInt(3)]));

        // 初始化头像列表
        mUsrListItems = new ArrayList<>();
        mUsrListRecyclerAdapter = new UsrListRecyclerAdapter(this, mUsrListItems);
        LinearLayoutManager usrLayoutManager = new LinearLayoutManager(this);
        usrLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        recyclerParticipants.setLayoutManager(usrLayoutManager);
        recyclerParticipants.setAdapter(mUsrListRecyclerAdapter);

        // 初始化人数
        txtNum.setText(mUsrListItems.size() + "人");

        // 初始化消息列表
        mMsgListItems = new ArrayList<>();
        mMsgListRecyclerAdapter = new MsgListRecyclerAdapter(this, mMsgListItems);
        LinearLayoutManager msgLayoutManager = new LinearLayoutManager(this);
        recyclerMsgList.setLayoutManager(msgLayoutManager);
        recyclerMsgList.setAdapter(mMsgListRecyclerAdapter);

        mRole = getIntent().getIntExtra(AppConstants.ACTION_KEY_CLIENT_ROLE, CLIENT_ROLE_BROADCASTER);
        if (mRole == CLIENT_ROLE_AUDIENCE) {
            getWorkerThread().getRtcEngine().setAudioProfile(AUDIO_PROFILE_DEFAULT, AUDIO_SCENARIO_DEFAULT);
        } else {
            getWorkerThread().getRtcEngine().setAudioProfile(AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO, AUDIO_SCENARIO_DEFAULT);
        }

        if (mRole != AppConstants.CLIENT_ROLE_OWNER) {
            btnImg.setVisibility(View.GONE);
            btnMusic.setVisibility(View.GONE);
        } else {
            btnUp.setVisibility(View.GONE);
        }

        getWorkerThread().getRtcEngine().setClientRole(mRole != CLIENT_ROLE_AUDIENCE ? CLIENT_ROLE_BROADCASTER : CLIENT_ROLE_AUDIENCE);

        getWorkerThread().joinChannel(mChannelName, getEngineConfig().getUid());

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        // 初始化背景
        if (mRole == AppConstants.CLIENT_ROLE_OWNER) {
            int id = getResources().getIdentifier("io.agora.agorachat:mipmap/bg_" + 0, null, null);
            llChatRoomBg.setBackgroundResource(id);
        }
    }

    @Override
    protected void deInitUIandEvent() {
        getWorkerThread().leaveChannel(getEngineConfig().getChannel());
        getEngineEventHandleManager().removeEventHandler(this);

        // 发送离开频道的频道消息
        if (mRole == AppConstants.CLIENT_ROLE_OWNER) {
            mSignal.messageChannelSend(mChannelName, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_LEAVE_CHANNEL, mAccount, "离开频道")), null);
        }

        mSignal.channelLeave(mChannelName);
        mSignal.logout();
    }

    @OnClick(R.id.btn_up)
    public void onBtnUpClicked() {
        if (mRole == CLIENT_ROLE_AUDIENCE) {
            Msg msg = new Msg(mRole, MsgType.MSG_TYPE_APPLY_HOST_IN, mAccount, "");
            // String channelID, String msg, String msgID
            mSignal.messageInstantSend(mOwnerAccount, 0, mGson.toJson(msg), null);
        } else if (mRole == CLIENT_ROLE_BROADCASTER) {
            getWorkerThread().getRtcEngine().setClientRole(CLIENT_ROLE_AUDIENCE);
            mSignal.messageChannelSend(mChannelName, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_CHANNEL_MSG, mAccount, "下麦")), null);
            mRole = CLIENT_ROLE_AUDIENCE;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnUp.setText("上麦");
                }
            });
        }
    }

    @OnClick(R.id.btn_mute)
    public void onBtnMuteClicked() {
        if ("闭麦".equals(btnMute.getText().toString())) {
            getWorkerThread().getRtcEngine().muteLocalAudioStream(true);
            btnMute.setText("开麦");
        } else {
            getWorkerThread().getRtcEngine().muteLocalAudioStream(false);
            btnMute.setText("闭麦");
        }
    }

    @OnClick(R.id.btn_msg_send)
    public void onBtnMsgSendClicked() {
        if (TextUtils.isEmpty(edtMsg.getText().toString())) {
            return;
        }
        Msg msg = new Msg(mRole, MsgType.MSG_TYPE_CHANNEL_MSG, mAccount, edtMsg.getText().toString());
        // String channelID, String msg, String msgID
        mSignal.messageChannelSend(mChannelName, mGson.toJson(msg), null);
        edtMsg.setText("");
    }

    @OnClick(R.id.btn_img)
    public void onBtnImgClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_selector);

        TextView txtSelectorTitle = alertDialog.findViewById(R.id.txt_selector_title);
        TextView txtSelectorDescription = alertDialog.findViewById(R.id.txt_selector_description);
        RecyclerView recyclerSelector = alertDialog.findViewById(R.id.recycler_selector);
        txtSelectorTitle.setText("请选择背景图片");
        txtSelectorDescription.setText("背景图片");
        // 初始化选择列表
        mImgSelectorRecyclerAdapter = new ImgSelectorRecyclerAdapter(this);
        GridLayoutManager imgLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerSelector.setLayoutManager(imgLayoutManager);
        recyclerSelector.setAdapter(mImgSelectorRecyclerAdapter);
        mImgSelectorRecyclerAdapter.setOnItemClickListener(new ImgSelectorRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mBgPosition = position;
                int id = getResources().getIdentifier("io.agora.agorachat:mipmap/bg_" + position, null, null);
                llChatRoomBg.setBackgroundResource(id);
                mSignal.messageChannelSend(mChannelName, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_CHANGE_IMG, mAccount, "" + position)), null);
            }
        });

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        window.setLayout(width, height * 2 / 3);
        window.setGravity(Gravity.BOTTOM);
    }

    @OnClick(R.id.btn_music)
    public void onBtnMusicClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_selector);

        TextView txtSelectorTitle = alertDialog.findViewById(R.id.txt_selector_title);
        TextView txtSelectorDescription = alertDialog.findViewById(R.id.txt_selector_description);
        RecyclerView recyclerSelector = alertDialog.findViewById(R.id.recycler_selector);
        txtSelectorTitle.setText("请选择背景音乐");
        txtSelectorDescription.setText("背景音乐");
        // 初始化选择列表
        mMusicSelectorRecyclerAdapter = new MusicSelectorRecyclerAdapter(this);
        GridLayoutManager musicLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerSelector.setLayoutManager(musicLayoutManager);
        recyclerSelector.setAdapter(mMusicSelectorRecyclerAdapter);
        mMusicSelectorRecyclerAdapter.setOnItemClickListener(new MusicSelectorRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                // String filePath, bool loopback, bool replace, int cycle
                getWorkerThread().getRtcEngine().startAudioMixing("/assets/music_" + position + ".mp3", false, false, -1);
            }
        });

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        window.setLayout(width, height * 2 / 3);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        // 登录信令
        // String appId, String account, String token, int uid, String deviceID, int retry_time_in_s, int retry_count
        mSignal.login2(getString(R.string.private_app_id), "" + (uid & 0xFFFFFFFFL), "_no_need_token", 0, null, 30, 3);
        mAccount = "" + (uid & 0xFFFFFFFFL);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtHostName.setText(mAccount);
            }
        });
    }

    private void addCallbackForSignal() {
        // 添加回调
        mSignal.callbackSet(new AbstractICallBack() {
            @Override
            public void onLoginSuccess(int i, int i1) {
                // int uid ( 固定填 0 ), int fd ( 仅供 Agora 内部使用 )
                super.onLoginSuccess(i, i1);
                Log.d("onLoginSuccess", "signal login success");

                mSignal.channelJoin(mChannelName);
            }

            /**
             * 其他用户加入频道回调
             *
             * @param s 客户端定义的用户账号 (account)
             * @param i 固定填 0 (uid)
             */
            @Override
            public void onChannelUserJoined(final String s, int i) {
                super.onChannelUserJoined(s, i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MsgListItem msgListItem = new MsgListItem(s, "加入频道");
                        mMsgListItems.add(msgListItem);
                        mMsgListRecyclerAdapter.notifyItemInserted(mMsgListItems.size() - 1);
                        recyclerMsgList.scrollToPosition(mMsgListItems.size() - 1);
                        // 增加头像
                        mUsrListItems.add(new Usr());
                        mUsrListRecyclerAdapter.notifyDataSetChanged();
                        txtNum.setText(mUsrListItems.size() + "人");
                        recyclerParticipants.scrollToPosition(mUsrListItems.size() - 1);
                        // 当有用户加入频道，发一条点对点消息告知该用户自己在频道内
                        mSignal.messageInstantSend(s, 0, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_IN_CHANNEL, mAccount, "加入频道")), null);
                        // 当有用户加入频道时，房主发一条点对点消息告诉用户当前的背景
                        mSignal.messageInstantSend(s, 0, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_CHANGE_IMG, mAccount, "" + mBgPosition)), null);
                    }
                });
            }

            /**
             * 其他用户离开频道回调
             *
             * @param s
             * @param i
             */
            @Override
            public void onChannelUserLeaved(final String s, int i) {
                super.onChannelUserLeaved(s, i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MsgListItem msgListItem = new MsgListItem(s, "离开频道");
                        mMsgListItems.add(msgListItem);
                        mMsgListRecyclerAdapter.notifyItemInserted(mMsgListItems.size() - 1);
                        recyclerMsgList.scrollToPosition(mMsgListItems.size() - 1);
                        // 删除头像
                        if (mUsrListItems != null && mUsrListItems.size() > 0) {
                            mUsrListItems.remove(0);
                            mUsrListRecyclerAdapter.notifyItemRemoved(0);
                            recyclerParticipants.scrollToPosition(0);
                            txtNum.setText(mUsrListItems.size() + "人");
                        }
                    }
                });
            }

            @Override
            public void onChannelJoined(String s) {
                // String channelID
                super.onChannelJoined(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 初始化频道内当前用户头像
                        mUsrListItems.add(new Usr());
                        mUsrListRecyclerAdapter.notifyDataSetChanged();
                        txtNum.setText(mUsrListItems.size() + "人");
                        recyclerParticipants.scrollToPosition(mUsrListItems.size() - 1);
                    }
                });
            }

            @Override
            public void onMessageChannelReceive(String s, final String s1, int i, final String s2) {
                // String channelID, String account, int uid ( 固定填 0 ), String msg
                super.onMessageChannelReceive(s, s1, i, s2);
                Log.d("onMessageChannelReceive", s2);
                final Msg msg = mGson.fromJson(s2, Msg.class);
                final int role = msg.getRole();
                switch (msg.getType()) {
                    case MsgType.MSG_TYPE_LEAVE_CHANNEL:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (role == AppConstants.CLIENT_ROLE_OWNER && mRole != AppConstants.CLIENT_ROLE_OWNER) {
                                    Toast.makeText(ChatRoomActivity.this, "房主注销了房间", Toast.LENGTH_SHORT).show();
                                    ChatRoomActivity.this.finish();
                                }
                            }
                        });
                        break;
                    case MsgType.MSG_TYPE_CHANNEL_MSG:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MsgListItem msgListItem = new MsgListItem(role == AppConstants.CLIENT_ROLE_OWNER ? s1 + "(房主)" : s1, mGson.fromJson(s2, Msg.class).getMsg());
                                mMsgListItems.add(msgListItem);
                                mMsgListRecyclerAdapter.notifyItemInserted(mMsgListItems.size() - 1);
                                recyclerMsgList.scrollToPosition(mMsgListItems.size() - 1);
                            }
                        });
                        break;
                    case MsgType.MSG_TYPE_CHANGE_IMG:
                        if (role == AppConstants.CLIENT_ROLE_OWNER) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int id = getResources().getIdentifier("io.agora.agorachat:mipmap/bg_" + msg.getMsg(), null, null);
                                    llChatRoomBg.setBackgroundResource(id);
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMessageInstantReceive(final String s, int i, final String s1) {
                // String account, int uid, String msg
                super.onMessageInstantReceive(s, i, s1);
                final Msg msg = mGson.fromJson(s1, Msg.class);
                final int role = msg.getRole();
                switch (msg.getType()) {
                    case MsgType.MSG_TYPE_APPLY_HOST_IN:
                        if (mRole == AppConstants.CLIENT_ROLE_OWNER) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MsgListItem msgListItem = new MsgListItem(s, "申请连麦");
                                    mMsgListItems.add(msgListItem);
                                    mMsgListRecyclerAdapter.notifyItemInserted(mMsgListItems.size() - 1);
                                    recyclerMsgList.scrollToPosition(mMsgListItems.size() - 1);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
                                    builder.setMessage(s + " 申请连麦")
                                            .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mSignal.messageInstantSend(s, 0, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_REPLY, mAccount, "1")), null);
                                                }
                                            })
                                            .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mSignal.messageInstantSend(s, 0, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_REPLY, mAccount, "0")), null);
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                            });
                        }
                        break;
                    case MsgType.MSG_TYPE_CHANGE_IMG:
                        if (role == AppConstants.CLIENT_ROLE_OWNER) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int id = getResources().getIdentifier("io.agora.agorachat:mipmap/bg_" + msg.getMsg(), null, null);
                                    llChatRoomBg.setBackgroundResource(id);
                                }
                            });
                        }
                        break;
                    case MsgType.MSG_TYPE_IN_CHANNEL:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 添加头像
                                Usr usr = new Usr(s1);
                                mUsrListItems.add(usr);
                                mUsrListRecyclerAdapter.notifyItemInserted(mUsrListItems.size() - 1);
                                txtNum.setText(mUsrListItems.size() + "人");
                                recyclerParticipants.scrollToPosition(mUsrListItems.size() - 1);

                                if (role == AppConstants.CLIENT_ROLE_OWNER && mRole != AppConstants.CLIENT_ROLE_OWNER) {
                                    mOwnerAccount = s;
                                }
                            }
                        });
                        break;
                    case MsgType.MSG_TYPE_REPLY:
                        if (mRole == CLIENT_ROLE_AUDIENCE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ("1".equals(msg.getMsg())) {
                                        Toast.makeText(ChatRoomActivity.this, "主播同意了您的连麦申请", Toast.LENGTH_SHORT).show();
                                        btnUp.setText("下麦");
                                        getWorkerThread().getRtcEngine().setClientRole(CLIENT_ROLE_BROADCASTER);
                                        mRole = CLIENT_ROLE_BROADCASTER;
                                        // String channelID, String msg, String msgID
                                        mSignal.messageChannelSend(mChannelName, mGson.toJson(new Msg(mRole, MsgType.MSG_TYPE_CHANNEL_MSG, mAccount, "上麦")), null);
                                    } else if ("0".equals(msg.getMsg())) {
                                        Toast.makeText(ChatRoomActivity.this, "主播拒绝了您的连麦申请", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
