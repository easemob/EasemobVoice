package io.agora.agoravoice.ui.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;

import io.agora.agoravoice.AgoraApplication;
import io.agora.agoravoice.Config;
import io.agora.agoravoice.R;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.im.OnResourceParseCallback;
import io.agora.agoravoice.im.ThreadManager;
import io.agora.agoravoice.im.reponsitories.Resource;
import io.agora.agoravoice.im.reponsitories.Status;
import io.agora.agoravoice.manager.ProxyManager;
import io.agora.agoravoice.utils.ToastUtil;
import io.agora.agoravoice.utils.WindowUtil;

public abstract class BaseActivity extends AppCompatActivity {

    protected final LifecycleProvider<Lifecycle.Event> mLifecycleProvider = AndroidLifecycle.createLifecycleProvider(this);
    protected int systemBarHeight;
    public BaseActivity mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setGlobalLayoutListener();
        systemBarHeight = WindowUtil.getStatusBarHeight(this);
    }

    private void setGlobalLayoutListener() {
        final View layout = findViewById(Window.ID_ANDROID_CONTENT);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onGlobalLayoutCompleted();
            }
        });
    }

    protected void onGlobalLayoutCompleted() {

    }

    protected void keepScreenOn(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected AgoraApplication application() {
        return (AgoraApplication) getApplication();
    }

    protected ProxyManager proxy() {
        return application().proxy();
    }

    protected SharedPreferences preferences() {
        return application().preferences();
    }

    protected Config config() {
        return application().config();
    }

    protected Dialog showDialog(String title, String message,
                                String positiveText, String negativeText,
                                final Runnable positiveClick,
                                final Runnable negativeClick) {
        final Dialog dialog = new Dialog(this, R.style.dialog_center);
        dialog.setContentView(R.layout.agora_voice_dialog_message);
        AppCompatTextView titleTextView = dialog.findViewById(R.id.dialog_title);
        titleTextView.setText(title);

        AppCompatTextView msgTextView = dialog.findViewById(R.id.dialog_message);
        msgTextView.setText(message);

        AppCompatTextView positiveButton = dialog.findViewById(R.id.dialog_positive_button);
        positiveButton.setText(positiveText);
        positiveButton.setOnClickListener(view -> positiveClick.run());

        AppCompatTextView negativeButton = dialog.findViewById(R.id.dialog_negative_button);
        negativeButton.setText(negativeText);
        negativeButton.setOnClickListener(view -> negativeClick.run());

        WindowUtil.hideStatusBar(dialog.getWindow(), false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    protected Dialog showDialog(int title, int message,
                                int positiveText, int negativeText,
                                final Runnable positiveClick,
                                final Runnable negativeClick) {
        Resources res = getResources();
        String t = res.getString(title);
        String m = res.getString(message);
        String p = res.getString(positiveText);
        String n = res.getString(negativeText);
        return showDialog(t, m, p, n, positiveClick, negativeClick);
    }

    protected Dialog showDialog(String title,
                                String positiveText, String negativeText,
                                final Runnable positiveClick,
                                final Runnable negativeClick) {
        final Dialog dialog = new Dialog(this, R.style.dialog_center);
        dialog.setContentView(R.layout.agora_voice_dialog);
        AppCompatTextView titleTextView = dialog.findViewById(R.id.dialog_title);
        titleTextView.setText(title);

        AppCompatTextView positiveButton = dialog.findViewById(R.id.dialog_positive_button);
        positiveButton.setText(positiveText);
        positiveButton.setOnClickListener(view -> positiveClick.run());

        AppCompatTextView negativeButton = dialog.findViewById(R.id.dialog_negative_button);
        negativeButton.setText(negativeText);
        negativeButton.setOnClickListener(view -> negativeClick.run());

        WindowUtil.hideStatusBar(dialog.getWindow(), false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    protected Dialog showDialog(int title,
                                int positiveText, int negativeText,
                                final Runnable positiveClick,
                                final Runnable negativeClick) {
        Resources res = getResources();
        String t = res.getString(title);
        String p = res.getString(positiveText);
        String n = res.getString(negativeText);
        return showDialog(t, p, n, positiveClick, negativeClick);
    }

    protected Dialog showOneButtonDialog(String message,
                                         String positiveText,
                                         final Runnable positiveClick,
                                         boolean cancelable) {
        final Dialog dialog = new Dialog(this, R.style.dialog_center);
        dialog.setContentView(R.layout.agora_voice_dialog_simple);

        AppCompatTextView msgTextView = dialog.findViewById(R.id.dialog_message);
        msgTextView.setText(message);

        AppCompatTextView positiveButton = dialog.findViewById(R.id.dialog_positive_button);
        positiveButton.setText(positiveText);
        positiveButton.setOnClickListener(view -> positiveClick.run());

        WindowUtil.hideStatusBar(dialog.getWindow(), false);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.show();
        return dialog;
    }

    protected Dialog showOneButtonDialog(int messageRes, int positiveText,
                                         final Runnable positiveClick,
                                         boolean cancelable) {
        Resources res = getResources();
        String message = res.getString(messageRes);
        String positive = res.getString(positiveText);
        return showOneButtonDialog(message, positive, positiveClick, cancelable);
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        Logging.i("parseResource 1  (response == null)  " +(response == null));
        if(response == null) {
            return;
        }
        if(response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        }else if(response.status == Status.ERROR) {
            ThreadManager.getInstance().runOnMainThread(()-> {
                callback.hideLoading();
                if(!callback.hideErrorMsg) {
                    ToastUtil.showShortToast(this,response.getMessage());
                }
                callback.onError(response.errorCode, response.getMessage());
            });

        }else if(response.status == Status.LOADING) {
            callback.onLoading();
        }
    }
}
