package edu.virginia.sde.reviews;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddReviewApplication extends Application {

    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("add-review.fxml"));

        System.out.println("Printing");
        Scene scene = new Scene(loader.load());
        stage.setTitle("Add-review");
        stage.setScene(scene);
        stage.show();
    }
}

