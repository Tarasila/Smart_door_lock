package project.taras.ua.bleutoothodroid.mainViewMVC;

import android.app.Activity;
import android.graphics.Color;
import android.transition.Visibility;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import project.taras.ua.bleutoothodroid.MainActivity;
import project.taras.ua.bleutoothodroid.PasswordFragment;
import project.taras.ua.bleutoothodroid.R;
import project.taras.ua.bleutoothodroid.helperClasses.Constants;

/**
 * Created by Taras on 21.08.2017.
 */

public class ViewMVC {

    private Activity context;

    Button b_start;
    Button b_add_new_card;
    Button b_sign_in_as_admin;

    FrameLayout fl_put_card_in_front_of_reader;

    public ViewMVC(Activity context){
        this.context = context;
    }

    public void init_view_elements(){
        b_add_new_card = (Button) context.findViewById(R.id.b_add_new_card);
        b_sign_in_as_admin = (Button) context.findViewById(R.id.b_sign_in_as_administrator);
        fl_put_card_in_front_of_reader = (FrameLayout) context.findViewById(R.id.inc_put_card_in_front_of_reader);
    }

    public void set_on_click_listener(View.OnClickListener onClickListener) {
        b_add_new_card.setOnClickListener(onClickListener);
        b_sign_in_as_admin.setOnClickListener(onClickListener);
    }

    public void undisguise_add_card_button() {
        b_sign_in_as_admin.setBackgroundColor(Color.GREEN);
        b_sign_in_as_admin.setText("Administrator");
        b_add_new_card.setVisibility(View.VISIBLE);
        Toast.makeText(context, "You've successfully connected to Odroid C2", Toast.LENGTH_LONG).show();
    }

    public void set_put_card_layout_visibility(int visibility) {
        switch (visibility){
            case View.VISIBLE:
                b_add_new_card.setVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                b_add_new_card.setVisibility(View.VISIBLE);
                break;
        }
        fl_put_card_in_front_of_reader.setVisibility(visibility);
    }

    public void show_toast_with_info(int what) {
        switch (what){
            case Constants.ADMIN_ACCESS_FAILURE:
                Toast.makeText(context, "Error. Couldn't connect to Odroid C2", Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_STATUS_ADD_CARD_WRITTEN_FAILURE_NOT_UNIQUE:
                Toast.makeText(context, "Error. Card is already in database. Please try another one", Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_STATUS_OPEN_DOOR_SUCCESS:
                Toast.makeText(context, "Door is open, please come in", Toast.LENGTH_LONG).show();
                break;

            case Constants.MSG_STATUS_OPEN_DOOR_FAILURE:
                Toast.makeText(context, "Access denied! Unknown card.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void show_password_dialog(){
        PasswordFragment password_fragment = new PasswordFragment();
        password_fragment.show(((MainActivity)context).getSupportFragmentManager(), "password confirmation");
    }
}
