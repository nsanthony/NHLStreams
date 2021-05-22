package nhlstreams.data.processing;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import lombok.extern.flogger.Flogger;

@Flogger
public class DataUtils {
	
	public static JsonElement getField(String field, JsonObject object) {
		
		if(object.get(field) != null) {
			return object.get(field);
		}else {
			log.atFine().log("Failed to get field %s", field);
			return error(field);
		}
	}
	
	public static JsonElement error(String field) {
		JsonObject errorObject = new JsonObject();
		errorObject.addProperty(field, "null");
		return errorObject.get(field);
	}

}
