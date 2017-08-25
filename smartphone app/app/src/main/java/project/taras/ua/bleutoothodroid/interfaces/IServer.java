package project.taras.ua.bleutoothodroid.interfaces;

import android.bluetooth.BluetoothSocket;

/**
 * Created by Taras on 21.08.2017.
 */

public interface IServer {

    void on_odroid_socket_connected(BluetoothSocket socket);
    void on_odroid_socket_connection_failure();

    void on_odroid_add_card_password_accepted();
    void on_odroid_add_card_written_success();

    void on_odroid_add_card_failure(int status);

    void on_odroid_open_door_access_granted(int status);
    void on_odroid_open_door_access_denied(int status);
}
