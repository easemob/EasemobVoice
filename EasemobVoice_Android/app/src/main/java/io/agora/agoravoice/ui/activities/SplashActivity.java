package io.agora.agoravoice.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.List;

import io.agora.agoravoice.R;
import io.agora.agoravoice.manager.LeanCloudManager;
import io.agora.agoravoice.bean.UserBean;
import io.agora.agoravoice.business.definition.struct.AppVersionInfo;
import io.agora.agoravoice.business.definition.struct.BusinessType;
import io.agora.agoravoice.business.definition.struct.GiftInfo;
import io.agora.agoravoice.business.definition.struct.MusicInfo;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.ServerClient;
import io.agora.agoravoice.im.PreferenceManager;
import io.agora.agoravoice.im.body.User;
import io.agora.agoravoice.im.reponsitories.UserRepository;
import io.agora.agoravoice.im.viewmodels.LoginViewModel;
import io.agora.agoravoice.manager.ProxyManager;
import io.agora.agoravoice.ui.activities.main.MainActivity;
import io.agora.agoravoice.ui.views.CropBackgroundRelativeLayout;
import io.agora.agoravoice.utils.Const;
import io.agora.agoravoice.utils.RandomUtil;
import io.agora.agoravoice.utils.ToastUtil;
import io.agora.agoravoice.utils.WindowUtil;

public class SplashActivity extends BaseActivity implements
        ProxyManager.GeneralServiceListener,
        ProxyManager.UserServiceListener,ProxyManager.ImServiceListener {
    private Dialog mUpgradeDialog;
    private LoginViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        WindowUtil.hideStatusBar(getWindow(), false);
        viewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);

        initialize();
    }

    private void initialize() {
        initProxy();
        checkAppVersion();
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        CropBackgroundRelativeLayout bgLayout =
                findViewById(R.id.splash_bg_layout);
        bgLayout.setCropBackground(R.drawable.splash_bg);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        AppCompatImageView icon = findViewById(R.id.splash_icon);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) icon.getLayoutParams();
        params.topMargin = metrics.heightPixels * 2 / 11;
        params.width = metrics.widthPixels / 3;
        params.height = metrics.widthPixels / 3;
        icon.setLayoutParams(params);

        AppCompatTextView appName = findViewById(R.id.splash_app_name);
        params = (RelativeLayout.LayoutParams) appName.getLayoutParams();
        params.width = metrics.widthPixels / 2;
        appName.setLayoutParams(params);

        AppCompatTextView power = findViewById(R.id.powered_by_agora);
        params = (RelativeLayout.LayoutParams) power.getLayoutParams();
        params.bottomMargin = metrics.heightPixels / 10;
        power.setLayoutParams(params);
    }

    private void checkAppVersion() {
        proxy().checkVersion(getAppVersion());
    }

    private String getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initProxy() {
        proxy().addGeneralServiceListener(this);
        proxy().addUserServiceListener(this);
        proxy().addImServiceListener(this);
        proxy().getMusicList();
    }

    private void login() {
        initUserFromStorage();
        if (!config().isUserExisted()) {
            selectRandomNameIfNotExist();
            proxy().createUser(config().getNickname());
        } else {
            proxy().login(config().getUserId());
        }
    }

    private void initUserFromStorage() {
        config().setUserId(preferences().getString(Const.KEY_USER_ID, null));
        config().setNickname(preferences().getString(Const.KEY_USER_NAME, null));
        config().setUserToken(preferences().getString(Const.KEY_TOKEN, null));
    }

    private void selectRandomNameIfNotExist() {
        if (TextUtils.isEmpty(config().getNickname())) {
            config().setNickname(RandomUtil.randomUserName(this));
        }
    }

    @Override
    public void onCheckVersionSuccess(AppVersionInfo info) {
        Logging.d("onCheckVersionSuccess " + info.appVersion);
        if (info.forcedUpgrade == 0) {
            login();
            return;
        }

        runOnUiThread(() -> {
            if (info.forcedUpgrade == 2) {
                // force to upgrade
                mUpgradeDialog = showDialog(R.string.dialog_upgrade_force_title,
                        R.string.dialog_upgrade_force_message,
                        R.string.text_upgrade,
                        R.string.text_cancel,
                        () -> {
                            dismissUpgradeDialog();
                            gotoDownloadLink(info.upgradeUrl);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        },
                        () -> {
                            dismissUpgradeDialog();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                );
            } else if (info.forcedUpgrade == 1) {
                // recommend to upgrade
                mUpgradeDialog = showDialog(R.string.dialog_upgrade_recommend_title,
                        R.string.dialog_upgrade_recommend_message,
                        R.string.text_upgrade,
                        R.string.text_cancel,
                        () -> {
                            dismissUpgradeDialog();
                            gotoDownloadLink(info.upgradeUrl);
                        },
                        () -> {
                            dismissUpgradeDialog();
                            login();
                        }
                );
            }
        });
    }

    private boolean upgradeDialogShowing() {
        return mUpgradeDialog != null && mUpgradeDialog.isShowing();
    }

    private void dismissUpgradeDialog() {
        if (upgradeDialogShowing()) {
            mUpgradeDialog.dismiss();
        }
    }

    private void gotoDownloadLink(String link) {
        if (TextUtils.isEmpty(link)) {
            ToastUtil.showShortToast(SplashActivity.this, R.string.toast_link_empty);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(link);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onMusicList(List<MusicInfo> info) {
            config().updateMusicInfo(info);
    }

    @Override
    public void onGiftList(List<GiftInfo> info) {
        // Current use locally stored gift information
    }

    @Override
    public void onGeneralServiceFailed(int type, int code, String message) {
        if (type == BusinessType.CHECK_VERSION) {
            // If check version errors, nothing done and
            // continue to try login
            login();
        }
    }

    @Override
    public void onUserCreated(String userId, String userName) {
        Logging.i("agora voice application onUserCreated " +
                    userId + " " + config().getNickname());
            config().setUserId(userId);
            preferences().edit().putString(Const.KEY_USER_ID, userId).apply();
            preferences().edit().putString(Const.KEY_USER_NAME, config().getNickname()).apply();
            proxy().login(userId);
    }

    @Override
    public void onEditUser(String userId, String userName) {
        // No such operation in this Activity
    }

    @Override
    public void onLoginSuccess(String userId, String userToken, String rtmToken) {
        Logging.i("onLoginSuccess " + userToken);
        config().setUserToken(userToken);
        //TODO 初始化登录IM 登录成功并获取AdminToken
        skipToTarget();
//        gotoMainActivity();
    }


    private void skipToTarget() {
        Logging.i("skipToTarget " );
        //登录过
        if(EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().isSdkInited()){
            String currentUser = EMClient.getInstance().getCurrentUser();
            PreferenceManager.init(this, currentUser);
            Logging.i("skipToTarget    22 " );
            EMLog.e("SplashActivity", "skipToTarget  : "+ currentUser);
            proxy().getAdminToken();
//            DemoHelper.saveUserId();
//            DemoHelper.initDb();
           gotoMainActivity();
        }else {
            //创建IM账号
            createImUser();
        }
    }

    private void createImUser() {
        Logging.i("createImUser " );
//        ProgressDialog pd = new ProgressDialog(mContext);
//        pd.setMessage("请稍等...");
//        pd.setCanceledOnTouchOutside(false);
        Logging.i("createImUser 1  userid  " +config().getNickname());
        User user = new User();
        user.setNickname(config().getNickname());
        user.setId(config().getUserId());
        user.setUsername(config().getUserId());

        try {
            EMClient.getInstance().createAccount(user.getUsername(),UserRepository.getInstance().getDefaultPsw());
            //创建成功登录：
            EMClient.getInstance().login(user.getUsername(), UserRepository.getInstance().getDefaultPsw(), new EMCallBack() {
                @Override
                public void onSuccess() {
                    skipToTarget();
                    LeanCloudManager.getInstance().saveUserInfo(new UserBean(user.getUsername(),config().getNickname(),config().getNickname()));
                }

                @Override
                public void onError(int code, String message) {
                    Logging.i("onError 22  code  " +code + ":  message : "+message);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } catch (HyphenateException e) {
            e.printStackTrace();
            Logging.i("onError 22  e.getMessage()  " +e.getMessage());
        }
    }
    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserServiceFailed(int type, int code, String message) {
        if (code == ServerClient.ERROR_CONNECTION) {
            runOnUiThread(() -> ToastUtil.showShortToast(
                    application(), R.string.error_no_connection));
            return;
        }

        if (type == BusinessType.CREATE_USER) {
            String msg = "Create user fails " + code + " " + message;
            Logging.e(msg);
            runOnUiThread(() -> ToastUtil.showShortToast(this, msg));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proxy().removeGeneralServiceListener(this);
        proxy().removeUserServiceListener(this);
    }

    @Override
    public void onGetAdminTokenSuccess(String token) {
        PreferenceManager.getInstance().saveAdminToken(token);
    }

    @Override
    public void onCreateChatRoomSuccess(String imRoomID) { }

    @Override
    public void onImServiceFailed(int type, int code, String message) {
        if (code == ServerClient.ERROR_CONNECTION) {
            runOnUiThread(() -> ToastUtil.showShortToast(
                    application(), R.string.error_no_connection));
            return;
        }

        if (type == BusinessType.GET_ADMIN_TOKEN) {
            String msg = "Create user fails " + code + " " + message;
            Logging.e(msg);
            runOnUiThread(() -> ToastUtil.showShortToast(this, msg));
        }
    }
}
