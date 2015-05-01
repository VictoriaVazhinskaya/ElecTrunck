import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MessageExchange {

    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        Integer number = index * 8 + 11;

        return "TN" + number + "EN";
    }

    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public String getServerResponse(List<Message> messages, int index) {
        /*List<Task> chunk = tasks.subList(index, tasks.size());
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("tasks", chunk);
        jsonObject.put("token", getToken(tasks.size()));

        return jsonObject.toJSONString();*/
        JSONArray array = new JSONArray();
        
        JSONObject jsonObject = new JSONObject(); 
        //jsonObject.put("messages", messages);
        int size = messages.size();
        for(int i=index; i<size; i++){
        JSONObject localJsonObject = new JSONObject();        
        localJsonObject.put("id", messages.get(i).getId());       
        localJsonObject.put("user", messages.get(i).getUserName());
        localJsonObject.put("message", messages.get(i).getMessage());
        array.add(localJsonObject);
        }
        jsonObject.put("messages", array);
        jsonObject.put("token", getToken(messages.size()));
        return jsonObject.toJSONString();
    }

    /*public Task getClientMessage(InputStream inputStream) throws Exception {
        JSONObject json = getJSONObject(inputStreamToString(inputStream));

        return new Task((String)json.get("id"), (String)json.get("description"), (Boolean)json.get("done") );
    }*/

    public JSONObject getClientMessage(InputStream inputStream) throws IOException, ParseException {
        return  getJSONObject(inputStreamToString(inputStream));      
    }

    public String getErrorMessage(String text) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("error", text);

        return jsonObject.toJSONString();
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;

        while ((length = in.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        System.out.println("Input stream " + new String(baos.toByteArray()));
        return new String(baos.toByteArray());
    }
}
