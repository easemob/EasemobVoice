package io.agora.agoravoice.manager;

import androidx.annotation.NonNull;

import java.util.List;

import io.agora.agoravoice.business.BusinessProxy;

public class IMManager {
    private BusinessProxy mProxy;

    public IMManager(@NonNull BusinessProxy proxy) {
        mProxy = proxy;
    }

    public void getAdminToken(){
        mProxy.getAdminToken();
    }

    public void createChatRoom(String subject, String description, String welcomeMessage,
                               int maxUserCount, List<String> members){
        mProxy.createChatRoom(subject,description,welcomeMessage,maxUserCount,members);
    }



}
