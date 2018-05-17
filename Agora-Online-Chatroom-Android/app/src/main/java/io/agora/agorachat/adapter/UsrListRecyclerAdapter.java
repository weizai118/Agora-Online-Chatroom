package io.agora.agorachat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.agorachat.R;
import io.agora.agorachat.bean.Usr;

public class UsrListRecyclerAdapter extends RecyclerView.Adapter<UsrListRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    private List<Usr> mItems;

    private int[] paths = {R.mipmap.portrait_0, R.mipmap.portrait_1, R.mipmap.portrait_2, R.mipmap.portrait_3};

    public UsrListRecyclerAdapter(Context context, List<Usr> items) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    @Override
    public UsrListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.participants_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imgUsrPortrait.setImageBitmap(readBitMap(mContext, paths[new Random().nextInt(3)]));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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
        public CircleImageView imgUsrPortrait;

        public ViewHolder(View view) {
            super(view);
            imgUsrPortrait = view.findViewById(R.id.usr_list_img_usr_portrait);
        }
    }

}
