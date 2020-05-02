package org.duder.service;

import org.duder.dto.chat.ChatMessage;
import org.duder.dto.event.CreateEvent;
import org.duder.dto.event.EventPreview;
import org.duder.dto.user.RegisterAccount;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET(Const.GET_MESSAGE_HISTORY_ENDPOINT)
    Single<List<ChatMessage>> getChatState(@Header("Authorization") String sessionToken);

    @POST(Const.REGISTER_USER)
    Single<Response<ResponseBody>> registerUser(@Body RegisterAccount registerAccount);

    @GET(Const.LOGIN_USER)
    Single<Response<ResponseBody>> loginUser(@Query("login") String login, @Query("password") String password);

    @GET(Const.LOGIN_USER_WITH_FB)
    Single<Response<ResponseBody>> loginUserWithFb(@Query("accessToken") String accessToken);

    @GET(Const.VALIDATE_USER)
    Single<Boolean> validateUser(@Query("sessionToken") String sessionToken);

    @GET(Const.EVENTS)
    Maybe<List<EventPreview>> findEventsPage(@Query("page") int page, @Query("size") int size, @Header("Authorization") String sessionToken);

    @POST(Const.EVENTS)
    Single<Response<ResponseBody>> createEvent(@Body CreateEvent event, @Header("Authorization") String sessionToken);

    @GET
    Single<Response<ResponseBody>> getEvent(@Url String url, @Header("Authorization") String sessionToken);

    @GET(Const.HOBBIES)
    Single<List<String>> findHobbies(@Header("Authorization") String sessionToken);
}
