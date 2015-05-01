import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class Server implements HttpHandler {

    private List<Message> history = new ArrayList<Message>();
    private MessageExchange messageExchange = new MessageExchange();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server port");
            return;
        }
        try {
            System.out.println("Server is starting...");
            Integer port = Integer.parseInt(args[0]);
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("Server started.");
            String serverHost = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Get list of messages: GET http://" + serverHost + ":" + port + "/chat?token={token}");
            System.out.println("Send message: POST http://" + serverHost + ":" + port + "/chat provide body json in format {\"message\" : \"{message}\"} ");
            System.out.println("Edit message: PUT http://" + serverHost + ":" + port + "/chat provide body json in format {\"message\" : \"{message}\"} ");
            System.out.println("Delete message: DELETE http://" + serverHost + ":" + port + "/chat?(id)");
            server.createContext("/chat", new Server());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            System.out.println("Error creating http server");
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String response = "";

        try {
            System.out.println("Begin request " + httpExchange.getRequestMethod());
            String query = httpExchange.getRequestURI().getQuery();
            System.out.println("Query " + query);

            if ("GET".equals(httpExchange.getRequestMethod())) {
                response = doGet(httpExchange);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                doPost(httpExchange);
            } else if ("PUT".equals(httpExchange.getRequestMethod())) {
                doPut(httpExchange);
            } else if("DELETE".equals(httpExchange.getRequestMethod())){
                doDelete(httpExchange);
            }else if ("OPTIONS".equals(httpExchange.getRequestMethod())) {
                response = "";
            } else {
                throw new Exception("Unsupported http method: " + httpExchange.getRequestMethod());
            }

            sendResponse(httpExchange, response);
            System.out.println("Response sent, size " + response.length());
            System.out.println("End request " + httpExchange.getRequestMethod());
            return;
        } catch (Exception e) {
            response = messageExchange.getErrorMessage(e.getMessage());
            e.printStackTrace();
        } 

        try{
            sendResponse(httpExchange, response);
        } catch(Exception e) {
            System.out.println("Unable to send response !");
        }
    }

    private String doGet(HttpExchange httpExchange) throws Exception {

        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            Map<String, String> map = queryToMap(query);
            String token = map.get("token");
            System.out.println("Token " + token);   
            if (token != null && !"".equals(token)) {
                int index = messageExchange.getIndex(token);
                String str =  messageExchange.getServerResponse(history.subList(index, history.size()), index);
                return str;
            } 
            throw new Exception("Token query parameter is absent in url: " + query);
        }
        throw new Exception("Absent query in url");
    }

    private void doPost(HttpExchange httpExchange) throws Exception {
        
         try {
            Message message = new Message(messageExchange.getClientMessage(httpExchange.getRequestBody()));
            System.out.println(message.getUserName() + ": " + message.getMessage());
            history.add(message);
        } catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }


    }

    private void doPut(HttpExchange httpExchange) throws Exception {

        try{
           changeHistory(messageExchange.getClientMessage(httpExchange.getRequestBody()));
           }catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }   

    }

    private void changeHistory(JSONObject jsonobject) throws Exception{
        String id = String.valueOf(jsonobject.get("id"));
        String message  = String.valueOf(jsonobject.get("message"));
        int size = history.size();
            for(int i=0; i<size; i++){
                if(history.get(i).getId().equals(id)){
                    history.get(i).setMessage(message); 
                    System.out.println("Edited message from User: " + history.get(i));                  
                    return;
                }
            }
            throw new Exception("No message have id = " + id);
    }

    private  void doDelete(HttpExchange httpExchange) throws Exception {  
       
        String query = httpExchange.getRequestURI().getQuery();
        
        if(query != null){
          String idString = getStringNumber(query);
          if(idString.equals("no numbers"))
              throw new Exception("ID query parameter is absent in url: " + query);              
          else {
              int i;
              int size = history.size();
              for( i=0; i<size; i++){
                  if(history.get(i).getId().equals(idString)){   
                      history.remove(i);
                      break;
                  }                
              }
              if(i == size)
                 throw new Exception("No message have id = " + idString);              
              return;
          }              
        }
        throw new Exception("Absent query in url");            
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

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        byte[] bytes = response.getBytes();
        Headers headers = httpExchange.getResponseHeaders();

        headers.add("Access-Control-Allow-Origin","*");
        if("OPTIONS".equals(httpExchange.getRequestMethod())) {
            headers.add("Access-Control-Allow-Methods","PUT, DELETE, POST, GET, OPTIONS");
        }
        httpExchange.sendResponseHeaders(200, bytes.length);
        writeBody(httpExchange, bytes);
    }

    private void writeBody(HttpExchange httpExchange, byte[] bytes) throws IOException {
        OutputStream os = httpExchange.getResponseBody();

        os.write( bytes);
        os.flush();
        os.close();
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
}
