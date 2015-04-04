import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

public class Server implements HttpHandler {
    private List<Message> history = new ArrayList<Message>();
    private MessageExchange messageExchange = new MessageExchange();
    
    
    public static void main(String[] args) {
        if (args.length != 1)
            System.out.println("Usage: java Server port");
        else {
            try {
                System.out.println("Server is starting...");
                Integer port = Integer.parseInt(args[0]);
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                System.out.println("Server started.");
                String serverHost = InetAddress.getLocalHost().getHostAddress();
                System.out.println("Get list of messages: GET http://" + serverHost + ":" + port + "/chat?token={token}");
                System.out.println("Send message: POST http://" + serverHost + ":" + port + "/chat provide body json in format {\"message\" : \"{message}\"} ");          
                server.createContext("/chat", new Server());
                server.setExecutor(null);
                server.start();
            } catch (IOException e) {
                System.out.println("Error creating http server: " + e);
            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException  {
        
        String response = "";
           
        if ("GET".equals(httpExchange.getRequestMethod())) {
            response = doGet(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            doPost(httpExchange);
        } else if ("DELETE".equals(httpExchange.getRequestMethod())) {
            response = doDelete(httpExchange);
        }
        else if ("PUT".equals(httpExchange.getRequestMethod()))
            doPut(httpExchange);
        else{
            response = "Unsupported http method: " + httpExchange.getRequestMethod();
        }
          
        sendResponse(httpExchange, response);
        
    }

    private String doGet(HttpExchange httpExchange) {
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> map = queryToMap(query);
            String token = map.get("token");
            if (token != null && !"".equals(token)) {
                int index = messageExchange.getIndex(token);
                String str =  messageExchange.getServerResponse(history.subList(index, history.size()));
                return str;
            } else {
                return "Token query parameter is absent in url: " + query;
            }
        }
        return  "Absent query in url";
    }

    private void doPost(HttpExchange httpExchange) {
        try {
            Message message = new Message(messageExchange.getClientMessage(httpExchange.getRequestBody()));
            System.out.println(message.getUserName() + ": " + message.getMessage());
            history.add(message);
        } catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }
    
    private   String doDelete(HttpExchange httpExchange){  
       
        String query = httpExchange.getRequestURI().getQuery();
        
        if(query != null){
          String idString = getStringNumber(query);
          if(idString.equals("no numbers"))
              return "ID query parameter is absent in url: " + query;              
          else {
              int id = Integer.parseInt(idString);
              int i;
              int size = history.size();
              for( i=0; i<size; i++){
                  if(history.get(i).getId() == id){   
                      history.remove(i);
                      break;
                  }                
              }
              if(i == size){              
                 return "No message have id = " + id;   
              }
              else {
                  
                  return "The message with id = " + id + " has successfully deleted";
              }
          }              
        }
        return  "Absent query in url";            
    }
    
    private void doPut(HttpExchange httpExchange){
           try{
           changeHistory(messageExchange.getClientMessage(httpExchange.getRequestBody()));
           }catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }        
    }
    
    private String getStringNumber(String param){
       String[] index = param.split("[^\\p{Digit}]+");
       if(index.length > 0){
           if(!index[0].equals(""))
               return index[0];
           else if (index.length > 1)
               return index[1];
       }
       return "no numbers";
    }

    private void sendResponse(HttpExchange httpExchange, String response) {
        try {
            byte[] bytes = response.getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin","*");
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write( bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
    
    private void changeHistory(JSONObject jsonobject){
        int id = Integer.parseInt(String.valueOf(jsonobject.get("id")));
        String message  = String.valueOf(jsonobject.get("message"));
        int size = history.size();
            for(int i=0; i<size; i++){
                if(history.get(i).getId() == id){
                    history.get(i).setMessage(message);                   
                    break;
                }
            }
    }
   
}
