import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Client implements Runnable {

    private List<Message> history = new ArrayList<Message>();
    private MessageExchange messageExchange = new MessageExchange();
    private static String userName;
    private String host;
    private Integer port;
    

    public Client(String host, Integer port) {
        this.host = host;
        this.port = port;
        userName = new String(getNewUserName());
    }


    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Usage: java ChatClient host port");
        else {
            System.out.println("Connection to server...");
            String serverHost = args[0];
            Integer serverPort = Integer.parseInt(args[1]);
            Client client = new Client(serverHost, serverPort);           
            new Thread(client).start();
            System.out.println("Connected to server: " + serverHost + ":" + serverPort);
            client.listen();
        }
    }

    private HttpURLConnection getHttpURLConnection() throws IOException {
        URL url = new URL("http://" + host + ":" + port + "/chat?token=" + messageExchange.getToken(history.size()));
        return (HttpURLConnection) url.openConnection();
    }

    public List<Message> getMessages() {
        List<Message> list = new ArrayList<Message>();
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection();
            connection.connect();
            String response = messageExchange.inputStreamToString(connection.getInputStream());      
            JSONObject jsonObject = messageExchange.getJSONObject(response);
            //System.out.println(jsonObject.toJSONString());
            JSONArray jsonArray = (JSONArray) jsonObject.get("messages");            
            int size = jsonArray.size();
            for (int i=0; i<size; i++) {
                Message msg = new Message((JSONObject)jsonArray.get(i));
                System.out.println(msg.getUserName() + ": " + msg.getMessage());
                list.add(msg);
            }
        } catch (IOException e) {
            System.err.println("ERROR_1_getM: " + e.getMessage());
        } catch (ParseException e) {
            System.err.println("ERROR_2_getM: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return list;
    }

    public void sendMessage(String message) {
        HttpURLConnection connection = null;
        try {
            connection = getHttpURLConnection();
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            byte[] bytes = messageExchange.getClientSendMessageRequest(getUniqId(), userName, message).getBytes();
            wr.write(bytes, 0, bytes.length);
            wr.flush();
            wr.close();

            connection.getInputStream();

        }  
        catch (IOException e){
            System.err.println("ERROR: " + e.getMessage());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    

    public void listen() {
        while (true) {
            List<Message> list = getMessages();
            if (list.size() > 0) {
                history.addAll(list);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("ERROR_listen: " + e.getMessage());
            }
        }
    }
    
    public static int getUniqId(){
        
        int uniqueId;
        Random rndm = new Random();
        Date date = new Date();
        uniqueId = (int) (rndm.nextInt(100)*rndm.nextInt(100)*date.getTime());
        return Math.abs(uniqueId);
        
    }
    
    public static String getNewUserName(){
        
        StringBuilder sb = new StringBuilder();
        Random rndm = new Random();
        sb.append("User");
        sb.append(rndm.nextInt(89)+10);
        return sb.toString();
        
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message = scanner.nextLine();
            sendMessage(message);
        }
    }
}
