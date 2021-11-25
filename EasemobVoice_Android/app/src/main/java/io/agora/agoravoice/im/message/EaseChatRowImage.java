package io.agora.agoravoice.im.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;


import java.io.File;

import io.agora.agoravoice.AgoraApplication;
import io.agora.agoravoice.R;
import io.agora.agoravoice.manager.LeanCloudManager;
import io.agora.agoravoice.bean.UserBean;
import io.agora.agoravoice.utils.WindowUtil;

public class EaseChatRowImage extends EaseChatRowFile{

    private String TAG = "EaseChatRowImage";

    protected ChatImage imageView;
    private EMImageMessageBody imgBody;

    public EaseChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_received_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ChatImage) findViewById(R.id.image);
    }


    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();
        if (message.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
            usernickView.setText(AgoraApplication.getInstance().config().getNickname() + ":");
        } else {
            LeanCloudManager.getInstance().getUserInfo(message.getFrom(), new EMValueCallBack<UserBean>() {
                @Override
                public void onSuccess(UserBean userBean) {
                    usernickView.setText(userBean.uName + ":");
                }

                @Override
                public void onError(int i, String s) {

                }
            });

        }

        // received messages
        if (message.direct() == EMMessage.Direct.RECEIVE) {
//            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
//            return;
        }

        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageViewGlide(thumbPath, filePath, message);
    }




    @Override
    protected void onViewUpdate(EMMessage msg) {
        if (msg.direct() == EMMessage.Direct.SEND) {
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                super.onViewUpdate(msg);
            }else{
                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
//                    progressBar.setVisibility(View.INVISIBLE);
                    percentageView.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(R.drawable.ease_default_image);
                } else {
//                    progressBar.setVisibility(View.GONE);
                    percentageView.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.ease_default_image);
                    String thumbPath = imgBody.thumbnailLocalPath();
                    if (!new File(thumbPath).exists()) {
                        // to make it compatible with thumbnail received in previous version
                        thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                    }
//                    showImageView(thumbPath, imgBody.getLocalUrl(), message);
                    showImageViewGlide(thumbPath, imgBody.getLocalUrl(), message);

                }
            }
            return;
        }

        // received messages
        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                imageView.setImageResource(R.drawable.ease_default_image);
            }else {
//                progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.ease_default_image);
            }
        } else if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
//            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
//            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
//                progressBar.setVisibility(View.VISIBLE);
//                percentageView.setVisibility(View.VISIBLE);
//            }else {
//                progressBar.setVisibility(View.INVISIBLE);
//                percentageView.setVisibility(View.INVISIBLE);
//            }
        } else {
//            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ease_default_image);
            String thumbPath = imgBody.thumbnailLocalPath();
            showImageViewGlide(thumbPath,imgBody.getRemoteUrl(),message);
        }
    }

    /**
     * load image into image view
     *
     */
    private void showImageViewGlide(final String thumbernailPath, final String localFullSizePath, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache s
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            Log.d(TAG, "showImageViewGlide, show thumbernailPath " + thumbernailPath);
            // thumbnail image is already loaded, reuse the drawable
//            imageView.setImageBitmap(bitmap);
            setImageSize(imageView, zoomImage(bitmap));
        } else {
            Log.d(TAG, "thumbnailDownloadStatus, show ease_default_image");

            if (message.direct() == EMMessage.Direct.SEND) {
                if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                    Glide.with(this)
                            .load(localFullSizePath)
                            .thumbnail(/*sizeMultiplier=*/ 0.4f)
                            .apply(ImageLoaderUtils.getInstances().getImageOptions(context))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Bitmap toBitamp = drawableToBitamp(resource);
                                    setImageSize(imageView, zoomImage(toBitamp));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                }
                            });
                }
            } else {
                Glide.with(this)
                            .load(imgBody.getRemoteUrl())
                            .thumbnail(Glide.with(this)
                                    .load(thumbernailPath))
                            .apply(ImageLoaderUtils.getInstances().getImageOptions(context))
                            .into(imageView);

            }
        }
    }
    private Bitmap drawableToBitamp(Drawable drawable)
    {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    public void setImageSize(ChatImage imageView, Bitmap bitmap) {
        EMLog.d("CMChatRowImage", "bitmap.getHeight " + bitmap.getHeight() + " bitmap.getWidth()  " + bitmap.getWidth());
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        lp.height = bitmap.getHeight();
        lp.width = bitmap.getWidth();
        //6. 发送的图片尺寸：横图 定宽120，竖图 定高200，超过规定大小即等比缩小，不超过即显示原图
        if (bitmap.getHeight() > 200 && bitmap.getWidth() > 120) {//超过规定缩放
            lp.height = 200;
            lp.width = bitmap.getWidth()*200 / bitmap.getHeight();
        }

        if (bitmap.getHeight() > 200 && bitmap.getWidth() < 120){ //竖图
            lp.height = 200;
            lp.width = bitmap.getWidth()*200 / bitmap.getHeight();
        }

        if (bitmap.getHeight() < 200 && bitmap.getWidth() > 120){ //横图
            lp.width = 120;
            lp.height = bitmap.getHeight() * 120/bitmap.getWidth();
        }
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(bitmap);
    }

    public Bitmap zoomImage(Bitmap bgimage) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        float screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        float screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        float whscale = width / height;

        int newWidth;
        if (whscale < 1) {
            newWidth = (int) (0.21 * screenWidth);
        } else {
            newWidth = (int) (0.42 * screenWidth);
        }
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        if (width > screenWidth) {
            width = screenWidth;
        }

        float scaleWidth = ((float) newWidth) / width;

        //float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
