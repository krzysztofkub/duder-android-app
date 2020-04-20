package org.duder.service;

import org.duder.model.chat.ChatMessage;
import org.duder.model.event.Event;
import org.duder.model.user.Account;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Maybe;
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

    synchronized public static ApiClient getApiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient;
    }

    public Single<List<ChatMessage>> getChatState(String sessionToken) {
        return apiService.getChatState(sessionToken);
    }

    public Single<Response<ResponseBody>> registerUser(Account account) {
        return apiService.registerUser(account);
    }

    public Single<Response<ResponseBody>> loginUser(String login, String password) {
        return apiService.loginUser(login, password);
    }

    public Maybe<List<Event>> getEvents(int page, int size, String sessionToken) {
        return apiService.findEventsPage(page, size, sessionToken);
    }

    public Single<Response<ResponseBody>> createEvent(Event event, String sessionToken) {
        return apiService.createEvent(event, sessionToken);
    }

    public Single<List<String>> getHobbies(String sessionToken) {
        return apiService.findHobbies(sessionToken);
    }
}