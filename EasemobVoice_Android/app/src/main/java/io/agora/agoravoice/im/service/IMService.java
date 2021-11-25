package io.agora.agoravoice.im.service;

import io.agora.agoravoice.business.server.retrofit.model.responses.StringResp;
import io.agora.agoravoice.im.body.IMAdminTokenBody;
import io.agora.agoravoice.im.body.IMCreateChatRoomBody;
import io.agora.agoravoice.im.resp.TokenResp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IMService {
    String CLIENT_ID = "";
    String CLIENT_SECRET = "";


    @POST("{path}/{app}/{key}/token")
    Call<TokenResp> getAdminToken(@Path(value = "path") String path,@Path(value = "app") String app,@Path(value = "key") String key, @Body IMAdminTokenBody body);

    @POST("{path}/{app}/{key}/chatrooms")
    Call<StringResp> createChatRoom(@Path(value = "path") String path,@Path(value = "app") String app,@Path(value = "key") String key, @Body IMCreateChatRoomBody body, @Header("Authorization") String token);
}
