package io.agora.agorachat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.agorachat.constants.AppConstants;
import io.agora.agorachat.R;
import io.agora.agorachat.adapter.ChatRoomRecyclerAdapter;
import io.agora.agorachat.base.BaseActivity;

import static io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE;

public class ChatRoomListActivity extends BaseActivity {

    @BindView(R.id.chat_room_recycler_view)
    RecyclerView chatRoomRecyclerView;
    @BindView(R.id.btn_create_room)
    Button btnCreateRoom;
    @BindView(R.id.btn_join_room)
    Button btnJoinRoom;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_room_list);
        ButterKnife.bind(this);

        mInflater = this.getLayoutInflater();

        chatRoomRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        chatRoomRecyclerView.setLayoutManager(mLayoutManager);
        chatRoomRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(20), true));

        mAdapter = new ChatRoomRecyclerAdapter(this);
        chatRoomRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {

    }

    @OnClick(R.id.btn_create_room)
    public void onBtnCreateRoomClicked() {
        View view = mInflater.inflate(R.layout.dialog_create_room, null);
        final EditText edtChatTopic = view.findViewById(R.id.edt_chat_topic);
        final EditText edtChannelName = view.findViewById(R.id.edt_channel_name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建房间")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (edtChatTopic.getText().toString().isEmpty()){
                            Toast.makeText(ChatRoomListActivity.this, "聊天话题不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (edtChannelName.getText().toString().isEmpty()){
                            Toast.makeText(ChatRoomListActivity.this, "频道名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(ChatRoomListActivity.this, ChatRoomActivity.class);
                        intent.putExtra(AppConstants.ACTION_KEY_CHANNEL_NAME, edtChannelName.getText().toString());
                        intent.putExtra(AppConstants.ACTION_KEY_CHAT_TOPIC, edtChatTopic.getText().toString());
                        intent.putExtra(AppConstants.ACTION_KEY_CLIENT_ROLE, AppConstants.CLIENT_ROLE_OWNER);
                        ChatRoomListActivity.this.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    @OnClick(R.id.btn_join_room)
    public void onBtnJoinRoomClicked() {
        View view = mInflater.inflate(R.layout.dialog_join_room, null);
        final EditText edtRoomId = view.findViewById(R.id.edt_room_id);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("加入房间")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (edtRoomId.getText().toString().isEmpty()){
                            Toast.makeText(ChatRoomListActivity.this, " ROOM ID 不能为空 ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(ChatRoomListActivity.this, ChatRoomActivity.class);
                        intent.putExtra(AppConstants.ACTION_KEY_CHANNEL_NAME, edtRoomId.getText().toString());
                        intent.putExtra(AppConstants.ACTION_KEY_CLIENT_ROLE, CLIENT_ROLE_AUDIENCE);
                        ChatRoomListActivity.this.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
