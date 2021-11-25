package io.agora.agoravoice.im;

import android.text.TextUtils;

import com.hyphenate.chat.EMClient;

import io.agora.agoravoice.AgoraApplication;
import io.agora.agoravoice.im.body.User;
import io.agora.agoravoice.im.reponsitories.UserRepository;


public class DemoHelper {


    /**
     * 是否显示登陆注册
     * @return
     */
    public static boolean isCanRegister() {
        return PreferenceManager.isCanRegister();
    }

    /**
     * 初始化数据库
     */
    public static void initDb() {
        DemoDbHelper.getInstance(AgoraApplication.getInstance()).initDb(EMClient.getInstance().getCurrentUser());
    }

    /**
     * 获取用户的昵称
     * @param username
     * @return
     */
    public static String getNickName(String username) {
        User user = UserRepository.getInstance().getUserByUsername(username);
        if(user == null) {
            return username;
        }
        if (TextUtils.isEmpty(user.getNickname())){
            return username;
        }
        return user.getNickname();
    }


    /**
     * 获取当前用户数据（模拟数据）
     * @return
     */
    public static User getCurrentDemoUser() {
        User user = UserRepository.getInstance().getCurrentUser();
        if(user == null) {
            user = UserRepository.getInstance().getUserByUsername(EMClient.getInstance().getCurrentUser());
        }
        return user;
    }

    /**
     * 保存当前用户相关联的id
     */
    public static void saveUserId() {
//        PreferenceManager.getInstance().saveUserId(getCurrentDemoUser().getId());
    }

    /**
     * 清除用户id
     */
    public static void clearUserId() {
        PreferenceManager.getInstance().saveUserId("");
    }

    public static String getUserId() {
        return PreferenceManager.getInstance().getUserId();
    }
}
