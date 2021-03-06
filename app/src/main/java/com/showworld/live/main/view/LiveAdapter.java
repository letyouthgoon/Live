package com.showworld.live.main.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.showworld.live.R;
import com.showworld.live.base.ui.TAdapter;
import com.showworld.live.base.ui.TAdapterDelegate;
import com.showworld.live.main.HttpUtil;
import com.showworld.live.main.module.LiveInfo;
import com.showworld.live.main.module.UserInfo;
import com.showworld.live.main.module.getLiveListRet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by alex on 16/6/1.
 */
public class LiveAdapter extends TAdapter<LiveInfo> implements View.OnClickListener {

    private int mResourceId;
    private UserInfo userInfo;
    private LiveInfo liveVideoInfo;
    private ClipboardManager clip;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    //    String serverRootPath = "http://203.195.167.34/upload/";
    String serverRootPath = HttpUtil.SERVER_URL + "upload/";

    public LiveAdapter(Context context, int resourceId, List<LiveInfo> items, TAdapterDelegate delegate) {
        super(context, items, delegate);
        mResourceId = resourceId;
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.user)
//                .showImageForEmptyUri(R.drawable.user)
//                .showImageOnFail(R.drawable.user)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(mResourceId, null);

            holder = new ViewHolder();
            holder.imageViewCoverImage = (ImageView) convertView.findViewById(R.id.image_view_live_cover_image);
            holder.imageButtonUserLogo = (ImageButton) convertView.findViewById(R.id.image_btn_user_logo);
            holder.textViewUserName = (TextView) convertView.findViewById(R.id.text_view_user_name);
            holder.textViewLiveTitle = (TextView) convertView.findViewById(R.id.text_view_live_title);
            holder.textViewLiveViewer = (TextView) convertView.findViewById(R.id.text_view_live_viewer);
            holder.textViewLivePraise = (TextView) convertView.findViewById(R.id.text_view_live_praise);
//            shareButton = (Button) view.findViewById(R.id.Share);
//            shareButton.setOnClickListener(this);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        liveVideoInfo = getItem(position);
        userInfo = liveVideoInfo.getUserInfo();
        String param = liveVideoInfo.getCoverpath();
        String coverUrl = serverRootPath + param;
        if (param.length() > 0) {
            imageLoader.displayImage(coverUrl, holder.imageViewCoverImage, options, animateFirstListener);
        }

        holder.imageButtonUserLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), userInfo.getUserName(), Toast.LENGTH_SHORT).show();
            }
        });
        String headurl = HttpUtil.rootUrl + "?imagepath=" + liveVideoInfo.getHeadImagePath() + "&width=0&height=0";
        imageLoader.displayImage(headurl, holder.imageButtonUserLogo, options);
        holder.textViewUserName.setText("@" + liveVideoInfo.getUserName());
        holder.textViewLiveTitle.setText(liveVideoInfo.getLiveTitle());
        holder.textViewLiveViewer.setText("" + liveVideoInfo.getLiveViewerCount());
        holder.textViewLivePraise.setText("" + liveVideoInfo.getLivePraiseCount());
        return convertView;
    }

    public void clear() {
        items.clear();
    }

    public void addAll(List<LiveInfo> data) {
        items = data;
        notifyDataSetChanged();
    }


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Share:
                clip = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                clip.setText(liveVideoInfo.getmShareUrl());
                Toast.makeText(context, clip.getText(), Toast.LENGTH_SHORT).show();
        }
    }


    static class ViewHolder {
        public TextView textViewLiveTitle;
        public ImageButton imageButtonUserLogo;
        public ImageView imageViewCoverImage;
        public TextView textViewLiveViewer;
        public TextView textViewLivePraise;
        public TextView textViewUserName;

    }
}
