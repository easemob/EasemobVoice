package io.agora.agoravoice.business.server.retrofit;


import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.internal.http2.Header;
import okhttp3.Callback;


public abstract class JsonCallback<T> implements Callback {
    private Class<T> clazz;

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
        onError(- 1, e.toString());
    }

    public void onResponse(Call call, Response response) throws IOException {
        int code = response.code();
        String body = response.body().string();

        if (code != 200) {
            onError(response.code(), body);
        } else {
            Headers headers = response.headers();
            Header[] hs = new Header[headers.size()];

            for (int i = 0; i < headers.size(); i++) {
                hs[i] = new Header(headers.name(i), headers.value(i));
            }
            T obj = null;
            try {
                obj = parseClass(body, clazz);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            onSuccess(code, hs, obj);
        }
    }

    public abstract void onError(int status, String errorMsg);

    public abstract void onSuccess(int statusCode, Header[] headers, T response);


    private T parseClass(String response, Class<T> rawType) throws Exception {
        T t = new Gson().fromJson(response, rawType);
        return t;
    }
}
