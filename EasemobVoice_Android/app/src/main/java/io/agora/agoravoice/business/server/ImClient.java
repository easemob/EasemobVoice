package io.agora.agoravoice.business.server;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.retrofit.interfaces.GeneralService;
import io.agora.agoravoice.business.server.retrofit.interfaces.LogService;
import io.agora.agoravoice.business.server.retrofit.interfaces.RoomService;
import io.agora.agoravoice.business.server.retrofit.interfaces.SeatService;
import io.agora.agoravoice.business.server.retrofit.interfaces.UserService;
import io.agora.agoravoice.im.service.IMService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImClient {
    private static final String SERVER_HOST_PRODUCT = "https://a4-v2.easemob.com/";
    private static final String OSS_HOST_PRODUCT = "https://api-solutions.agoralab.co/";
    private static final String ERROR_UNKNOWN_MSG = "";
    private static final String API_ROOT_PATH = "ent/voice";

    private static final int MAX_RESPONSE_THREAD = 10;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 30;

    private static final int ERROR_OK = 0;
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_CONNECTION = -2;


    private final IMService mImService;



    public ImClient() {
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
        mImService = retrofit.create(IMService.class);

        builder = new Retrofit.Builder()
                .baseUrl(OSS_HOST_PRODUCT)
                .client(okHttpClient)
                .callbackExecutor(Executors.newFixedThreadPool(MAX_RESPONSE_THREAD))
                .addConverterFactory(GsonConverterFactory.create());
        builder.client(client);
        retrofit = builder.build();

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
}
