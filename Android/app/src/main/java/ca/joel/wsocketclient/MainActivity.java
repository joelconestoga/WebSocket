package ca.joel.wsocketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ServerConnection.ServerListener {

    private ServerConnection server;

    private TextView txvLog;
    private ScrollView scvScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server = new ServerConnection("ws://10.0.2.2:8080/webtech8185/mysocket");

        setupButtons();
    }

    private void setupButtons() {
        txvLog = (TextView) findViewById(R.id.txvLog);
        scvScroll = (ScrollView) findViewById(R.id.scvScroll);

        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> server.connect(MainActivity.this));

        Button btnListFiles = (Button) findViewById(R.id.btnListFiles);
        btnListFiles.setOnClickListener(v -> server.sendMessage("listFiles"));

        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(v -> server.disconnect());
    }

    @Override
    public void onNewMessage(String message) {
        runOnUiThread(() -> txvLog.setText(txvLog.getText() + "\n" + message));
        scvScroll.fullScroll(View.FOCUS_DOWN);
    }
}
