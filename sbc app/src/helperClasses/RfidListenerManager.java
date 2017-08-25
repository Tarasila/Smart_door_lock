package helperClasses;

import android.util.Log;
import listeners.AddCardRunnable;
import listeners.DoorRunnable;
import ua.taras.appc.MainActivity;

public class RfidListenerManager {
	
	public MainActivity main_activity;
		
	public Thread door_thread;
	public Thread add_card_thread;
    
	public final DoorRunnable door_runnable;
	public final AddCardRunnable add_card_runnable;
	
	public boolean add_new_card_action = false;

	
	public RfidListenerManager(MainActivity context){
		this.main_activity = context;
		this.door_runnable = new DoorRunnable(this);
		this.add_card_runnable = new AddCardRunnable(this);
		
		door_thread = new Thread(door_runnable);
		add_card_thread = new Thread(add_card_runnable);
		
		door_thread.start();
		add_card_thread.start();
	}
	
	public void suspend_door_runnable() {
		Log.v("C_JAVA", "INTERRUPT_DOOR_RUNNABLE...");
		add_new_card_action = true;
		set_interruption_status(1);
	}
	
	// gets called from DoorRunnable in sleeping state
	public void on_door_runnable_has_been_interrupted(){
		start_add_card_runnable();
	}

	public void start_add_card_runnable(){
		synchronized (add_card_runnable){
			Log.v("C_JAVA", "START_ADD_CARD_RUNNABLE...");
			add_card_runnable.notifyAll();
		}
	}
	
	public void suspend_add_card_runnable() {
		synchronized (door_runnable){			
    		Log.v("C_JAVA", "suspend_add_card_runnable...");
			add_new_card_action = false;
			door_runnable.notifyAll();
		}	
	}

	public void start_listening_to_new_incoming_card() {
		main_activity.start_listening_to_new_incoming_card();
	}
	

	//** JNI **//
	public native void set_interruption_status(int s);
	static {
		System.loadLibrary("code_c");
	}
	
}
