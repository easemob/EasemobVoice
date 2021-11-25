package io.agora.agoravoice.im.resp;

import com.google.gson.Gson;

import java.io.Serializable;


public class CommonResp implements Serializable {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
