package project.taras.ua.bleutoothodroid.bluetoothCommunication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import project.taras.ua.bleutoothodroid.interfaces.IServer;

import static android.content.ContentValues.TAG;

/**
 * Created by Taras on 20.08.2017.
 */

public class ConnectThread extends Thread {

    private IServer server_listener;

    private final BluetoothSocket socket;
    private final BluetoothDevice device;

    public ConnectThread(IServer server, BluetoothDevice device){
        this.server_listener = server;
        this.device = device;

        BluetoothSocket temp_socket = null;

        try {
            UUID MY_UUID = uuidFromShortCode16("FF10");
            temp_socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = temp_socket;
    }

    @Override
    public void run() {
        //TODO: TRY TO FIGURE OUT HOW TO CLOSE DISCOVERY PROPERLY
        // Cancel discovery because it otherwise slows down the connection.
        //mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                server_listener.on_odroid_socket_connection_failure();
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        //TODO:
        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        server_listener.on_odroid_socket_connected(socket);
    }

    /** helper function to generate UUID */

    public static final String baseBluetoothUuidPostfix = "0000-1000-8000-00805F9B34FB";

    public static UUID uuidFromShortCode16(String shortCode16) {
        return UUID.fromString("0000" + shortCode16 + "-" + baseBluetoothUuidPostfix);
    }
}
