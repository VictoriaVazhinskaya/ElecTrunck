import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Message{
public String id;
public String userName;
public String message;

public Message(String id, String userName, String message){
this.id  = id;
this.userName = userName;
this.message = message;
}

public Message(JSONObject jsonobject) {
this.id = String.valueOf(jsonobject.get("id"));
this.userName = String.valueOf(jsonobject.get("user"));
this.message = String.valueOf(jsonobject.get("message"));
}

public String getUserName(){
    return userName;
}

public String getId(){
    return id;
}

public String getMessage(){
    return message;
}

public void setId(String value) {
		this.id = value;
	}

public void setMessage(String message){
    this.message = String.valueOf(message);
}

public void setUserName(String name) {
		this.userName = name;
	}

@Override public String toString(){
    //StringBuilder sb = new StringBuilder();
    return "{" + "\"id\":\"" + id + "\",\"user\":\"" + userName + "\",\"message\":\"" + message + "\"}";
}

}