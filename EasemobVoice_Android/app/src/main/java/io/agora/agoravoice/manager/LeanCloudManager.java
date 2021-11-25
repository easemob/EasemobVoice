package io.agora.agoravoice.manager;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.util.EMLog;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.agora.agoravoice.bean.RoomBean;
import io.agora.agoravoice.bean.UserBean;
import io.agora.agoravoice.im.PreferenceManager;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LeanCloudManager {
    public static final  String ROOM_INFO ="RoomBean";
    public static final  String ROOM_ID ="roomId";
    public static final  String IM_ROOM_ID ="imRoomId";
    public static final  String ROOM_NAME ="RoomName";


    public static final  String USER_INFO ="user_bean";
    public static final  String USER_ID ="user_id";
    public static final  String USER_NAME ="user_name";
    public static final  String USER_AVATAR ="user_avatar";

    private LeanCloudManager(){}
    private static LeanCloudManager instance;
    public static LeanCloudManager getInstance(){
        if (instance == null) {
            instance = new LeanCloudManager();
        }
        return instance;
    }


    public void saveRoomInfo(RoomBean roomBean){
        LCObject room = new LCObject(ROOM_INFO);
        room.put(ROOM_ID, roomBean.roomId);
        room.put(IM_ROOM_ID, roomBean.imRoomId);
        room.put(ROOM_NAME,roomBean.imRoomName);

        // 将对象保存到云端
        room.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // 成功保存之后，执行其他逻辑
               EMLog.e("saveRoomInfo","保存成功。objectId：" + todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                EMLog.e("saveRoomInfo","保存失败");
            }
            public void onComplete() {}
        });
    }

    public void getRoomInfo(String roomId, EMValueCallBack<RoomBean> callBack){
        LCQuery<LCObject> query = new LCQuery<>(ROOM_INFO);
        query.whereEqualTo(ROOM_ID, roomId);
        query.getFirstInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject room) {
                RoomBean roomBean = new RoomBean();
                roomBean.roomId =  room.getString(ROOM_ID);
                roomBean.imRoomId =  room.getString(IM_ROOM_ID);
                roomBean.imRoomName =  room.getString(ROOM_NAME);
                callBack.onSuccess(roomBean);
            }
            public void onError(Throwable throwable) {
                callBack.onError(-1,"获取 Room 信息失败!");
            }
            public void onComplete() {}
        });
    }

    //用户信息相关 开始
    public void saveUserInfo(UserBean userBean){
        LCObject userInfo = new LCObject(USER_INFO);
        userInfo.put(USER_ID, userBean.uId);
        userInfo.put(USER_NAME, userBean.uName);
        userInfo.put(USER_AVATAR,userBean.uAvatar);
        // 将对象保存到云端
        userInfo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject todo) {
                // 成功保存之后，执行其他逻辑
                EMLog.e("saveUserInfo","保存成功。objectId：" + todo.getObjectId());
                PreferenceManager.getInstance().saveUserObjId(userBean.uId,todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                EMLog.e("saveUserInfo","保存失败");
            }
            public void onComplete() {}
        });
    }

    public void getUserInfo(String uId, EMValueCallBack<UserBean> callBack){
        LCQuery<LCObject> query = new LCQuery<>(USER_INFO);
        query.whereEqualTo(USER_ID, uId);
        query.getFirstInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject user) {
                UserBean userBean = new UserBean();
                userBean.uId = user.getString(USER_ID);
                userBean.uName = user.getString(USER_NAME);
                userBean.uAvatar = user.getString(USER_AVATAR);
                callBack.onSuccess(userBean);
            }
            public void onError(Throwable throwable) {
                callBack.onError(-1,"获取 用户 信息失败!");
            }
            public void onComplete() {}
        });
    }

    public void updateUserInfo(String objId,String name){
        LCObject userInfo = LCObject.createWithoutData(USER_INFO, objId);
        userInfo.put(USER_NAME, name);
        userInfo.saveInBackground().subscribe(new Observer<LCObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCObject savedTodo) {
                EMLog.e("updateUserInfo","更新成功 "+savedTodo.getObjectId());
            }
            public void onError(Throwable throwable) {
                EMLog.e("updateUserInfo","更新失败 ");
            }
            public void onComplete() {}
        });;
    }


    //用户信息相关 end



}
