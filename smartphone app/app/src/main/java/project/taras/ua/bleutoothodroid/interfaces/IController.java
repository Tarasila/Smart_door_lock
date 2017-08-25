package project.taras.ua.bleutoothodroid.interfaces;

/**
 * Created by Taras on 21.08.2017.
 */

public interface IController {

    void on_odroid_connected();
    void on_odroid_socket_connection_failure();

    void on_odroid_start_listening_to_new_incoming_card();
    void on_odroid_card_successfully_added();

    void on_odroid_card_has_not_been_added(int status);

    void on_odroid_open_door_success();
    void on_odroid_open_door_failure();
}
