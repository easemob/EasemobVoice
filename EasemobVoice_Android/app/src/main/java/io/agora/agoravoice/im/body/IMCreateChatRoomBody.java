package io.agora.agoravoice.im.body;

import java.util.List;

public class IMCreateChatRoomBody extends CommonReq {
    private String name;
    private String description;
    private int maxusers;
    private String owner;
    private String[] members;

    public IMCreateChatRoomBody(String name, String description, int maxusers, String owner,  String[] members) {
        this.name = name;
        this.description = description;
        this.maxusers = maxusers;
        this.owner = owner;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxusers() {
        return maxusers;
    }

    public void setMaxusers(int maxusers) {
        this.maxusers = maxusers;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }
}
