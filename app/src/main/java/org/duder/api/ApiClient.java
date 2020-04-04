package org.duder.api;

import org.duder.model.ChatMessage;
import org.duder.model.User;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private Retrofit retrofit;
    private ApiService apiService;
    private static ApiClient apiClient;

    private ApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(Const.REST_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        this.apiService = retrofit.create(ApiService.class);
    }

    public static ApiClient getApiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public Single<List<ChatMessage>> getChatState() {
        return apiService.getChatState();
    }

    public Single<Response<ResponseBody>> registerUser(User user) {
        return apiService.registerUser(user);
    }
}
