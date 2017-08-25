package ua.taras.appc.bluetoothCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import helperClasses.Constants;
import helperClasses.MsgManager;

public class ConnectedThread extends Thread{

	private final ClientRequestHandler client_request_handler;
	
	private final BluetoothSocket socket;
    private final InputStream in_stream;
    private final OutputStream out_stream;
    private byte[] buffer; // mmBuffer store for the stream
    
	public ConnectedThread(BluetoothSocket socket, ClientRequestHandler client_request_handler){
		this.socket = socket;
		this.client_request_handler = client_request_handler;
		
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        
        try {
			tmpIn = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        in_stream = tmpIn;
        out_stream = tmpOut;
		
	}
	
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

				String[] msg_parts = MsgManager.unpack_msg_from_client(sb.toString());
				int msg_what = Integer.parseInt(msg_parts[0]);
				String msg_subject = msg_parts[1];

				Message readMsg = handler.obtainMessage(msg_what, msg_subject);
				readMsg.sendToTarget();

			} catch (IOException e) {

				e.printStackTrace();
				break;
			}
		}
	}
	
	public void write_to_client(byte[] buff){
		try {
			Log.i("C_JAVA_CON_TH", new String(buff));
			out_stream.write(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_ADD_CARD:

				client_request_handler.check_admin_password((String) msg.obj);
				break;
			}
		}
	};
	
}
