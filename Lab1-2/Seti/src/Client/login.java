package Client;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.net.Socket;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.Objects;



public class login {
    @FXML public TextField server_ip;
    @FXML public TextField port;
    @FXML public TextField name;
    @FXML public String sPort;

    private final Pattern pattern;
    {
        this.pattern = Pattern.compile(
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    }

    public boolean validateIP(String ipAddress) {
        return this.pattern.matcher(ipAddress).matches();
    }


    @SuppressWarnings("deprecation")
	public void onClick() throws IOException{
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);

        System.out.println("Clicked");
        data.ip = server_ip.getText();
        if (!validateIP(data.ip)) {
            alert.setContentText("Введен не валидный IP.");
            alert.showAndWait();
            return;
        }
        this.sPort = port.getText();
        data.name = name.getText();
        System.out.println("name" + data.name);
        if (Objects.equals(data.name, "")) {
            alert.setContentText("Имя не может быть пустым!");
            alert.showAndWait();
            return;
        }
        data.port = Integer.parseInt(sPort);

        Socket sock;
        try {
            sock = new Socket(data.ip, 10001);//data.port
        } catch (Exception E) {
        	System.err.println(E.getMessage());
            alert.setContentText("Ошибка подключения к серверу. \nПроверьте введенные данные.");
            alert.showAndWait();
            return;
        }
        data.socket = sock;

        Stage stage;
        stage = (Stage) server_ip.getScene().getWindow();
        //Parent parent = FXMLLoader.load(getClass().getResource("room.fxml"));
        Parent root = FXMLLoader.load(login.class.getResource("room.fxml"));
        stage.setScene(new Scene(root, 600, 400));

        stage.setTitle(data.name);
        stage.setOnCloseRequest(e-> {
            //e.consume();
            room.th.stop();
            System.exit(0);
        });
        stage.setResizable(false);

        stage.show();
    }

    public void buttonPressed(KeyEvent e) {
        if(e.getCode().toString().equals("ENTER"))
        {
            try {
                onClick();
            } catch(IOException E) {
                System.out.println("Error Enter");
            }

        }
    }


}
