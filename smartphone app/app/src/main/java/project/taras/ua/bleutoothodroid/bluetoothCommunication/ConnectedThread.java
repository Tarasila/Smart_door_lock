package project.taras.ua.bleutoothodroid.bluetoothCommunication;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import project.taras.ua.bleutoothodroid.helperClasses.Constants;
import project.taras.ua.bleutoothodroid.interfaces.IServer;
import project.taras.ua.bleutoothodroid.helperClasses.MsgManager;

/**
 * Created by Taras on 21.08.2017.
 */

public class ConnectedThread extends Thread {

    private final IServer server_listener;
    private final BluetoothSocket socket;
    private final InputStream in_stream;
    private final OutputStream out_stream;

    private static int WRITTEN_MSG_TO_ODROID;

    public ConnectedThread(IServer server, BluetoothSocket socket) {
        this.server_listener = server;
        this.socket = socket;

        OutputStream temp_out_stream = null;
        InputStream temp_in_stream = null;

        try {
            temp_out_stream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            temp_in_stream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.out_stream = temp_out_stream;
        this.in_stream = temp_in_stream;
    }

    @Override
    public void run() {

        final int bufferSize = 1024;
        char[] buffer;
        StringBuilder sb;
        Reader in = null;
        try {
            in = new InputStreamReader(in_stream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        int num_bytes;

        while (true) {
            try {
                buffer = new char[bufferSize];
                sb = new StringBuilder();

                num_bytes = in.read(buffer);
                if (num_bytes > 0) {
                    sb.append(buffer, 0, num_bytes);
                }

                Log.i("C_JAVA_CN_TH", "packed msg " + sb.toString());

                String[] msg_parts = MsgManager.unpack_msg_from_odroid(sb.toString());
                int msg_what = Integer.parseInt(msg_parts[0]);
                int msg_status = Integer.parseInt(msg_parts[1]);

                Message readMsg = handler.obtainMessage(msg_what, msg_status);
                readMsg.sendToTarget();

            } catch (IOException e) {

                e.printStackTrace();
                break;
            }
        }
    }

    public void write_to_odroid(byte[] buff) {
        try {
            out_stream.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int resp_status = (int) msg.obj;

            switch (msg.what) {

                case Constants.MSG_ADD_CARD:

                    switch (resp_status) {
                        case Constants.MSG_STATUS_ADD_CARD_PASSWORD_SUCCESS:
                            Log.i("C_JAVA_CN_TH", "ADD CARD PASSWORD SUCCESS");
                            server_listener.on_odroid_add_card_password_accepted();
                            break;

                        case Constants.MSG_STATUS_ADD_CARD_WRITTEN_SUCCESS:
                            Log.i("C_JAVA_CN_TH", "ADD CARD WRITTEN SUCCESS");
                            server_listener.on_odroid_add_card_written_success();
                            break;

                        case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
                            Log.i("C_JAVA_CN_TH", "ADD CARD WRITTEN FAILURE_NOT_UNIQUE");
                            server_listener.on_odroid_add_card_failure(resp_status);
                            break;
                    }
                    break;

                case Constants.MSG_OPEN_DOOR:

                    switch (resp_status) {

                        case Constants.MSG_STATUS_OPEN_DOOR_SUCCESS:
                            Log.i("C_JAVA_CN_TH", "OPEN DOOR SUCCESS");
                            server_listener.on_odroid_open_door_access_granted(resp_status);
                            break;

                        case Constants.MSG_STATUS_OPEN_DOOR_FAILURE:
                            Log.i("C_JAVA_CN_TH", "OPEN DOOR FAILURE");
                            server_listener.on_odroid_open_door_access_denied(resp_status);
                            break;
                    }

                    break;
            }
        }
    };

}
