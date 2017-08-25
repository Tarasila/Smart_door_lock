package ua.taras.appc.bluetoothCommunication;

import android.util.Log;
import helperClasses.Constants;
import ua.taras.appc.bluetoothCommunication.interfaces.IServer;

public class ClientRequestHandler {

	private final IServer server_listener;
	
	public ClientRequestHandler(IServer server){
		this.server_listener = server;
	}

	public void check_admin_password(String password) {
		boolean result = Constants.ADMIN_PASSWORD.equalsIgnoreCase(password);
		
		if (result){
			Log.i("C_JAVA_CHK_PASSWORD", "res " + result);
			server_listener.on_add_card_permission_granted();
		}
	}
	
}
