package fit.nlu.utils;

public interface Config {
    static final String IP = "172.31.99.54";
    static final String PORT = "8081";
    static final String WS_URL = "ws://" + IP + ":" + PORT + "/ws/websocket";
    static final String BASE_URL = "http://" + IP + ":" + PORT + "/";
}
