package com.facturo.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogService {
    private static final String APP_DIR_NAME = ".facturo";
    private static final String LOG_FILE_NAME = "facturo.log";

    public static String getAppDir() {
        String userHome = System.getProperty("user.home");
        Path appDirPath = Paths.get(userHome, APP_DIR_NAME);
        File appDir = appDirPath.toFile();

        if (!appDir.exists()) {
            boolean created = appDir.mkdirs();
            if (!created) {
                System.err.println("Could not create application directory at: " + appDirPath.toString());
            }
        }
        return appDirPath.toString();
    }

    public static String getLogFilePath() {
        return Paths.get(getAppDir(), LOG_FILE_NAME).toString();
    }

    public static void log(String level, String message, Throwable exception) {
        String logPath = getLogFilePath();
        try (FileWriter fw = new FileWriter(logPath, true);
                PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println(String.format("[%s] [%s] %s", timestamp, level, message));
            if (exception != null) {
                exception.printStackTrace(pw);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public static void info(String message) {
        log("INFO", message, null);
    }

    public static void error(String message, Throwable exception) {
        log("ERROR", message, exception);
    }
}
