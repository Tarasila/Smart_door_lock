package helperClasses;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.util.Log;

public class CardManager {

	private Activity context;
	private Pref pref;
	
	public CardManager(Activity context){
		this.context = context;
		this.pref = Pref.get_instance(context);
	}
	
	public void save_new_card(String card_id){
		pref.save_new_card(card_id);
	}

	public boolean is_card_already_added(String card_id) {
		// new recast point
		binary_to_hex(card_id);
		
		String string_card_array = pref.get_all_cards_id();

		try {
			JSONArray json_card_array = new JSONArray(string_card_array);

			for (int i = 0; i < json_card_array.length(); i++) {
				if (card_id.equalsIgnoreCase((String) json_card_array.get(i))) {
					Log.v("C_JAVA", "CARD ADDING_CARD MANAGER_NOT UNIQUE");
					return true;
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.v("C_JAVA", "CARD ADDING_CARD MANAGER_UNIQUE");
		return false;
	}
	
	public String array_to_string(String[] array){
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++){
			if (array[i] != null)
				builder.append(array[i]);
		}
		return builder.toString();
	}
	
	public String binary_to_hex(String bits){
		String bits_24 = bits.substring(1, bits.length() - 1);
		int decimal = Integer.parseInt(bits_24,2);
	    String hex_str = Integer.toString(decimal,16);
		Log.v("C_JAVA", "CARD_ID_BINARY_24 " + bits_24);
		Log.v("C_JAVA", "CARD_ID_HEX " + hex_str);

	    return hex_str;
	}
	
}
