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
        // Sử dụng đường dẫn tuyệt đối bắt đầu từ dấu gạch chéo '/'
        URL fxmlLocation = MainApp.class.getResource("/org/example/appnganhang/login-view.fxml");

        if (fxmlLocation == null) {
            throw new IOException("Không thể tìm thấy file login-view.fxml tại đường dẫn resources!");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 450, 500);
        stage.setTitle("Hệ Thống Chuyển Tiền Ngân Hàng - Đăng Nhập");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}