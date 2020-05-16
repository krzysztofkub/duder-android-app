package org.duder.service;

import org.duder.dto.chat.ChatMessage;
import org.duder.dto.event.EventLoadingMode;
import org.duder.dto.event.EventPreview;
import org.duder.dto.user.Dude;
import org.duder.dto.user.RegisterAccount;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiClient apiClient;
    private Retrofit retrofit;
    private ApiService apiService;

    private ApiClient() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl(Const.REST_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        this.apiService = retrofit.create(ApiService.class);
    }

    synchronized public static ApiClient getApiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public Single<List<ChatMessage>> getChatState(String sessionToken) {
        return apiService.getChatState(sessionToken);
    }

    public Single<Response<ResponseBody>> registerUser(RegisterAccount registerAccount) {
        return apiService.registerUser(registerAccount);
    }

    public Single<Response<ResponseBody>> loginUser(String login, String password) {
        return apiService.loginUser(login, password);
    }

    public Single<Response<ResponseBody>> loginUserWithFb(String accessToken) {
        return apiService.loginUserWithFb(accessToken);
    }

    public Single<Boolean> validate(String sessionToken) {
        return apiService.validateUser(sessionToken);
    }

    public Single<List<Dude>> getDudes(int page, int size, String sessionToken) {
        return apiService.getDudes(page, size, sessionToken);
    }

    public Maybe<List<EventPreview>> getEvents(int page, int size, EventLoadingMode loadingMode, String sessionToken) {
        return apiService.findEventsPage(page, size, loadingMode, sessionToken);
    }

    public Single<Response<ResponseBody>> createEvent(RequestBody event, MultipartBody.Part image, String sessionToken) {
        return apiService.createEvent(event, image, sessionToken);
    }

    public Single<Response<ResponseBody>> getEvent(String url, String sessionToken) {
        return apiService.getEvent(url, sessionToken);
    }

    public Single<List<String>> getHobbies(String sessionToken) {
        return apiService.findHobbies(sessionToken);
    }
}
