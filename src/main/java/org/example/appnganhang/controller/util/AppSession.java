package org.example.appnganhang.controller.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.appnganhang.MainApp;
import org.example.appnganhang.model.util.DBConnection;

import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** Shared authenticated identity used by the database access gate. */
public final class AppSession {
    private static volatile String username;
    private static volatile String role;
    private static volatile Stage stage;
    private static ScheduledExecutorService permissionMonitor;
    private static ScheduledFuture<?> permissionCheck;

    private AppSession() {}

    public static void activate(String loginName, String roleName) {
        username = loginName;
        role = roleName;
    }

    public static String getUsername() { return username; }
    public static String getRole() { return role; }

    public static void attachStage(Stage activeStage) {
        stage = activeStage;
        if (permissionMonitor != null) permissionMonitor.shutdownNow();
        permissionMonitor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "app-permission-monitor");
            thread.setDaemon(true);
            return thread;
        });
        permissionCheck = permissionMonitor.scheduleWithFixedDelay(() -> {
            if (username == null) return;
            try (Connection ignored = DBConnection.getConnection()) {
                // DBConnection checks account status and current role for every access.
            } catch (Exception ignored) {
                returnToLogin();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void returnToLogin() {
        Stage currentStage = stage;
        clear();
        if (currentStage == null) return;
        javafx.application.Platform.runLater(() -> {
            try {
                var resource = MainApp.class.getResource("/org/example/appnganhang/util-view/login-view.fxml");
                if (resource != null) {
                    currentStage.setScene(new Scene(FXMLLoader.load(resource)));
                    currentStage.setTitle("SMART BANK - Đăng nhập");
                    currentStage.show();
                }
            } catch (Exception e) {
                currentStage.close();
            }
        });
    }

    public static void clear() {
        if (permissionCheck != null) permissionCheck.cancel(true);
        if (permissionMonitor != null) permissionMonitor.shutdownNow();
        permissionCheck = null;
        permissionMonitor = null;
        username = null;
        role = null;
        stage = null;
    }
}
