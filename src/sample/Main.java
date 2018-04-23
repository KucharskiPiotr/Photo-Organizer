package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Static variable to be accessed by Controller class
     */
    public static String apiKey;

    /**
     * Method initializes main window and makes Controller usable
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("okno.fxml"));
        primaryStage.setTitle("Photo Organiser");
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.setResizable(false);
        primaryStage.show();

        FXMLLoader loader = new FXMLLoader();
        Controller c = loader.getController();
        if(c != null)
            c.setStage(primaryStage);
    }

    /**
     * Main method to start program, as parameter gets Indico API key
     * @param args Indico API user's key
     */
    public static void main(String[] args)
    {
        apiKey = args[0];
        launch(args);
    }
}
