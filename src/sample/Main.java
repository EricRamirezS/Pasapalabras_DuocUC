package sample;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    public static final Rectangle2D SCREEN = Screen.getPrimary().getBounds();
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Pasapalabras");
        primaryStage.setScene(new Scene(root, SCREEN.getWidth(), SCREEN.getHeight()));
        primaryStage.show();

        try {
            List<Webcam> webcam = Webcam.getWebcams();
            if (webcam.get(0)!=null){
                webcam.get(0).open();
            }
        }catch (Exception ignore){}
    }


    public static void main(String[] args) {
        launch(args);
    }
}
