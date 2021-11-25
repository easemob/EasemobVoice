package io.agora.agoravoice.im.net;

import io.agora.agoravoice.business.server.retrofit.JsonCallback;
import okhttp3.Headers;


public interface CmosRequest<T> {
    void requestPostAsyncWithHeader(String url, String jsonStringParmas, Object tag, Headers headers, JsonCallback<T> requestCallback);

    void requestPostAsyncWithToken(String url, String jsonStringParmas, Object tag, Headers headers, JsonCallback<T> requestCallback);

    void requestAsyncWithAdminToken(String url, String jsonStringParmas, Object tag, Headers headers, int flag, JsonCallback<T> requestCallback);

    void requestGetAsyncWithHeader(String url, Object tag, Headers headers, JsonCallback<T> requestCallback);
    void cancelRequest(Object tag);
}
