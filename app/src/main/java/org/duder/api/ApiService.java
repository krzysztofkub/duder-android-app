package org.duder.api;

import org.duder.model.ChatMessage;
import org.duder.model.User;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET(Const.GET_MESSAGE_HISTORY_ENDPOINT)
    Single<List<ChatMessage>> getChatState();

    @POST(Const.REGISTER_USER)
    Single<Response<ResponseBody>> registerUser(@Body User user);
}
