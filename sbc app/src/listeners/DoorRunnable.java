package listeners;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.util.Log;
import android.util.TimeUtils;
import helperClasses.RfidListenerManager;
import ua.taras.appc.MainActivity;

public class DoorRunnable implements Runnable {

	private RfidListenerManager manager;

	public DoorRunnable(RfidListenerManager manager) {
		this.manager = manager;
	}

	public void run() {

		try {

			while (!Thread.interrupted()) {

				synchronized (manager.door_runnable) {

					while (!manager.add_new_card_action) {

						if (manager.add_new_card_action) {
							Log.v("C_JAVA", "DOOR_RUNNABLE_GO_TO_SLEEP...");
							break;
						}
						
						manager.main_activity.start_infinite_door_rfid_reader();
					}

					while (manager.add_new_card_action) {
						Log.v("C_EXAM_JAVA", "DOOR_RUNNABLE_SLEEPING...");
						manager.on_door_runnable_has_been_interrupted();
						wait();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
