package io.agora.agoravoice.bean;

public class RoomBean {
    public String roomId;
    public String imRoomId;
    public String imRoomName;
    public String roomBg;

    public RoomBean() { }

    public RoomBean(String roomId, String imRoomId, String imRoomName) {
        this.roomId = roomId;
        this.imRoomId = imRoomId;
        this.imRoomName = imRoomName;
    }

    @Override
    public String toString() {
        return "RoomBean{" +
                "roomId='" + roomId + '\'' +
                ", imRoomId='" + imRoomId + '\'' +
                ", imRoomName='" + imRoomName + '\'' +
                ", roomBg='" + roomBg + '\'' +
                '}';
    }
}
