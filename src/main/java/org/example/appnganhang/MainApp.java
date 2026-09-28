package org.example.appnganhang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL fxmlLocation =
                MainApp.class.getResource("/org/example/appnganhang/login-view.fxml");

        if (fxmlLocation == null) {
            throw new IOException(
                    "Không thể tìm thấy file login-view.fxml tại đường dẫn resources!"
            );
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);

        Scene scene = new Scene(fxmlLoader.load(), 500, 850);

        stage.setTitle("SMART BANK");
        stage.setScene(scene);

        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}