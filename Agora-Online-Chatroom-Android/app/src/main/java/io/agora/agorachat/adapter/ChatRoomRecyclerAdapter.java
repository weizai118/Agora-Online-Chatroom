package io.agora.agorachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Random;

import io.agora.agorachat.R;

public class ChatRoomRecyclerAdapter extends RecyclerView.Adapter<ChatRoomRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    private int[] paths = {R.mipmap.bg_0, R.mipmap.bg_1, R.mipmap.bg_2,
            R.mipmap.bg_3, R.mipmap.bg_4, R.mipmap.bg_5, R.mipmap.bg_6};

    public ChatRoomRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ChatRoomRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_room_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imgBg.setImageBitmap(readBitMap(mContext, paths[position]));
        holder.txtName.setText("聊天室 " + position);
        holder.txtNum.setText((new Random().nextInt(10) + 1) + "人");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 7;
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgBg;
        public TextView txtName;
        public TextView txtNum;

        public ViewHolder(View view) {
            super(view);
            imgBg = view.findViewById(R.id.chat_room_img);
            txtName = view.findViewById(R.id.chat_room_txt_name);
            txtNum = view.findViewById(R.id.chat_room_num);
        }
    }

}
