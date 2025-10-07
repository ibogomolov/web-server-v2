package com.example.ibogomolov;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.HttpURLConnection;

@AllArgsConstructor
public class FileHandler implements HttpHandler {

    private String rootDir;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String relativePath = exchange.getRequestURI().getPath();
        IO.println(String.format("Accessing %s%s", this.rootDir, relativePath));
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        exchange.close();
    }
}
