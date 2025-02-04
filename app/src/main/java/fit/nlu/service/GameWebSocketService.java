package fit.nlu.service;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;


/**
 * Service quản lý kết nối WebSocket cho game, xử lý việc kết nối, gửi/nhận message
 * và quản lý các subscriptions tới các topics khác nhau.
 */
public class GameWebSocketService implements Serializable {
    private static final String TAG = "WebSocketManager";
    private static final String WS_URL = "ws://192.168.31.211:8081/ws/websocket";

    // Lưu trữ các disposables để quản lý subscriptions
    private final CompositeDisposable compositeDisposable;
    // Map để theo dõi các subscription theo topic
    private final Map<String, Disposable> topicSubscriptions;
    private final StompClient stompClient;
    private final WebSocketEventListener eventListener;
    private WebSocket webSocket;

    public interface WebSocketEventListener {
        void onConnected();
        void onDisconnected();
        void onError(String error);
        void onMessageReceived(String topic, String message);
    }

    public GameWebSocketService(WebSocketEventListener listener) {
        this.eventListener = listener;
        // Khởi tạo STOMP client với URL của server
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);
        // Cấu hình heartbeat để duy trì kết nối
        stompClient.withClientHeartbeat(30000)
                .withServerHeartbeat(30000);
        this.compositeDisposable = new CompositeDisposable();
        this.topicSubscriptions = new HashMap<>();
        this.connect();
    }

    public void connect() {
        if (stompClient != null && !stompClient.isConnected()) {
            // Theo dõi lifecycle của kết nối
            Disposable lifecycleDisposable = stompClient.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleLifecycleEvent, this::handleConnectionError);

            compositeDisposable.add(lifecycleDisposable);
            stompClient.connect();
        }
    }

    /**
     * Xử lý các sự kiện lifecycle của WebSocket
     */
    private void handleLifecycleEvent(LifecycleEvent lifecycleEvent) {
        switch (lifecycleEvent.getType()) {
            case OPENED:
                Log.d(TAG, "WebSocket Connection Opened");
                eventListener.onConnected();
                break;
            case ERROR:
                Log.e(TAG, "WebSocket Connection Error", lifecycleEvent.getException());
                eventListener.onError(lifecycleEvent.getException().getMessage());
                break;
            case CLOSED:
                Log.d(TAG, "WebSocket Connection Closed");
                eventListener.onDisconnected();
                break;
        }
    }

    private void handleConnectionError(Throwable throwable) {
        Log.e(TAG, "WebSocket Connection Error", throwable);
        eventListener.onError(throwable.getMessage());
    }

    /**
     * Subscribe để nhận messages từ một topic cụ thể.
     * Lưu subscription để có thể unsubscribe sau này.
     */
    public void subscribe(String topic) {
        // Kiểm tra xem đã subscribe topic này chưa
        if (topicSubscriptions.containsKey(topic)) {
            Log.d(TAG, "Already subscribed to topic: " + topic);
            return;
        }

        Disposable disposable = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        topicMessage -> {
                            Log.d(TAG, "Received message from topic: " + topic);
                            eventListener.onMessageReceived(topic, topicMessage.getPayload());
                        },
                        throwable -> {
                            Log.e(TAG, "Error while subscribing to topic: " + topic, throwable);
                            eventListener.onError("Error while subscribing to topic: " + throwable.getMessage());
                        }
                );

        // Lưu subscription để quản lý
        topicSubscriptions.put(topic, disposable);
        compositeDisposable.add(disposable);
    }

    /**
     * Hủy đăng ký nhận message từ một topic cụ thể.
     * Trả về true nếu unsubscribe thành công.
     */
    public boolean unsubscribe(String topic) {
        Disposable subscription = topicSubscriptions.remove(topic);
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
            compositeDisposable.remove(subscription);
            Log.d(TAG, "Unsubscribed from topic: " + topic);
            return true;
        }
        return false;
    }

    /**
     * Gửi message tới một destination cụ thể
     */
    public void sendMessage(String destination, String message) {
        if (stompClient.isConnected()) {
            compositeDisposable.add(stompClient.send(destination, message)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> Log.d(TAG, "Message sent successfully to: " + destination),
                            throwable -> {
                                Log.e(TAG, "Error sending message to: " + destination, throwable);
                                eventListener.onError("Send error: " + throwable.getMessage());
                            }
                    ));
        } else {
            Log.e(TAG, "Cannot send message: WebSocket not connected");
            eventListener.onError("Cannot send message: WebSocket not connected");
        }
    }

    /**
     * Đóng tất cả các connections và subscriptions
     */
    public void disconnect() {
        // Hủy tất cả subscriptions
        for (String topic : new ArrayList<>(topicSubscriptions.keySet())) {
            unsubscribe(topic);
        }
        topicSubscriptions.clear();

        // Dispose tất cả các disposables
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }

        // Ngắt kết nối STOMP client
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }
}
