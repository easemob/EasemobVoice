package io.agora.agoravoice.bean;

public class UserBean {
    public String uId;
    public String uName;
    public String uAvatar;

    public UserBean() {
    }

    public UserBean(String uId, String uName, String uAvatar) {
        this.uId = uId;
        this.uName = uName;
        this.uAvatar = uAvatar;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uId='" + uId + '\'' +
                ", uName='" + uName + '\'' +
                ", uAvatar='" + uAvatar + '\'' +
                '}';
    }
}
