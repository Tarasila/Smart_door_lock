package ua.taras.appc.bluetoothCommunication;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import ua.taras.appc.bluetoothCommunication.interfaces.IServer;

public class AcceptThread extends Thread{
	
	private IServer server_listener;
	private BluetoothAdapter bluetoothAdapter;
	
	public final BluetoothServerSocket serverSocket;
	
	
	public AcceptThread(IServer server_listener, BluetoothAdapter bluetoothAdapter){
		this.server_listener = server_listener;
		this.bluetoothAdapter = bluetoothAdapter;
		
		// Use a temporary object that is later assigned to serverSocket
        // because serverSocket is final.
		BluetoothServerSocket tmp = null;
		
		try {
			
			UUID MY_UUID = uuidFromShortCode16("FF10");
			tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("odroid", MY_UUID);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.serverSocket = tmp;
	}

	public void run(){
		
		BluetoothSocket socket;
		
		while (true) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				Log.e("C_JAVA_AC_TH", "ERROR" + e.getMessage());
				e.printStackTrace();
				break;
			}
			
			if (socket != null){
				// A connection was accepted. Perform work associated with
                // the connection in a separate thread.
				server_listener.on_socket_given(socket);
				Log.e("C_JAVA_AC_TH", "On socket given");

				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	// helper function to generate UUID
	
	public static final String baseBluetoothUuidPostfix = "0000-1000-8000-00805F9B34FB";
	
	public static UUID uuidFromShortCode16(String shortCode16) {
	    return UUID.fromString("0000" + shortCode16 + "-" + baseBluetoothUuidPostfix);
	}
	
}
