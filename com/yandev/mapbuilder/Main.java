

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class Main extends Application {
    private Parent root;
    private Dimension screenSize;
    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Internet Hero Map Builder");
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        primaryStage.setScene(new Scene(root, screenSize.width, screenSize.height- (int) (screenSize.height/20) ));
        primaryStage.setResizable(false);
        primaryStage.show();
        showStartWindow(primaryStage);
    }

    private void showStartWindow(Stage primaryStage) throws IOException {

        Parent startWindowLayout = FXMLLoader.load(getClass().getResource("window_start.fxml"));
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(startWindowLayout);
        Scene secondScene = new Scene(secondaryLayout, 600, 400);


        // New window (Stage)
        Stage startWindow = new Stage();
        startWindow.setTitle("Выберите парамеры");
        startWindow.setScene(secondScene);
        startWindow.setResizable(false);



        // Specifies the modality for new window.
        startWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        startWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        startWindow.setX(primaryStage.getX());
        startWindow.setY(primaryStage.getY());

        startWindow.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });
        WindowStartController.initController(startWindow);
        startWindow.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
