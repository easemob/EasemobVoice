package io.agora.agoravoice.business.server.retrofit.listener;

public interface IMServiceListener {
    void onGetAdminTokenSuccess(String token);

    void onCreateChatRoomSuccess(String imRoomID);

    void onImServiceFailed(int type, int code, String msg);
}
