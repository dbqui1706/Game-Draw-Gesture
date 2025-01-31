package fit.nlu.service;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import fit.nlu.enums.RoomState;
import fit.nlu.model.Player;
import fit.nlu.model.Room;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.http2.Http2Reader;

public class GameWebSocketService {
    private static final String WS_URL = "ws://10.0.2.2:8080/game/";
    private WebSocket webSocket;
    private String roomId;
    private WebSocketEventListener eventListener;

    public interface WebSocketEventListener {
        void onStateChanged(RoomState state);

        void onPlayerJoined(Player player);

        void onPlayerLeft(Player player);

        void onGameUpdate(Room roomData);

        void onError(String error);
    }

    public GameWebSocketService(String roomId, WebSocketEventListener webSocketEventListener) {
        this.roomId = roomId;
        this.eventListener = webSocketEventListener;
    }

    private void connectWebSocket() {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();

        // Khoởi tạo WebSocket connection
        Request request = new Request.Builder().url(WS_URL + roomId).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                handleMessages(text);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                eventListener.onError("WebSocket connection failed: " + t.getMessage());
                // Thử kết nối lại sau 5 giây
                new Handler().postDelayed(() -> connectWebSocket(), 5000);
            }
        });
    }

    private void handleMessages(String message) {
        // Xử lý message từ server
        try {
            Gson gson = new Gson();
            WebSocketMessage<?> wsMessage = gson.fromJson(message, WebSocketMessage.class);

            switch (wsMessage.getType()) {
                case WebSocketMessage.STATE_CHANGED:
                    RoomState newState = gson.fromJson(
                            wsMessage.getData().toString(),
                            RoomState.class
                    );
                    eventListener.onStateChanged(newState);
                    break;
                case WebSocketMessage.PLAYER_JOINED:
                    Player newPlayer = gson.fromJson(
                            wsMessage.getData().toString(),
                            Player.class
                    );
                    eventListener.onPlayerJoined(newPlayer);
                    break;

                default:
                    eventListener.onError("Unknown message type: " + wsMessage.getType());
            }
        } catch (JsonSyntaxException e) {
            eventListener.onError("Invalid message format");
        }
    }

    // Gửi message tới server
    public void sendMessage(String type, Object data) {
        WebSocketMessage<?> message = new WebSocketMessage<>(type, data);
        String json = new Gson().toJson(message);
        webSocket.send(json);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }
}
