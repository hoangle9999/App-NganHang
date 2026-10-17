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

        URL fxmlLocation = MainApp.class.getResource(
                "/org/example/appnganhang/util-view/login-view.fxml"
        );

        if (fxmlLocation == null) {
            throw new IOException(
                    "Không tìm thấy customer-login-view.fxml. "
                            + "Hãy kiểm tra vị trí file trong src/main/resources!"
            );
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);

        Scene scene = new Scene(fxmlLoader.load(), 500, 850);

        stage.setTitle("SMART BANK - Đăng Nhập");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}