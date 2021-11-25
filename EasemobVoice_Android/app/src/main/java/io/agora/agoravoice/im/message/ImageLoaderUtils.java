package io.agora.agoravoice.im.message;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import io.agora.agoravoice.R;


public class ImageLoaderUtils {
    private static ImageLoaderUtils instances=new ImageLoaderUtils();

    private ImageLoaderUtils(){

    }

    public static ImageLoaderUtils getInstances(){
        return instances;
    }


    public static boolean isDestroy(Context mActivity){
        if (mActivity instanceof Activity){
            if (mActivity == null || ((Activity)mActivity).isFinishing() ||
                    ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity)mActivity).isDestroyed())){
                return true;
            }
        }
        return false;
    }

    /**
     *  加载用户头像的配置
     * @param context
     * @return
     */
//    public RequestOptions getAvatarOptions(Context context){
//        RequestOptions requestOptions = new RequestOptions()
//                .transform(new RoundedCorners(4))
//                .placeholder(R.drawable.ease_default_avatar)
//                .error(R.drawable.ease_default_avatar)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .skipMemoryCache(false)
//                .dontAnimate();
//        return requestOptions;
//    }

    /**
     * 加载emoji表情
     * @param context
     * @param imageView
     * @param path
     */
    public void loadEmojiIcon(Context context, ImageView imageView, String path){
        Glide.with(context).load(path)
                .apply(getEmojiIconOptions(context)).into(imageView);
    }

    /**
     *
     * @param context
     * @return
     */
    public RequestOptions getEmojiIconOptions(Context context){
        RequestOptions requestOptions = new RequestOptions()
                .skipMemoryCache(false)
                .placeholder(R.drawable.ease_default_expression)
                .error(R.drawable.ease_default_expression)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return requestOptions;
    }

    /**
     * 加载emoji表情
     * @param context
     * @param imageView
     * @param path
     */
    public void loadImage(Context context, ImageView imageView, String path){
        Glide.with(context).load(path)
                .apply(getImageOptions(context)).into(imageView);
    }
    /**
     *
     * @param context
     * @return
     */
    public RequestOptions getImageOptions(Context context){
        RequestOptions requestOptions = new RequestOptions()
                .skipMemoryCache(false)
                .placeholder(R.drawable.ease_default_image)
                .error(R.drawable.ease_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        return requestOptions;
    }

}
