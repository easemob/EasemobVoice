package io.agora.agoravoice.business.server;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.retrofit.JsonCallback;
import io.agora.agoravoice.business.server.retrofit.interfaces.GeneralService;
import io.agora.agoravoice.business.server.retrofit.listener.IMServiceListener;
import io.agora.agoravoice.im.PreferenceManager;
import io.agora.agoravoice.im.body.IMCreateChatRoomBody;
import io.agora.agoravoice.im.resp.CreateChatRoomResp;
import io.agora.agoravoice.im.service.IMService;
import io.agora.agoravoice.business.server.retrofit.interfaces.LogService;
import io.agora.agoravoice.business.server.retrofit.interfaces.SeatService;
import io.agora.agoravoice.business.server.retrofit.interfaces.RoomService;
import io.agora.agoravoice.business.server.retrofit.interfaces.UserService;
import io.agora.agoravoice.business.server.retrofit.listener.GeneralServiceListener;
import io.agora.agoravoice.business.log.LogUploaderListener;
import io.agora.agoravoice.business.server.retrofit.listener.RoomServiceListener;
import io.agora.agoravoice.business.server.retrofit.listener.SeatServiceListener;
import io.agora.agoravoice.business.server.retrofit.listener.UserServiceListener;
import io.agora.agoravoice.business.server.retrofit.model.body.ChatMsgBody;
import io.agora.agoravoice.business.server.retrofit.model.body.CreateRoomBody;
import io.agora.agoravoice.business.server.retrofit.model.body.CreateUserBody;
import io.agora.agoravoice.business.server.retrofit.model.body.EditUserBody;
import io.agora.agoravoice.business.server.retrofit.model.body.ModifyRoomBody;
import io.agora.agoravoice.business.server.retrofit.model.body.OssBody;
import io.agora.agoravoice.business.server.retrofit.model.body.SeatBehaviorBody;
import io.agora.agoravoice.business.server.retrofit.model.body.LoginBody;
import io.agora.agoravoice.business.server.retrofit.model.body.SeatStateBody;
import io.agora.agoravoice.business.server.retrofit.model.body.SendGiftBody;
import io.agora.agoravoice.business.server.retrofit.model.requests.Request;
import io.agora.agoravoice.business.server.retrofit.model.responses.BooleanResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.CreateUserResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.GiftListResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.JoinResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.LoginResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.MusicResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.OssResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.Resp;
import io.agora.agoravoice.business.server.retrofit.model.responses.RoomListResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.StringResp;
import io.agora.agoravoice.business.server.retrofit.model.responses.VersionResp;
import io.agora.agoravoice.im.body.IMAdminTokenBody;
import io.agora.agoravoice.im.IMUtils;
import io.agora.agoravoice.im.resp.TokenResp;
import okhttp3.OkHttpClient;
import okhttp3.internal.http2.Header;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;
import retrofit2.internal.EverythingIsNonNull;

public class ServerClient {
    private static final String SERVER_HOST_PRODUCT = "https://api.agora.io/";
    private static final String OSS_HOST_PRODUCT = "https://api-solutions.agoralab.co/";
    private static final String IM_SERVER_HOST = "https://a4-v2.easemob.com/";
    private static final String ERROR_UNKNOWN_MSG = "";
    private static final String API_ROOT_PATH = "ent/voice";

    private static final int MAX_RESPONSE_THREAD = 10;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 30;

    private static final int ERROR_OK = 0;
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_CONNECTION = -2;

    private final GeneralService mGeneralService;
    private final UserService mUserService;
    private final RoomService mRoomService;
    private final SeatService mSeatService;
    private final LogService mLogService;
    private final IMService mImService;

    private String mAppId;

    public ServerClient(String appId) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SERVER_HOST_PRODUCT)
                .client(okHttpClient)
                .callbackExecutor(Executors.newFixedThreadPool(MAX_RESPONSE_THREAD))
                .addConverterFactory(GsonConverterFactory.create());

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(Logging::d);
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        builder.client(client);

        Retrofit retrofit = builder.build();
        mGeneralService = retrofit.create(GeneralService.class);
        mUserService = retrofit.create(UserService.class);
        mRoomService = retrofit.create(RoomService.class);
        mSeatService = retrofit.create(SeatService.class);


        builder = new Retrofit.Builder()
                .baseUrl(OSS_HOST_PRODUCT)
                .client(okHttpClient)
                .callbackExecutor(Executors.newFixedThreadPool(MAX_RESPONSE_THREAD))
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(client);
        retrofit = builder.build();

        mLogService = retrofit.create(LogService.class);



        builder = new Retrofit.Builder()
                .baseUrl(IM_SERVER_HOST)
                .client(okHttpClient)
                //.callbackExecutor(Executors.newFixedThreadPool(MAX_RESPONSE_THREAD))
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(client);
        retrofit = builder.build();


        builder.client(client);
        mImService = retrofit.create(IMService.class);

        mAppId = appId;
    }

    public ServerClient() {
        this(null);
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    /**
     * @return the service bridge path, used to navigate the
     * different services of scenarios of servers. This path
     * is relatively fixed but configurable when needed
     */
    private String getRootPath() {
        return API_ROOT_PATH;
    }

    public String getBaseUrl() {
        return SERVER_HOST_PRODUCT;
    }

    @EverythingIsNonNull
    public void checkVersion(String appCode, int osType, int terminalType,
                             String version, GeneralServiceListener listener) {
        mGeneralService.checkVersion(appCode, osType, terminalType, version).enqueue(new Callback<VersionResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<VersionResp> call, Response<VersionResp> response) {
                VersionResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onGeneralServiceFail(Request.CHECK_VERSION, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onGeneralServiceFail(Request.CHECK_VERSION, resp.code, resp.msg);
                } else {
                    listener.onAppVersionCheckSuccess(resp.data);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<VersionResp> call, Throwable t) {
                listener.onGeneralServiceFail(Request.CHECK_VERSION, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public void musicInfoList(GeneralServiceListener listener) {
        mGeneralService.getMusicList().enqueue(new Callback<MusicResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<MusicResp> call, Response<MusicResp> response) {
                MusicResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onGeneralServiceFail(Request.MUSIC_LIST, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onGeneralServiceFail(Request.MUSIC_LIST, resp.code, resp.msg);
                } else {
                    listener.onGetMusicList(resp.data);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<MusicResp> call, Throwable t) {
                listener.onGeneralServiceFail(Request.MUSIC_LIST, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public void giftList(GeneralServiceListener listener) {
        mGeneralService.getGiftList(getRootPath()).enqueue(new Callback<GiftListResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<GiftListResp> call, Response<GiftListResp> response) {
                GiftListResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onGeneralServiceFail(Request.GIFT_LIST, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onGeneralServiceFail(Request.GIFT_LIST, resp.code, resp.msg);
                } else {
                    listener.onGetGiftList(resp.data);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<GiftListResp> call, Throwable t) {
                listener.onGeneralServiceFail(Request.GIFT_LIST, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void getOssParams(String appId, OssBody body, LogUploaderListener listener) {
        mLogService.getOssParams(appId, body).enqueue(new Callback<OssResp>() {
            @Override
            public void onResponse(Call<OssResp> call, Response<OssResp> response) {
                OssResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onOssParamsFail(Request.UPLOAD_LOGS_OSS, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onOssParamsFail(Request.UPLOAD_LOGS_OSS, resp.code, resp.msg);
                } else {
                    listener.onOssParamsResponse(resp);
                }
            }

            @Override
            public void onFailure(Call<OssResp> call, Throwable t) {
                listener.onOssParamsFail(Request.UPLOAD_LOGS_OSS, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public Call<StringResp> logStsCallback(@Url String url) {
        return mLogService.logStsCallback(url);
    }

    private boolean appIdValid() {
        if (TextUtils.isEmpty(mAppId)) {
            Logging.e("createUser app id is empty");
            return false;
        } else {
            return true;
        }
    }

    @EverythingIsNonNull
    public void createUser(String userName, UserServiceListener listener) {
        if (!appIdValid()) return;

        mUserService.createUser(getRootPath(), mAppId,
                new CreateUserBody(userName)).enqueue(new Callback<CreateUserResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<CreateUserResp> call, Response<CreateUserResp> response) {
                CreateUserResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onUserServiceFailed(Request.CREATE_USER, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onUserServiceFailed(Request.CREATE_USER, resp.code, resp.msg);
                } else {
                    listener.onUserCreateSuccess(resp.data.userId, userName);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<CreateUserResp> call, Throwable t) {
                listener.onUserServiceFailed(Request.CREATE_USER, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void editUserSuccess(String token, final String userId,
                                final String userName, UserServiceListener listener) {
        if (!appIdValid()) return;

        mUserService.editUser(getRootPath(), mAppId, token, userId,
                new EditUserBody(userName)).enqueue(new Callback<BooleanResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                BooleanResp resp = response.body();
                if (resp == null) {
                    listener.onUserServiceFailed(Request.EDIT_USER, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onUserServiceFailed(Request.EDIT_USER, resp.code, resp.msg);
                } else {
                    listener.onUserEditSuccess(userId, userName);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<BooleanResp> call, Throwable t) {
                listener.onUserServiceFailed(Request.EDIT_USER, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public void login(@NonNull String userId, @NonNull UserServiceListener listener) {
        if (!appIdValid()) return;

        mUserService.login(getRootPath(), mAppId, new LoginBody(userId)).enqueue(new Callback<LoginResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<LoginResp> call, Response<LoginResp> response) {
                LoginResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onUserServiceFailed(Request.LOGIN, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onUserServiceFailed(Request.LOGIN, resp.code, resp.msg);
                } else {
                    listener.onLoginSuccess(userId, resp.data.userToken, resp.data.rtmToken);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<LoginResp> call, Throwable t) {
                listener.onUserServiceFailed(Request.LOGIN, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public void join(@NonNull String token, @NonNull String roomId,
                     @NonNull String userId, @NonNull UserServiceListener listener) {
        mUserService.join(token, getRootPath(), mAppId, roomId, userId).enqueue(new Callback<JoinResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<JoinResp> call, Response<JoinResp> response) {
                JoinResp resp = response.body();
                if (resp != null && resp.data != null) {
                    if (resp.code == ERROR_OK) {
                        listener.onJoinSuccess(userId, resp.data.streamId, resp.data.role);
                    } else {
                        listener.onUserServiceFailed(Request.JOIN, resp.code, resp.msg);
                    }
                } else {
                    try {
                        String message = response.errorBody().string();
                        Resp r = new Gson().fromJson(message, Resp.class);
                        listener.onUserServiceFailed(Request.JOIN, r.code, r.msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<JoinResp> call, Throwable t) {
                listener.onUserServiceFailed(Request.JOIN, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void getAdminToken(IMServiceListener listener){
        String path = IMUtils.getTokenUrl();
        EMLog.e("wyz", "getAccess_token +++ path "+path);
        String appKey = EMClient.getInstance().getOptions().getAppKey();
        String[] keyArrys = appKey.split("#");
        mImService.getAdminToken(path,keyArrys[0],keyArrys[1],new IMAdminTokenBody(IMService.CLIENT_ID,IMService.CLIENT_SECRET))
                .enqueue(new Callback<TokenResp>() {
            @Override
            public void onResponse(Call<TokenResp> call, Response<TokenResp> response) {
                EMLog.e("wyz"," response.message()  "+response.message());
                TokenResp resp = response.body();
                if (resp != null && resp.getAccess_token() != null) {
                    XLog.e("wyz", "getAccess_token +++ "+response.body().getAccess_token());
                    listener.onGetAdminTokenSuccess(resp.getAccess_token());
                } else {
                    try {
                        String message = response.errorBody().string();
                        Resp r = new Gson().fromJson(message, Resp.class);
                        listener.onImServiceFailed(Request.GET_ADMIN_TOKEN, r.code, r.msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResp> call, Throwable t) {
                listener.onImServiceFailed(Request.GET_ADMIN_TOKEN, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

//    public void createChatRoom(String subject, String description, String welcomeMessage,
//                               int maxUserCount, List<String> members, IMServiceListener listener){
//        String path = IMUtils.createChatRoomUrl();
//        EMLog.e("wyz", "createChatRoomUrl +++ path "+path);
//        String appKey = EMClient.getInstance().getOptions().getAppKey();
//        String[] keyArrys = appKey.split("#");
//        String currentUser = EMClient.getInstance().getCurrentUser();
//        IMCreateChatRoomBody createChatRoomBody = new IMCreateChatRoomBody(subject,description,maxUserCount,currentUser, members);
//        String adminToken = PreferenceManager.getInstance().getAdminToken();
//        EMLog.e("wyz", "createChatRoomUrl +++ adminToken "+TextUtils.isEmpty(adminToken));
//
//        mImService.createChatRoom(path,keyArrys[0],keyArrys[1],createChatRoomBody,"Bearer " + adminToken)
//                .enqueue(new JsonCallback<CreateChatRoomResp>(CreateChatRoomResp.class){
//
////                    @Override
////                    public void onResponse(Call call, Response response) {
////                        EMLog.e("wyz"," response.message()  "+response.message());
////                        CreateChatRoomResp resp = (CreateChatRoomResp)response.body();
////                        if (resp != null && resp.data.id != null) {
////                            EMLog.e("wyz", "id +++ "+resp.data.id);
////                            listener.onGetAdminTokenSuccess(resp.data.id);
////                        } else {
////                            try {
////                                String message = response.errorBody().string();
////                                Resp r = new Gson().fromJson(message, Resp.class);
////                                listener.onImServiceFailed(Request.GET_ADMIN_TOKEN, r.code, r.msg);
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
//
//                    @Override
//                    public void onError(int status, String errorMsg) {
//                        listener.onImServiceFailed(Request.CREATE_CHATROOM, ERROR_CONNECTION, errorMsg);
//                        EMLog.e("wyz","onError  response.message()  "+errorMsg);
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, CreateChatRoomResp response) {
//
//                    }
//
////                    @Override
////                    public void onFailure(Call call, Throwable t) {
////                        EMLog.e("wyz"," response.message()  "+t.getMessage());
////                        listener.onImServiceFailed(Request.CREATE_CHATROOM, ERROR_CONNECTION, t.getMessage());
////                    }
//                });
//    }




    @EverythingIsNonNull
    public void createRoom(String token, String roomName, int duration, int maxNum,
                           String image, @NonNull RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.createRoom(getRootPath(), mAppId, token,
                new CreateRoomBody(roomName, image, duration, maxNum)).enqueue(new Callback<StringResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<StringResp> call, Response<StringResp> response) {
                StringResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onRoomServiceFailed(Request.CREATE_ROOM, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onRoomServiceFailed(Request.CREATE_ROOM, resp.code, resp.msg);
                } else {
                    listener.onRoomCreated(resp.data, roomName);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<StringResp> call, Throwable t) {
                listener.onRoomServiceFailed(Request.CREATE_ROOM, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void closeRoom(String token, String roomId, RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.closeRoom(getRootPath(), mAppId, token, roomId).enqueue(new Callback<BooleanResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                BooleanResp resp = response.body();
                if (resp == null) {
                    listener.onRoomServiceFailed(Request.LEAVE_ROOM, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onRoomServiceFailed(Request.LEAVE_ROOM, resp.code, resp.msg);
                } else {
                    listener.onLeaveRoom(roomId);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<BooleanResp> call, Throwable t) {
                listener.onRoomServiceFailed(Request.LEAVE_ROOM, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void leaveRoom(String token, String roomId, String userId, RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.leaveRoom(getRootPath(), token, mAppId, roomId, userId).enqueue(new Callback<BooleanResp>() {
            @Override
            public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                BooleanResp resp = response.body();
                if (resp == null) {
                    listener.onRoomServiceFailed(Request.LEAVE_ROOM, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onRoomServiceFailed(Request.LEAVE_ROOM, resp.code, resp.msg);
                } else {
                    listener.onLeaveRoom(roomId);
                }
            }

            @Override
            public void onFailure(Call<BooleanResp> call, Throwable t) {
                listener.onRoomServiceFailed(Request.LEAVE_ROOM, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void getRoomList(String token, final String nextId,
                            int count, int type, RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.getRoomList(getRootPath(), mAppId, token, nextId,
                count, type).enqueue(new Callback<RoomListResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<RoomListResp> call, Response<RoomListResp> response) {
                RoomListResp resp = response.body();
                if (resp == null || resp.data == null) {
                    listener.onRoomServiceFailed(Request.ROOM_LIST, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onRoomServiceFailed(Request.ROOM_LIST, resp.code, resp.msg);
                } else {
                    listener.onGetRoomList(nextId, resp.data.total, resp.data.list);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<RoomListResp> call, Throwable t) {
                listener.onRoomServiceFailed(Request.ROOM_LIST, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void sendGift(String token, String roomId,
                         String giftId, int count, RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.sendGift(getRootPath(), mAppId, token, roomId, new SendGiftBody(giftId, count))
            .enqueue(new Callback<BooleanResp>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                    BooleanResp resp = response.body();
                    if (resp == null) {
                        listener.onRoomServiceFailed(Request.SEND_GIFT, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                    } else if (!resp.data) {
                        listener.onRoomServiceFailed(Request.SEND_GIFT, resp.code, resp.msg);
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<BooleanResp> call, Throwable t) {
                    listener.onRoomServiceFailed(Request.SEND_GIFT, ERROR_CONNECTION, t.getMessage());
                }
            });
    }

    @EverythingIsNonNull
    public void modifyRoom(String token, String roomId,
                           String backgroundId, RoomServiceListener listener) {
        if (!appIdValid()) return;

        mRoomService.modifyRoom(getRootPath(), mAppId, token, roomId, new ModifyRoomBody(backgroundId))
                .enqueue(new Callback<BooleanResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                BooleanResp resp = response.body();
                if (resp == null) {
                    listener.onRoomServiceFailed(Request.MODIFY_ROOM, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (!resp.data) {
                    listener.onRoomServiceFailed(Request.MODIFY_ROOM, resp.code, resp.msg);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<BooleanResp> call, Throwable t) {
                listener.onRoomServiceFailed(Request.MODIFY_ROOM, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    public void sendChatMessage(@NonNull String token, @NonNull String appId,
                                @NonNull String roomId, @NonNull String message,
                                @NonNull RoomServiceListener listener) {
        mRoomService.sendChatMessage(token, appId, roomId, new ChatMsgBody(message))
            .enqueue(new Callback<Resp>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<Resp> call, Response<Resp> response) {
                    Resp resp = response.body();
                    if (resp == null) {
                        listener.onRoomServiceFailed(Request.SEND_CHAT, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                    } else if (resp.code != ERROR_OK) {
                        listener.onRoomServiceFailed(Request.SEND_CHAT, resp.code, resp.msg);
                    }
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<Resp> call, Throwable t) {
                    listener.onRoomServiceFailed(Request.SEND_CHAT, ERROR_CONNECTION, t.getMessage());
                }
            }
        );
    }

    @EverythingIsNonNull
    public void requestSeatBehavior(String token, String roomId, String userId, String userName,
                                    int no, int type, SeatServiceListener listener) {
        if (!appIdValid()) return;

        mSeatService.requestSeatBehavior(getRootPath(), mAppId, token, roomId, userId,
                new SeatBehaviorBody(no, type)).enqueue(new Callback<StringResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<StringResp> call, Response<StringResp> response) {
                StringResp resp = response.body();
                if (resp == null) {
                    listener.onSeatBehaviorFail(type, userId, userName, no, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (resp.code != ERROR_OK) {
                    listener.onSeatBehaviorFail(type, userId, userName, no, resp.code, resp.msg);
                } else {
                    listener.onSeatBehaviorSuccess(roomId, type, userId, userName, no, resp.code, resp.msg);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<StringResp> call, Throwable t) {
                listener.onSeatBehaviorFail(type, userId, userName, no, ERROR_CONNECTION, t.getMessage());
            }
        });
    }

    @EverythingIsNonNull
    public void requestSeatStateChange(String token, String roomId,
                                       int no, int state, SeatServiceListener listener) {
        if (!appIdValid()) return;

        mSeatService.modifySeatState(getRootPath(), mAppId, token, roomId,
                new SeatStateBody(no, state)).enqueue(new Callback<BooleanResp>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<BooleanResp> call, Response<BooleanResp> response) {
                BooleanResp resp = response.body();
                if (resp == null) {
                    listener.onSeatStateChangeFail(no, state, ERROR_UNKNOWN, ERROR_UNKNOWN_MSG);
                } else if (!resp.data) {
                    listener.onSeatStateChangeFail(no, state, resp.code, resp.msg);
                } else {
                    listener.onSeatStateChanged(no, state);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<BooleanResp> call, Throwable t) {
                listener.onSeatStateChangeFail(no, state, ERROR_CONNECTION, t.getMessage());
            }
        });
    }
}