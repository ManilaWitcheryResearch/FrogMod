package manila.frogmod.mcs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by swordfeng on 16-11-18.
 */
public class JsonMessage extends Message {
    @Override
    public String encode() {
        return obj.toString();
    }

    public String uri = null;
    public JsonObject obj;

    public JsonMessage() {
        this.obj = new JsonObject();
    }

    public JsonMessage(JsonObject obj) {
        this.obj = obj;
    }

    public JsonMessage setSuccess() {
        obj.addProperty("result", "success");
        obj.addProperty("errormsg", "");
        return this;
    }

    public JsonMessage setFailure(String reason) {
        obj.addProperty("result", "failed");
        obj.addProperty("errormsg", reason);
        return this;
    }

    public static JsonMessage decode(String buf) throws JsonParseException {
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(buf); /* JsonParseException */
        if (!(elem instanceof JsonObject)) {
            throw new JsonParseException("Not a JSON object");
        }
        JsonObject obj = (JsonObject) elem;
        return new JsonMessage(obj);
    }
}
