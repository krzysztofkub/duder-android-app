package org.duder.service;

import org.duder.model.event.Event;
import org.duder.model.user.Account;
import org.duder.model.chat.ChatMessage;
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

public interface ApiService {
    @GET(Const.GET_MESSAGE_HISTORY_ENDPOINT)
    Single<List<ChatMessage>> getChatState(@Header("Authorization") String sessionToken);

    @POST(Const.REGISTER_USER)
    Single<Response<ResponseBody>> registerUser(@Body Account account);

    @GET(Const.LOGIN_USER)
    Single<Response<ResponseBody>> loginUser(@Query("login") String login, @Query("password") String password);

    @GET(Const.EVENTS)
    Maybe<List<Event>> findEventsPage(@Query("page") int page, @Query("size") int size, @Header("Authorization") String sessionToken);

    @POST(Const.EVENTS)
    Single<Response<ResponseBody>> createEvent(@Body Event event, @Header("Authorization") String sessionToken);

    @GET(Const.HOBBIES)
    Single<List<String>> findHobbies(@Header("Authorization") String sessionToken);
}
