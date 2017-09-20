package ua.taras.appc;

import ua.taras.appc.R;
import ua.taras.appc.bluetoothCommunication.BluetoothServer;
import ua.taras.appc.bluetoothCommunication.interfaces.IController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import helperClasses.CardManager;
import helperClasses.Constants;
import helperClasses.MsgManager;
import helperClasses.RfidListenerManager;
import listeners.DoorRunnable;

public class MainActivity extends Activity implements IController{

	//private Button b_configure;
	//private Button b_start_reading_rfid_card;
	
	private Process process;
	
	private BluetoothServer bluetooth_server;
	private CardManager card_manager;
	private RfidListenerManager rfid_listener_manager;
	
	//file descriptors
	int fd26;
	int fd33;
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				/*case R.id.b_read_rfid_card:
					//gpioLineVal(fd26, fd33);
				break;*/
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		bluetooth_server = new BluetoothServer(this);
		card_manager = new CardManager(this);
		
		try {
			
			//export gpio pins
			export_gpio225_and_gpio234();
			
			//enable bluetooth discoverable mode
			//bluetooth_server.enable_bluetooth_discoverable_mode();
			bluetooth_server.start_accepting_thread();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//b_start_reading_rfid_card = (Button) findViewById(R.id.b_read_rfid_card);
		//b_configure				  = (Button) findViewById(R.id.b_configure_uart);
		
		
        //b_start_reading_rfid_card.setOnClickListener(onClickListener);    
	}

	private void export_gpio225_and_gpio234() throws IOException{
		process = Runtime.getRuntime().exec("/system/bin/su");
			
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes("echo " + 225 + " > /sys/class/gpio/export\n");
		os.writeBytes("echo " + "in" + " > /sys/class/gpio/gpio225/direction\n");
		os.writeBytes("echo " + "1" + " > /sys/class/gpio/gpio225/active_low\n");
		os.writeBytes("echo " + "falling" + " > /sys/class/gpio/gpio225/edge\n");
		
		os.writeBytes("echo " + 234 + " > /sys/class/gpio/export\n");
		os.writeBytes("echo " + "in" + " > /sys/class/gpio/gpio234/direction\n");
		os.writeBytes("echo " + "1" + " > /sys/class/gpio/gpio234/active_low\n");
		os.writeBytes("echo " + "falling" + " > /sys/class/gpio/gpio234/edge\n");
		
		os.flush();
		
		fd26 = configurePin26();
		Log.i("C_JAVA", "fd - " + fd26);
		fd33 = configurePin33();
		Log.i("C_JAVA", "fd - " + fd33);
		
		if (fd26 == -1 || fd33 == -1){
			Log.i("C_JAVA", "EXPORT AGAIN");
			try {
				export_gpio225_and_gpio234();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else{
			// lets create the manager to handle infinite door listener
	 		rfid_listener_manager = new RfidListenerManager(this);
	 		// smth like start...
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();	
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	public void start_infinite_door_rfid_reader(){
		Log.v("C_EXAM_JAVA", "DOOR_START LISTENING");
		String[] card_id_array = gpioLineVal(fd26, fd33);
		
		if (card_id_array != null && card_id_array.length > 0) {
			String card_id = card_manager.array_to_string(card_id_array);
			
			if (card_manager.is_card_already_added(card_id)){
				// access granted
				bluetooth_server.on_open_door_access_granted();
				Log.v("C_JAVA", "OPEN DOOR ACCESS GRANTED");
			} else{
				// access denied
				bluetooth_server.on_open_door_access_denied();
				Log.v("C_JAVA", "OPEN DOOR ACCESS DENIED");
			}

		} else {
			Log.v("C_JAVA", "DOOR LISTENER HAS BEEN INTERRUPTED");
			//rfid_listener_manager.start_add_card_runnable();
		}	
	}
	
	
	// gets called from bluetooth server
	@Override
	public void notify_rfid_man_to_start_listenning_to_new_incom_card() {
		rfid_listener_manager.suspend_door_runnable();
	}
	
	@Override
	public void start_listening_to_new_incoming_card() {
		Log.v("C_EXAM_JAVA", "ADD_CARD_START LISTENING");
		
		String[] card_id_array = gpioLineVal(fd26, fd33);
		Log.v("C_JAVA", "CARD ID " + Arrays.asList(card_id_array).toString() );
	
		if (card_id_array != null && card_id_array.length > 0) {
			String card_id = card_manager.array_to_string(card_id_array);
			
			if (!card_manager.is_card_already_added(card_id)){
				Log.v("C_JAVA", card_id+" NOT ADDED");
				//save
				card_manager.save_new_card(card_id);
				// notify server to send client response
				bluetooth_server.on_card_saved_to_shared_pref();
				Log.v("C_JAVA", "CARD ADDING_SAVED");
				rfid_listener_manager.suspend_add_card_runnable();
			} else{
				bluetooth_server.on_add_card_failure(
						Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE);
				Log.v("C_JAVA", "CARD ADDING_FAIL_NOT_UNIQUE");
				rfid_listener_manager.suspend_add_card_runnable();
			}
		}
	}
	
	//** JNI **//
	private native static int configurePin26();
	private native static int configurePin33();
	public native String[] gpioLineVal(int fd26, int fd33);
	static{
		System.loadLibrary("code_c");
	}
	
	
	

	
}
