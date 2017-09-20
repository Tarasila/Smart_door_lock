package ua.taras.appc.bluetoothCommunication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import helperClasses.Constants;
import helperClasses.MsgManager;
import ua.taras.appc.bluetoothCommunication.interfaces.IServer;
import ua.taras.appc.bluetoothCommunication.interfaces.IController;

public class BluetoothServer implements IServer{
	
	private Activity context;
	private BluetoothAdapter bluetooth_adapter;
	
	AcceptThread accept_thread;
	public ConnectedThread connected_thread;
	
	private boolean bluetooth_connection_established = false;
	
	private final ClientRequestHandler client_request_handler;
	
	public BluetoothServer(Activity context){
		this.context = context;
		this.bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();
		
		this.client_request_handler = new ClientRequestHandler(this);
	}
	
	public void enable_bluetooth_discoverable_mode(){
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 4000);
		context.startActivity(discoverableIntent);
		Log.i("C_JAVA", "BT_ENABLED");
	}
	
	public void start_accepting_thread(){
		accept_thread = new AcceptThread(this, bluetooth_adapter);
		accept_thread.start();
	}

	
	/** CALLBACKS FROM CONNECT THREAD **/
	
	@Override
	public void on_socket_given(BluetoothSocket socket) {
		// this thread reads client messages and send them back 
		// over Handler object to BluetoothServer class 
		connected_thread = new ConnectedThread(socket, client_request_handler);
		connected_thread.start();
		
		bluetooth_connection_established = true;
	}
	
	/** CALLBACKS FROM CLIENT REQUEST HANDLER **/
	
	@Override
	public void on_add_card_permission_granted() {
		byte[] response = MsgManager.make_response_msg(
				Constants.MSG_ADD_CARD, 
				Constants.MSG_STATUS_ADD_CARD_PASSWORD_SUCCESS
				);
		
		connected_thread.write_to_client(response);
		
		((IController)context).notify_rfid_man_to_start_listenning_to_new_incom_card();
		
	}

	/** CALLBACKS FROM MAIN ACTIVITY **/
	
	@Override
	public void on_card_saved_to_shared_pref() {
		byte[] msg_buff = MsgManager.make_response_msg(
				Constants.MSG_ADD_CARD,
				Constants.MSG_STATUS_ADD_CARD_WRITTEN_SUCCESS
				);
		connected_thread.write_to_client(msg_buff);		
	}

	@Override
	public void on_add_card_failure(int status) {
		switch (status){
			case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
				byte[] msg_buff = MsgManager.make_response_msg(
						Constants.MSG_ADD_CARD,
						Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE
						);
				connected_thread.write_to_client(msg_buff);	
				break;
		}
		
	}

	@Override
	public void on_open_door_access_granted() {
		if (bluetooth_connection_established) {
			byte[] msg_buff = MsgManager.make_response_msg(
					Constants.MSG_OPEN_DOOR,
					Constants.MSG_STATUS_OPEN_DOOR_SUCCESS);
			connected_thread.write_to_client(msg_buff);
		}
		
	}

	@Override
	public void on_open_door_access_denied() {
		if (bluetooth_connection_established) {
			byte[] msg_buff = MsgManager.make_response_msg(
					Constants.MSG_OPEN_DOOR,
					Constants.MSG_STATUS_OPEN_DOOR_FAILURE);
			connected_thread.write_to_client(msg_buff);
		}
		
	}
	
	

}
