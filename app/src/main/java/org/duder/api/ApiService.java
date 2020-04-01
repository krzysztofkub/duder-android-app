package org.duder.api;

import org.duder.model.ChatMessage;
import org.duder.util.Const;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET(Const.GET_MESSAGE_HISTORY_ENDPOINT)
    Single<List<ChatMessage>> getChatState();
}
