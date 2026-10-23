package org.example.appnganhang.controller.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {

    private static final String BASE_PATH = "/org/example/appnganhang/";

    private static final String[] VIEW_FOLDERS = {
            "admin-view/",
            "CSKH-view/",
            "giaoDichVien-view/",
            "giaDichVien/",
            "khachHang-view/",
            "util-view/"
    };

    private static final String TITLE = "Hệ Thống Chuyển Tiền Ngân Hàng - SMART BANK";

    public static void moView(String tenFile, ActionEvent event) {

        if ("login-view.fxml".equals(tenFile)) AppSession.clear();

        for (String folder : VIEW_FOLDERS){
            String DUONG_DAN = BASE_PATH + folder + tenFile;
            if (Navigation.class.getResource(DUONG_DAN) != null){
                try {
                    FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(DUONG_DAN));

                    Parent root = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    stage.setScene(new Scene(root));
                    stage.setTitle(TITLE);
                    stage.show();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return;
            }
        }
        System.out.println("Không tìm thấy FXML: " + tenFile);
    }
}
