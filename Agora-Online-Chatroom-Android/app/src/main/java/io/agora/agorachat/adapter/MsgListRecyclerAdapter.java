package io.agora.agorachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.agorachat.R;
import io.agora.agorachat.bean.MsgListItem;
import io.agora.agorachat.utils.BitmapUtils;

public class MsgListRecyclerAdapter extends RecyclerView.Adapter<MsgListRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<MsgListItem> mItems;

    private int[] paths = {R.mipmap.portrait_0, R.mipmap.portrait_1, R.mipmap.portrait_2, R.mipmap.portrait_3};

    public MsgListRecyclerAdapter(Context context, List<MsgListItem> items) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    @Override
    public MsgListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.msg_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imgPortrait.setImageBitmap(BitmapUtils.readBitMap(mContext, paths[new Random().nextInt(3)]));
        holder.txtUsrName.setText(mItems.get(position).getUsrName());
        holder.txtMsgContent.setText(mItems.get(position).getMsgContent());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgPortrait;
        public TextView txtUsrName;
        public TextView txtMsgContent;

        public ViewHolder(View view) {
            super(view);
            imgPortrait = view.findViewById(R.id.img_usr_portrait);
            txtUsrName = view.findViewById(R.id.txt_usr_name);
            txtMsgContent = view.findViewById(R.id.txt_msg_content);
        }
    }

}
