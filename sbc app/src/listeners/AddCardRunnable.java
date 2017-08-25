package listeners;

import android.util.Log;
import helperClasses.RfidListenerManager;

public class AddCardRunnable implements Runnable {

	private RfidListenerManager manager;

	public AddCardRunnable(RfidListenerManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {

		try {

			while (!Thread.interrupted()) {

				synchronized (manager.add_card_runnable) {

					while (manager.add_new_card_action) {

						if (!manager.add_new_card_action) {
							Log.v("C_JAVA", "ADD_CARD_RUNNABLE_GO_TO_SLEEP...");
							break;
						}

						manager.start_listening_to_new_incoming_card();
					}

					while (!manager.add_new_card_action) {
						Log.v("C_EXAM_JAVA", "ADD_CARD_RUNNABLE_SLEEPING...");
						wait();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
