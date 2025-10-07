package com.example.ibogomolov;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpHandlers;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileHandler implements HttpHandler {

    public static final List<String> ALLOWED_METHODS = List.of("GET");

    private String rootDir;

    /**
     * Inspired by the similar one in sun.net.httpserver.simpleserver.FileServerHandler.
     */
    public static HttpHandler create(String rootDir) {
        HttpHandler badMethodHandler = HttpHandlers.of(HttpURLConnection.HTTP_BAD_METHOD,
                Headers.of("Allow", "GET"), "");
        return HttpHandlers.handleOrElse(r -> ALLOWED_METHODS.contains(r.getRequestMethod()),
                new FileHandler(rootDir), badMethodHandler);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String relativePath = exchange.getRequestURI().getPath();
        IO.println(String.format("Accessing %s%s", this.rootDir, relativePath));
        File file = new File(this.rootDir, relativePath);
        if (file.exists() && file.isFile() && file.canRead()) {
            FileHandler.setContentType(file, exchange);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length());
            try (OutputStream output = exchange.getResponseBody()) {
                Files.copy(file.toPath(), output);
            } catch (IOException e) {
                System.err.println("Error reading file " + file.getAbsolutePath());
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
            exchange.close();
        }
    }

    private static void setContentType(File file, HttpExchange exchange) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        exchange.getResponseHeaders().add("Content-Type", mimeType);
        IO.println(exchange.getResponseHeaders());
    }
}
