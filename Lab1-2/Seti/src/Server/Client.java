package Server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Client {
    String name;
    String message;
    private DataOutputStream dos;

    public DataOutputStream getDos() {
        return dos;
    }



    Client(String name, DataOutputStream dos, DataInputStream dis) {
        this.name = name;
        this.dos = dos;

        new Thread(() -> {
            try {
                JSONParser parser = new JSONParser();
                String listUsers;
                while(true) {
                    SimpleDateFormat fdate = new SimpleDateFormat(" ( HH:mm )");
                    message = dis.readUTF();
                    JSONObject msg = (JSONObject) parser.parse(message);

                    if (Objects.equals(msg.get("message"), "/users")) {
                        listUsers = "\n----------------------------------\nСписок пользователей: \n";
                        List<Client> entry = Server.clients;
                        for (Client cli : entry) {
                            listUsers = listUsers + "\t" + cli.name + "\n";
                        }
                        listUsers = listUsers + "----------------------------------\n";
                        msg.put("name", "[ SERVER NOTICE ]");
                        msg.put("message", listUsers);
                        msg.put("time", fdate.format(new Date()));

                        dos.writeUTF(msg.toJSONString());
                        continue;
                    }
                    msg.put("time", fdate.format(new Date()));


                    System.out.println(message);
                    Server.broadcast(msg.toJSONString());
                }
            } catch (IOException E) {
                try {
                    dis.close();
                    dos.close();
                    Server.clients = Server.clients.stream()
                                .filter(e -> !(e == this))
                                .collect(Collectors.toList());

                    SimpleDateFormat fdate = new SimpleDateFormat(" ( HH:mm )");
                    String exit_message = "{ \"name\" : \"" + "[ SERVER NOTICE ]" + "\", \"message\" : \"" + name + " Disconnected" + "\", \"time\" : \"" + fdate.format(new Date()) + "\"}";
                    Server.broadcast(exit_message);

                    System.out.println("[Current User : " + Server.clients.size() + "]");

                } catch(IOException E2) {
                    E2.printStackTrace();
                }
            } catch (Exception E) {
                E.printStackTrace();
            }

        }).start();
    }



}
