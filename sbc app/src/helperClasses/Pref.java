package helperClasses;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Pref {
	
	private static final String PREF = "pref";
	private static final String CARD_ARRAY = "cardarray";
	
	private static Pref instance;
	private static SharedPreferences preferences;

	private Pref(){
	}
	
	public static Pref get_instance(Context context){
		if (instance == null){
			instance = new Pref();
			preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
		}
		return instance;
	}
	
	public void save_new_card(String card_number){
		
		String jsonArray_string = preferences.getString(CARD_ARRAY, null);
		if (jsonArray_string == null){
			create_new_array_for_cards_to_save_in();
		} 
		
		
		JSONArray array = null;
		
		try {
			array = new JSONArray(jsonArray_string);
			array.put(card_number);
			
			preferences
			.edit()
			.putString(CARD_ARRAY, array.toString())
			.apply();

		} catch (JSONException e) {
			Log.e("JSON", "ADD_CARD_ERROR");
			e.printStackTrace();
		}
		
	}
	
	public String get_all_cards_id(){
		String jsonArray_string = preferences.getString(CARD_ARRAY, null);
		if (jsonArray_string == null){
			create_new_array_for_cards_to_save_in();
		}
		return preferences.getString(CARD_ARRAY, null);
	}
	
	
	
	
	private void create_new_array_for_cards_to_save_in(){
		Log.e("PREF", "CREATE NEW ARRAY FOR CARDS");
		JSONArray array = new JSONArray();			
		
		preferences
			.edit()
			.putString(CARD_ARRAY, array.toString())
			.apply();
	}
	
}
