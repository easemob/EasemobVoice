package io.agora.agoravoice.im.message;

import android.net.Uri;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.agoravoice.R;

/**
 */
public class EmCustomMsgHelper{
    private static EmCustomMsgHelper instance;
    private EmCustomMsgHelper(){}

    private String chatRoomId;

    public static EmCustomMsgHelper getInstance() {
        if(instance == null) {
            synchronized (EmCustomMsgHelper.class) {
                if(instance == null) {
                    instance = new EmCustomMsgHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 根据业务要求，放在application或者其他需要初始化的地方
     */
    public void init() {
//        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    /**
     * 设置聊天室id
     * @param chatRoomId
     */
    public void setChatRoomInfo(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
    //

    public void addJoinMessage(String user){
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatRoomId);
        message.setFrom(user);
        EMTextMessageBody textMessageBody = new EMTextMessageBody("加入房间");
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute("member_add", true);
        EMClient.getInstance().chatManager().saveMessage(message);
    }
    public void addLeaveMessage(String user){
        EMMessage message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        message.setTo(chatRoomId);
        message.setFrom(user);
        EMTextMessageBody textMessageBody = new EMTextMessageBody("离开房间");
        message.addBody(textMessageBody);
        message.setChatType(EMMessage.ChatType.ChatRoom);
        message.setAttribute("member_add", true);
        EMClient.getInstance().chatManager().saveMessage(message);
    }
    /**
     * 发送礼物消息(多参数)
     * @param params
     * @param callBack
     */
    public void sendGiftMsg(Map<String, String> params, final OnMsgCallBack callBack) {
        if(params.size() <= 0) {
            return;
        }
        sendCustomMsg(EmCustomMsgType.CHATROOM_GIFT.getName(), params, callBack);
    }
    /**
     * 发送自定义消息
     * @param to
     * @param chatType
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String to, EMMessage.ChatType chatType, String event, Map<String, String> params, final OnMsgCallBack callBack) {
        final EMMessage sendMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
        EMCustomMessageBody body = new EMCustomMessageBody(event);
        body.setParams(params);
        sendMessage.addBody(body);
        sendMessage.setTo(to);
        sendMessage.setChatType(chatType);
        sendMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                if(callBack != null) {
                    callBack.onSuccess();
                    callBack.onSuccess(sendMessage);
                }
            }

            @Override
            public void onError(int i, String s) {
                if(callBack != null) {
                    callBack.onError(i, s);
                    callBack.onError(sendMessage.getMsgId(), i, s);
                }
            }

            @Override
            public void onProgress(int i, String s) {
                if(callBack != null) {
                    callBack.onProgress(i, s);
                }
            }
        });
        EMClient.getInstance().chatManager().sendMessage(sendMessage);
    }
    /**
     * 发送自定义消息
     * @param event
     * @param params
     * @param callBack
     */
    public void sendCustomMsg(String event, Map<String, String> params, final OnMsgCallBack callBack) {
        sendCustomMsg(chatRoomId, EMMessage.ChatType.ChatRoom, event, params, callBack);
    }

    public void sendTextMessage(String content) {
        EMLog.d("TAG","sendTextMessage  chatRoomId："+ chatRoomId+"   content:"+content);
        EMMessage message = EMMessage.createTxtSendMessage(content, chatRoomId);
        sendMessage(message);
    }


    public void sendImageMessage(Uri imageUri) {
        EMMessage message = EMMessage.createImageSendMessage(imageUri, false, chatRoomId);
        sendMessage(message);
    }

    public void sendMessage(EMMessage message) {
        if(message == null) {
            return;
        }
//        addMessageAttributes(message);
//        if (chatType == EaseConstant.CHATTYPE_GROUP){
//            message.setChatType(EMMessage.ChatType.GroupChat);
//        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
//            message.setChatType(EMMessage.ChatType.ChatRoom);
//        }

        message.setChatType(EMMessage.ChatType.ChatRoom);
        EMLog.d("TAG","ChatType："+ message.getChatType() +"   from :"+message.getFrom());
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.e("wyz","消息发送成功回调");
            }

            @Override
            public void onError(int code, String error) {

            }

            @Override
            public void onProgress(int progress, String status) {
            }
        });
        // send message
        EMClient.getInstance().chatManager().sendMessage(message);
    }
}
