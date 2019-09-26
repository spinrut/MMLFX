package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mml.MMLPlayer;

public class Main extends Application {

    private MMLPlayer mmlPlayer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        mmlPlayer = new MMLPlayer();
        controller.setMMLPlayer(mmlPlayer);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 300));

        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_HIDING,
                event -> { mmlPlayer.close(); });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
