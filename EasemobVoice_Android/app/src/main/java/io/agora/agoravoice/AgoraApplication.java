package io.agora.agoravoice;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

//import com.tencent.bugly.crashreport.CrashReport;

import com.elvishew.xlog.XLog;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.leancloud.LeanCloud;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.im.EaseIM;
import io.agora.agoravoice.im.LiveDataBus;
import io.agora.agoravoice.manager.ProxyManager;
import io.agora.agoravoice.ui.activities.ChatRoomActivity;
import io.agora.agoravoice.ui.activities.ImChatRoomActivity;
import io.agora.agoravoice.ui.views.EaseEmojicon;
import io.agora.agoravoice.ui.views.EaseEmojiconGroupEntity;
import io.agora.agoravoice.ui.views.EaseEmojiconInfoProvider;
import io.agora.agoravoice.ui.views.EmojiconExampleGroupData;
import io.agora.agoravoice.utils.Const;
import okhttp3.OkHttpClient;

public class AgoraApplication extends Application {
    private static final String TAG = AgoraApplication.class.getSimpleName();
    private ProxyManager mProxy;
    private Config mConfig;
    private SharedPreferences mPreferences;

    private static AgoraApplication instance;
    public static AgoraApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // Log must be initialized before 
        // all other functions
        instance = this;
        Logging.init(this);
        // 提供 this、App ID、App Key、Server Host 作为参数
        // 注意这里千万不要调用 cn.leancloud.core.LeanCloud 的 initialize 方法，否则会出现 NetworkOnMainThread 等错误。
        LeanCloud.initialize(this, "", "", "");
        initOkHttp();
        initChatSdk();
        initGlobalVariables();
        initBugly();
    }

    private void initGlobalVariables() {
        mPreferences = getSharedPreferences(Const.SP_NAME, Context.MODE_PRIVATE);
        mConfig = new Config();
        mProxy = new ProxyManager(this);
    }

    public ProxyManager proxy() {
        return mProxy;
    }

    public Config config() {
        return mConfig;
    }

    public SharedPreferences preferences() {
        return mPreferences;
    }

    private void initBugly() {
//        CrashReport.initCrashReport(getApplicationContext(),
//                getResources().getString(R.string.bugly_app_id), false);
    }
    private static OkHttpClient client;

    public static OkHttpClient getDefaultRequestClient() {
        return client;
    }

    private void initOkHttp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void initChatSdk(){
        EMOptions options = new EMOptions();
//        options.enableDNSConfig(false);
//        options.setRestServer("a1-hsb.easemob.com");
//        options.setIMServer("106.75.100.247");
//        options.setImPort(6717);

        EmClientInit(this, options);

        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);

        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override public void onConnected() {
                LiveDataBus.get().with(Config.NETWORK_CONNECTED).postValue(true);
            }

            @Override public void onDisconnected(int errorCode) {
                if(errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE)
                {
//                    Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    Intent intent = new Intent(getApplicationContext(), ImChatRoomActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("conflict", true);
                    startActivity(intent);
                }
            }
        });
    }

    private void EmClientInit(AgoraApplication context, EMOptions options) {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(context, pid);

        Logging.d("process app name : " + processAppName);

        // if there is application has remote service, application:onCreate() maybe called twice
        // this check is to make sure SDK will initialized only once
        // return if process name is not application's name since the package name is the default process name
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            Logging.e( "enter the service process!");
            return;
        }
        if(options == null){
            EMClient.getInstance().init(context, initChatOptions());
        }else{
            EMClient.getInstance().init(context, options);
        }
        EaseIM.getInstance().setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for(EaseEmojicon emojicon : data.getEmojiconList()){
                    if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        });

    }

    private EMOptions initChatOptions() {
        Logging.d("init HuanXin Options");

        EMOptions options = new EMOptions();
        Logging.d("init HuanXin Options     " +  options.getRestServer());

        // change to need confirm contact invitation
        options.setAcceptInvitationAlways(false);
        // set if need read ack
        options.setRequireAck(true);
        // set if need delivery ack
        options.setRequireDeliveryAck(false);

        return options;
    }

    /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(Context context, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mProxy != null) mProxy.release();
    }
}
