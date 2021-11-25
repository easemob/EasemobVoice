package io.agora.agoravoice.im.body;

public class IMAdminTokenBody {

    private String grant_type="client_credentials";
    private String client_id;
    private String client_secret;

    public IMAdminTokenBody(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
    }
}
