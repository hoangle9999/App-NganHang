module org.example.appnganhang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.appnganhang to javafx.fxml;

    exports org.example.appnganhang;
    exports org.example.appnganhang.controller;
    opens org.example.appnganhang.controller to javafx.fxml;

    exports org.example.appnganhang.model.dao.Impl;
    opens org.example.appnganhang.model.dao.Impl to javafx.fxml;
}