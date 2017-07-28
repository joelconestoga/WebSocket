package ca.joel.wsocketclient;

import android.os.Handler;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


public class ServerConnection {

    public enum ConnectionStatus {
        DISCONNECTED,
        CONNECTED
    }

    public interface ServerListener {
        void onNewMessage(String message);
        void onStatusChange(ConnectionStatus status);
    }

    private String url;
    private OkHttpClient client;
    private WebSocket wSocket;

    private Handler messageHandler;
    private Handler statusHandler;
    private Handler failureHandler;

    private ServerListener listener;

    public ServerConnection(String url) {
        this.url = url;

        client = new OkHttpClient.Builder()
                .readTimeout(3,  TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        messageHandler = new Handler(msg -> {
            this.listener.onNewMessage((String) msg.obj);
            return true;
        }
        );

        statusHandler = new Handler(msg -> {
            this.listener.onStatusChange((ConnectionStatus) msg.obj);
            return true;
        }
        );

        failureHandler = new Handler(msg -> {
            disconnect();
            return true;
        }
        );
    }

    public void connect(ServerListener listener) {

        this.listener = listener;

        Request request = new Request.Builder().url(url).build();

        SocketListener socketListener = new SocketListener(messageHandler,
                statusHandler, failureHandler);

        wSocket = client.newWebSocket(request, socketListener);
    }

    public void sendMessage(String message) {
        wSocket.send(message);
    }

    public void disconnect() {
        wSocket.cancel();
        listener = null;
        messageHandler.removeCallbacksAndMessages(null);
        statusHandler.removeCallbacksAndMessages(null);
    }
}
