package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class room {
    public static Thread th;
    Socket sock;
    DataOutputStream dos;
    DataInputStream dis;

    boolean loseConnection = false;

    @FXML
    public TextField myMsg;
    @FXML
    public TextArea chatLog;

    public room() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        try {



            sock = data.socket;
            dos = new DataOutputStream(sock.getOutputStream());
            dis = new DataInputStream(sock.getInputStream());

            dos.writeUTF(data.name);
            /*
            * This Thread let the client recieve the message from the server for any time;
            */
            th = new Thread(() -> {
                try {

                    JSONParser parser = new JSONParser();

                    while(true) {
                        String newMsgJson = dis.readUTF();

                        System.out.println("RE : " + newMsgJson);
                        Message newMsg = new Message();

                        Object obj = parser.parse(newMsgJson);
                        JSONObject msg = (JSONObject) obj;

                        newMsg.setName((String) msg.get("name"));
                        newMsg.setMessage((String) msg.get("message"));
                        newMsg.setTime((String) msg.get("time"));

                        chatLog.appendText(newMsg.getName() + newMsg.getTime() + " : " + newMsg.getMessage() + "\n");
                    }
                } catch(Exception E) {
                    loseConnection = true;
                    E.printStackTrace();
                }

            });

            th.start();

        } catch(IOException E) {
            loseConnection = true;
            alert.setContentText("Потеря соединения!");
            alert.showAndWait();
            exit();

            E.printStackTrace();
        }

    }

    public void onClickSend() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);

        if (loseConnection) {
            alert.setContentText("Соединение потеряно!");
            alert.showAndWait();
            exit();
            return;
        }
        try {
            String msg = myMsg.getText();

            //String json = "{" + " 'name' : '" + data.name + "', 'message' : '" + msg + "'" + "}";

            JSONObject js = new JSONObject();
            js.put("name", data.name);
            js.put("message", msg);

            String json = js.toJSONString();


            System.out.println(json);

            dos.writeUTF(json);
            myMsg.setText("");
            myMsg.requestFocus();



        } catch(IOException E) {
            alert.setContentText("Потеря соединения!");
            alert.showAndWait();
            exit();
            E.printStackTrace();
        }

    }

    public void exit() {
        Stage stage;
        stage = (Stage) myMsg.getScene().getWindow();
        //Parent parent = FXMLLoader.load(getClass().getResource("room.fxml"));
        try {
            Parent root = FXMLLoader.load(login.class.getResource("login.fxml"));

        stage.setScene(new Scene(root, 600, 400));

        stage.setTitle(data.name);
        stage.setOnCloseRequest(e-> {
            //e.consume();
            room.th.stop();
            System.exit(0);
        });
        stage.setResizable(false);

        stage.show();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public void buttonPressed(KeyEvent e) {
        if(e.getCode().toString().equals("ENTER"))
        {
            onClickSend();
        }
    }
}

