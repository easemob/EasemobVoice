package io.agora.agoravoice.im.reponsitories;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import io.agora.agoravoice.im.DemoHelper;
import io.agora.agoravoice.im.body.User;
import okhttp3.internal.http2.Header;

/**
 * 用于解析及获取模拟用户数据
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private UserRepository(){}
    private Context context;
    private List<User> mUsers;
    private User currentUser;
    private Map<String, User> userMap = new HashMap<>();

    public static UserRepository getInstance() {
        if(instance == null) {
            synchronized (UserRepository.class) {
                if(instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        String users = getJsonFromAssets("users.json");
        if(TextUtils.isEmpty(users)) {
            return;
        }
        mUsers = new Gson().fromJson(users, new TypeToken<ArrayList<User>>(){}.getType());
    }


    /**
     * 获取管理员token
     * @param tag
     */
//    public void getAdminToken(Object tag) {
//        CmosRequest<TokenResp> request = new CmosRequestOkImpl<TokenResp>();
//        UserTokenReq userTokenReq = new UserTokenReq();
//        userTokenReq.setClient_id(DemoConstants.CLIENT_ID);
//        userTokenReq.setClient_secret(DemoConstants.CLIENT_SECRET);
//        String tokenUrl = Utils.getTokenUrl();
//        EMLog.e(TAG, "getAdminToken:  = "+tokenUrl );
//        request.requestPostAsyncWithHeader(tokenUrl, userTokenReq.toString(), tag, null, new JsonCallback<TokenResp>(TokenResp.class) {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, TokenResp response) {
//                EMLog.e(TAG, "admin token:: onSuccess ");
//                PreferenceManager.getInstance().saveAdminToken(response.getAccess_token());
//            }
//
//            @Override
//            public void onError(int status, String errorMsg) {
//                EMLog.e(TAG, "admin token: onError = " + errorMsg + "; status " + status);
//            }
//        });
//    }

//    public void createChatRoom(String roomName,Object tag , JsonCallback<ChatRoomResp> callback) {
//        String currentUser = EMClient.getInstance().getCurrentUser();
//        String[] members = {currentUser};
//
//        CmosRequest request = new CmosRequestOkImpl();
//        String chatRoomUrl = Utils.createChatRoomUrl();
//        Log.e(TAG, "chatRoomUrl:  = "+chatRoomUrl );
//        ChatRoomReq chatRoomReq = new ChatRoomReq();
//        chatRoomReq.setName(roomName);
//        chatRoomReq.setMaxusers("5000");
//        chatRoomReq.setDescription(roomName);
//        chatRoomReq.setOwner(currentUser);
//        chatRoomReq.setMembers(members);
//
//        request.requestAsyncWithAdminToken(chatRoomUrl , chatRoomReq.toString(), tag, null, 1, callback);
//
//    }


    /**
     * 获取随机用户
     * @return
     */
//    public User getRandomUser() {
//        User user;
//        if(mUsers != null) {
//            user = mUsers.get(getRandom(mUsers.size()));
//        }else {
//            user = getDefaultUser();
//        }
//        user.setUsername(Utils.getStringRandom(8));
//        currentUser = user;
//        userMap.put(user.getUsername(), user);
//        return user;
//    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取默认密码
     * @return
     */
    public String getDefaultPsw() {
        return "000000";
    }

    /**
     * 获取默认用户
     * @return
     */
    private User getDefaultUser() {
        User user = new User();
        user.setId("hxtest");
        user.setNick("测试");
//        user.setAvatarResource(R.mipmap.em_avatar_1);
        return user;
    }

    /**
     * 通过环信id，寻找默认用户
     * @param username
     * @return
     */
    public User getUserByUsername(String username) {
        //先检查map中是否有数据
        if(userMap.keySet().contains(username)) {
            return userMap.get(username);
        }
        //再检查currentUser是否存在，以及是否是需要的
        if(currentUser != null && TextUtils.equals(username, currentUser.getUsername())) {
            userMap.put(username, currentUser);
            return currentUser;
        }
        //再检查是否是当前用户，如果是的话，检查是否有保存的用户id
        if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
            String userId = DemoHelper.getUserId();
            if(!TextUtils.isEmpty(userId)) {
                User user = getUserById(userId);
                if(user != null) {
                    currentUser = user;
                    userMap.put(username, user);
                    return user;
                }
            }
        }
        //如果以上均没有则返回一个随机用户数据
        if(mUsers != null) {
            User user = mUsers.get(getRandom(mUsers.size()));
            userMap.put(username, user);
            if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
                currentUser = user;
            }
            return user;
        }
        User defaultUser = getDefaultUser();
        if(TextUtils.equals(username, defaultUser.getUsername())) {
            userMap.put(username, defaultUser);
            if(TextUtils.equals(username, EMClient.getInstance().getCurrentUser())) {
                currentUser = defaultUser;
            }
            return defaultUser;
        }
        return null;
    }

    /**
     * 通过模拟数据id，返回用户数据
     * @param id
     * @return
     */
    public User getUserById(String id) {
        if(mUsers != null) {
            for(User user : mUsers) {
                if(TextUtils.equals(id, user.getId())) {
                    return user;
                }
            }
        }
        User defaultUser = getDefaultUser();
        if(TextUtils.equals(id, defaultUser.getId())) {
            return defaultUser;
        }
        return null;
    }

    /**
     * 获取随机数
     * @param max
     * @return
     */
    public int getRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public String getJsonFromAssets(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
