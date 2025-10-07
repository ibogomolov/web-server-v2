package com.example.ibogomolov;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

@AllArgsConstructor
public class FileHandler implements HttpHandler {

    private String rootDir;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String relativePath = exchange.getRequestURI().getPath();
        IO.println(String.format("Accessing %s%s", this.rootDir, relativePath));
        File file = new File(this.rootDir, relativePath);
        if (file.exists() && file.isFile() && file.canRead()) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
            try (OutputStream output = exchange.getResponseBody()) {
                Files.copy(file.toPath(), output);
            } catch (IOException e) {
                System.err.println("Error reading file " + file.getAbsolutePath());
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            exchange.close();
        }
    }
}
