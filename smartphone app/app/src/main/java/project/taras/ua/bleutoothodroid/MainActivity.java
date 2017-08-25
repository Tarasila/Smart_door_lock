package project.taras.ua.bleutoothodroid;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import project.taras.ua.bleutoothodroid.bluetoothCommunication.BluetoothServer;
import project.taras.ua.bleutoothodroid.interfaces.IController;
import project.taras.ua.bleutoothodroid.helperClasses.Constants;
import project.taras.ua.bleutoothodroid.helperClasses.MsgManager;
import project.taras.ua.bleutoothodroid.mainViewMVC.ViewMVC;

public class MainActivity extends AppCompatActivity implements IController, PasswordFragment.IPasswordFragment{

    BluetoothServer bluetoothServer;
    ViewMVC viewMVC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewMVC = new ViewMVC(this);
        viewMVC.init_view_elements();
        viewMVC.set_on_click_listener(onClickListener);

        bluetoothServer = new BluetoothServer(this);
    }

    /** //// CONNECT THREAD CALLBACK \\\\ */

    @Override
    public void on_odroid_connected() {
        Message msg = handler.obtainMessage(Constants.ADMIN_ACCESS_GRANTED);
        msg.sendToTarget();
    }

    @Override
    public void on_odroid_socket_connection_failure() {
        Message msg = handler.obtainMessage(Constants.ADMIN_ACCESS_FAILURE);
        msg.sendToTarget();
    }

    /** //// PASSWORD FRAGMENT CALLBACK \\\\ */

    @Override
    public void on_send_password_clicked(String password) {
        byte[] msg_buff = MsgManager.make_msg(Constants.MSG_ADD_CARD, password);
        Log.v("MSG_PASSWORD ", new String(msg_buff));
        bluetoothServer.connected_thread.write_to_odroid(msg_buff);
    }

    /** //// CONNECTED THREAD CALLBACK \\\\ */

    @Override
    public void on_odroid_start_listening_to_new_incoming_card() {
        viewMVC.set_put_card_layout_visibility(View.VISIBLE);
    }

    @Override
    public void on_odroid_card_successfully_added() {
        viewMVC.set_put_card_layout_visibility(View.INVISIBLE);
    }

    public void on_odroid_card_has_not_been_added(int status){
        switch (status){
            case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
                viewMVC.set_put_card_layout_visibility(View.INVISIBLE);

                Message msg = handler.obtainMessage(Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE);
                msg.sendToTarget();
                break;
        }
    }

    @Override
    public void on_odroid_open_door_success() {
        Message msg = handler.obtainMessage(Constants.MSG_STATUS_OPEN_DOOR_SUCCESS);
        msg.sendToTarget();
    }

    @Override
    public void on_odroid_open_door_failure() {
        Message msg = handler.obtainMessage(Constants.MSG_STATUS_OPEN_DOOR_FAILURE);
        msg.sendToTarget();
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.b_sign_in_as_administrator:
                    bluetoothServer.sign_in_as_admin();
                    break;

                case R.id.b_add_new_card:
                    viewMVC.show_password_dialog();
                    // TO EARLY FOR SERVER TO BE CALLED
                    //bluetoothServer.add_new_card();
                    break;
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.ADMIN_ACCESS_GRANTED:
                    viewMVC.undisguise_add_card_button();
                    break;

                case Constants.ADMIN_ACCESS_FAILURE:
                    viewMVC.show_toast_with_info(msg.what);
                    break;

                case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
                    viewMVC.show_toast_with_info(msg.what);
                    break;

                case Constants.MSG_STATUS_OPEN_DOOR_SUCCESS:
                    viewMVC.show_toast_with_info(msg.what);
                    break;

                case Constants.MSG_STATUS_OPEN_DOOR_FAILURE:
                    viewMVC.show_toast_with_info(msg.what);
                    break;
            }
        }
    };




}
