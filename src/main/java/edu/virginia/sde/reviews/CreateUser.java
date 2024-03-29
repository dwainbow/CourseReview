package edu.virginia.sde.reviews;




import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateUser extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Create User");
        stage.setScene(scene);
        stage.show();
    }
}
