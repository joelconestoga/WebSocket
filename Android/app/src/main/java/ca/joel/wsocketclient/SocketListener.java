package ca.joel.wsocketclient;

import android.os.Handler;
import android.os.Message;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketListener extends WebSocketListener {

    private Handler messageHandler;
    private Handler statusHandler;
    private Handler failureHandler;

    public SocketListener(Handler messageHandler, Handler statusHandler,
                          Handler failureHandler) {
        this.messageHandler = messageHandler;
        this.statusHandler = statusHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Message m = statusHandler.obtainMessage(0, ServerConnection.ConnectionStatus.CONNECTED);
        statusHandler.sendMessage(m);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Message m = messageHandler.obtainMessage(0, text);
        messageHandler.sendMessage(m);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Message m = statusHandler.obtainMessage(0, ServerConnection.ConnectionStatus.DISCONNECTED);
        statusHandler.sendMessage(m);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Message m = failureHandler.obtainMessage(0, ServerConnection.ConnectionStatus.DISCONNECTED);
        failureHandler.sendMessage(m);
    }
}
