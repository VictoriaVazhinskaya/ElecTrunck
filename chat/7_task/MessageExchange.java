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

    public String getServerResponse(List<Message> messages)  {
        JSONArray array = new JSONArray();
        
        JSONObject jsonObject = new JSONObject(); 
        //jsonObject.put("messages", messages);
        int size = messages.size();
        for(int i=0; i<size; i++){
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

    public String getClientSendMessageRequest(int id, String userName, String message)  {
            
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("user", userName);
        jsonObject.put("message", message);
        return jsonObject.toString();
    }

    public JSONObject getClientMessage(InputStream inputStream) throws ParseException {
        return  getJSONObject(inputStreamToString(inputStream));      
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            if(in != null)
                System.out.println(in);
            else System.out.println("null");
            e.printStackTrace();
        }

        return new String(baos.toByteArray());
    }
}
