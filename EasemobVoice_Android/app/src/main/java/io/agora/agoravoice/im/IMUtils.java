package io.agora.agoravoice.im;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

public class IMUtils {

    public static String getTokenUrl(){
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        String[] keyArrys = appKey.split("#");
        String restServer = EMClient.getInstance().getOptions().getRestServer();
        EMLog.e("TokenUrl", "getTokenUrl: "+restServer );
        if (TextUtils.isEmpty(restServer)){
            restServer = "https://a4-v2.easemob.com:443";
        }
        EMLog.e("TokenUrl", "getTokenUrl: 2 "+restServer );
        return   "";
    }


    public static String createChatRoomUrl(){
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        String[] keyArrys = appKey.split("#");
        String restServer = EMClient.getInstance().getOptions().getRestServer();
        EMLog.e("createChatRoomUrl", "createChatRoomUrl: "+restServer );
        if (TextUtils.isEmpty(restServer)){
            restServer = "https://a4-v2.easemob.com:443";
        }
        return restServer + "/" + keyArrys[0] + "/" + keyArrys[1] + "/chatrooms";
    }

    public static String getChatRoomInfo(String imRoomid){
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        String[] keyArrys = appKey.split("#");
        String restServer = EMClient.getInstance().getOptions().getRestServer();
        if (TextUtils.isEmpty(restServer)){
            restServer = "https://a4-v2.easemob.com:443";
        }
        return restServer + "/" + keyArrys[0] + "/" + keyArrys[1] + "/chatrooms/"+imRoomid;
    }
}
