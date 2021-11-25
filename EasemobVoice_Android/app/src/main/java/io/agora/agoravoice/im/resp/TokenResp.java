package io.agora.agoravoice.im.resp;

public class TokenResp {
    /**
     * access_token : YWMtm43V3rclEemM9A2zvEhp8wAAAAAAAAAAAAAAAAAAAAEivP-gj4YR5p34UW9t9oxtAgMAAAFsX4b25gBPGgDZ2fcu4FzN77CNpS0IXeH4LJXTLWFQGSZ903x2soSPTA
     * expires_in : 5184000
     * application : 22bcffa0-8f86-11e6-9df8-516f6df68c6d
     */

    private String access_token;
    private int expires_in;
    private String application;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }
}
