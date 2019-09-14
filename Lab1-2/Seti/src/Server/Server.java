package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Server {

    public static List<Client> clients;
    public static List<String> banClients = new ArrayList<>();
    public static DataOutputStream dos;
    DataInputStream dis;

    Server() {

        banClients.add("Roman");
        System.out.println("Сервер запущен!");

        String name;
        Socket client;

        clients = new ArrayList<Client>();

        try {
            ServerSocket server = new ServerSocket(10001);

            while(true) {
                client = server.accept();
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());

                SimpleDateFormat fdate = new SimpleDateFormat(" ( HH:mm )");

                name = dis.readUTF();
                boolean flagBan = false;
                List<String> ban = Server.banClients;
                for (String elem : ban) {
                    if (Objects.equals(name, elem)){
                        dos.writeUTF("{ \"name\" : \"" + "[ SERVER NOTICE ]" + "\", \"message\" : \"" + name +"\n\nВы забанены!" + "\", \"time\" : \"" + fdate.format(new Date()) + "\"}");
                        flagBan = true;
                    }
                }
                if (flagBan) {
                    continue;
                }

                Client user = new Client(name, dos, dis);
                System.out.println("Подключился: " + name);
                clients.add(user);

                String enter_message = "{ \"name\" : \"" + "[ SERVER NOTICE ]" + "\", \"message\" : \"" + name +" Connected" + "\", \"time\" : \"" + fdate.format(new Date()) + "\"}";
                //System.out.println(enter_message);

                broadcast(enter_message);

                System.out.println("[Current User : " + Server.clients.size() + "]");

            }
        } catch(IOException E) {
            E.printStackTrace();
        }
    }

    public static void broadcast(String enter_message) {
        try {
            List<Client> entry = Server.clients;
            for (Client cli : entry) {
                DataOutputStream edos = cli.getDos();
                edos.writeUTF(enter_message);
            }
        } catch(IOException E) {
            E.printStackTrace();
        }
    }
}
