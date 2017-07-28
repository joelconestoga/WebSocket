package ca.joel.wsocketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ServerConnection.ServerListener {

    private ServerConnection wSocket;

    private TextView txvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wSocket = new ServerConnection("ws://10.0.2.2:8080/webtech8185/mysocket");

        setupButtons();
    }

    private void setupButtons() {
        txvLog = (TextView) findViewById(R.id.txvLog);

        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> wSocket.connect(MainActivity.this));

        Button btnListFiles = (Button) findViewById(R.id.btnListFiles);
        btnListFiles.setOnClickListener(v -> wSocket.sendMessage("listFiles"));
    }

    @Override
    public void onNewMessage(String message) {
        txvLog.setText(txvLog.getText() + "\n" + message);
    }

    @Override
    public void onStatusChange(ServerConnection.ConnectionStatus status) {

    }
}
