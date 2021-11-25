package io.agora.agoravoice.im.body;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by wei on 2017/2/14.
 */

public class User implements Serializable {
    private String username;
    private String nick;
    private String avatar;
    private int avatarResource;
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNick() {
        return nick;
    }

    public String getNickname() {
        if(TextUtils.isEmpty(nick)) {
            return username;
        }
        return nick;
    }

    public void setNickname(String nick) {
        this.nick = nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAvatarResource() {
        return avatarResource;
    }

    public void setAvatarResource(int avatarResource) {
        this.avatarResource = avatarResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
