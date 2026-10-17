module org.example.appnganhang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.example.appnganhang to javafx.fxml;

    exports org.example.appnganhang;

    exports org.example.appnganhang.model.dao.Impl;
    opens org.example.appnganhang.model.dao.Impl to javafx.fxml;


    exports org.example.appnganhang.controller.util;
    opens org.example.appnganhang.controller.util to javafx.fxml;

    exports org.example.appnganhang.controller.admin;
    opens org.example.appnganhang.controller.admin to javafx.fxml;

    exports org.example.appnganhang.controller.giaodichvien;
    opens org.example.appnganhang.controller.giaodichvien to javafx.fxml;

    exports org.example.appnganhang.controller.khachhang;
    opens org.example.appnganhang.controller.khachhang to javafx.fxml;

    exports org.example.appnganhang.controller.cskh;
    opens org.example.appnganhang.controller.cskh to javafx.fxml;

    // Cho phép JavaFX đọc thuộc tính entity qua reflection
    opens org.example.appnganhang.model.entity to javafx.base;
}