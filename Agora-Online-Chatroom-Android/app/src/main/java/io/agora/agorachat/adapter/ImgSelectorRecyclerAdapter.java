package io.agora.agorachat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.agorachat.R;
import io.agora.agorachat.bean.MsgListItem;
import io.agora.agorachat.utils.BitmapUtils;

public class ImgSelectorRecyclerAdapter extends RecyclerView.Adapter<ImgSelectorRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public ImgSelectorRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ImgSelectorRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.selector_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int id = mContext.getResources().getIdentifier("io.agora.agorachat:mipmap/bg_" + position, null, null);
        holder.img.setImageBitmap(BitmapUtils.readBitMap(mContext, id));
        holder.name.setText("背景 " + position);

        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView img;
        public TextView name;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img_selector_item);
            name = view.findViewById(R.id.txt_selector_item_name);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

}
