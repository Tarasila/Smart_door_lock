package project.taras.ua.bleutoothodroid.bluetoothCommunication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Set;

import project.taras.ua.bleutoothodroid.helperClasses.Constants;
import project.taras.ua.bleutoothodroid.helperClasses.MsgManager;
import project.taras.ua.bleutoothodroid.interfaces.IController;
import project.taras.ua.bleutoothodroid.interfaces.IServer;

/**
 * Created by Taras on 20.08.2017.
 */

public class BluetoothServer implements IServer{

    private Activity context;
    private final BluetoothAdapter bluetoothAdapter;
    private final IntentFilter filter;

    private ConnectThread connect_thread;
    public ConnectedThread connected_thread;

    public BluetoothServer(Activity context){
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.v("TAG_BLUETOOTH", "name " + deviceName);
                Log.v("TAG_BLUETOOTH", "MAC " + deviceHardwareAddress);
            }
        }
    };

    public void start_discovering() {
        context.registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void sign_in_as_admin() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                //TODO: IF DEVICE NAME MATCHES ODROID C2 START CLIENT CONNECT THREAD

                Log.v("TAG_BLUETOOTH_PAIR", "name " + deviceName);
                Log.v("TAG_BLUETOOTH_PAIR", "MAC " + deviceHardwareAddress);

                if (device.getName().equalsIgnoreCase("Odroid-c2")){

                    connect_thread = new ConnectThread(this, device);
                    connect_thread.start();
                }
            }
        }
    }

    // Don't forget to unregister the ACTION_FOUND receiver.
    public void stop_discovering() {
        context.unregisterReceiver(mReceiver);
    }

    public void add_new_card(){

    }



    /** CALLBACKS FROM CONNECT THREAD **/

    @Override
    public void on_odroid_socket_connected(BluetoothSocket socket) {
        connected_thread = new ConnectedThread(this, socket);
        connected_thread.start();

        ((IController)context).on_odroid_connected();
    }

    @Override
    public void on_odroid_socket_connection_failure() {
        ((IController)context).on_odroid_socket_connection_failure();
    }

    /** CALLBACKS FROM CONNECTED THREAD **/

    @Override
    public void on_odroid_add_card_password_accepted() {
        ((IController)context).on_odroid_start_listening_to_new_incoming_card();
    }

    @Override
    public void on_odroid_add_card_written_success() {
        ((IController)context).on_odroid_card_successfully_added();
    }

    @Override
    public void on_odroid_add_card_failure(int status) {
        switch (status){
            case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
                ((IController)context).on_odroid_card_has_not_been_added(status);
                break;
        }
    }

    @Override
    public void on_odroid_open_door_access_granted(int status) {
        switch (status){
            case Constants.MSG_STATUS_OPEN_DOOR_SUCCESS:
                ((IController)context).on_odroid_open_door_success();
                break;
        }
    }

    @Override
    public void on_odroid_open_door_access_denied(int status) {
        switch (status){
            case Constants.MSG_STATUS_OPEN_DOOR_FAILURE:
                ((IController)context).on_odroid_open_door_failure();
                break;
        }
    }
}
