module org.example.appnganhang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.appnganhang to javafx.fxml;
    exports org.example.appnganhang;
}