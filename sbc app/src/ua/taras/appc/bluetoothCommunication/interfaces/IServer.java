package ua.taras.appc.bluetoothCommunication.interfaces;

import android.bluetooth.BluetoothSocket;

public interface IServer {
	
	void on_socket_given(BluetoothSocket socket);
	void on_add_card_permission_granted();
	void on_card_saved_to_shared_pref();
	void on_add_card_failure(int status);
	void on_open_door_access_granted();
	void on_open_door_access_denied();


}
