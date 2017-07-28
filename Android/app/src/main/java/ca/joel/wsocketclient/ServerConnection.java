package ca.joel.wsocketclient;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class ServerConnection extends WebSocketListener {

    public interface ServerListener {
        void onNewMessage(String message);
    }

    private String url;
    private OkHttpClient client;
    private WebSocket wSocket;

    private ServerListener listener;

    public ServerConnection(String url)  {
        this.url = url;

        client = new OkHttpClient.Builder()
                .readTimeout(3,  TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public void connect(ServerListener listener) {

        this.listener = listener;

        Request request = new Request.Builder().url(url).build();

        wSocket = client.newWebSocket(request, this);
    }

    public void sendMessage(String message) {
        wSocket.send(message);
    }

    public void disconnect() {
        wSocket.cancel();
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        listener.onNewMessage("Connected");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (listener != null)
            listener.onNewMessage(text);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        if (listener != null)
            listener.onNewMessage("Disconnected");
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (listener != null)
            listener.onNewMessage("Disconnected");
    }
}
