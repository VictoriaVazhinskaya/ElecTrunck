import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Message{
public int id;
public String userName;
public String message;

public Message(int id, String userName, String message){
this.id  = id;
this.userName = userName;
this.message = message;
}

public Message(JSONObject jsonobject) {
this.id = Integer.parseInt(String.valueOf(jsonobject.get("id")));
this.userName = String.valueOf(jsonobject.get("user"));
this.message = String.valueOf(jsonobject.get("message"));
}

public String getUserName(){
    return userName;
}

public int getId(){
    return id;
}

public String getMessage(){
    return message;
}

public void setMessage(String message){
    this.message = String.valueOf(message);
}

//@Override public String toString(){
//    StringBuilder sb = new StringBuilder();
//    return "{" + "\"id\":\"" + id + "\",\"user\":\"" + userName + "\",\"message\":\"" + message + "\"}";
//}

}