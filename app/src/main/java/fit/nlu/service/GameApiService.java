package fit.nlu.service;

import java.util.List;

import fit.nlu.dto.RoomResponse;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GameApiService {
    @POST("api/game/create-room")
    Call<Room> createRoom(@Body Player player);

    @POST("api/game/join-room")
    Call<Room> joinRoom(@Query("roomId") String roomId, @Body Player player);

    @GET("api/game/rooms")
    Call<List<RoomResponse>> getRooms();
}
