package com.example.ibogomolov;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

    @Mock
    private HttpExchange mockExchange;
    @TempDir
    Path tempDir;
    private HttpHandler handler;

    @BeforeEach
    void setUp() {
        handler = FileHandler.create(tempDir);
        Mockito.when(mockExchange.getRequestMethod()).thenReturn("GET");
    }

    @Test
    void accessFile() throws IOException {
        Path fileIn = tempDir.resolve("page.html");
        FileOutputStream stream = new FileOutputStream(fileIn.toFile());
        stream.write("<h1>Hello world</h1>".getBytes());
        stream.close();
        Path fileOut = tempDir.resolve("output.html");
        Headers headers = new Headers();

        Mockito.when(mockExchange.getRequestURI()).thenReturn(URI.create("http://localhost/page.html"));
        Mockito.when(mockExchange.getResponseHeaders()).thenReturn(headers);
        Mockito.when(mockExchange.getResponseBody()).thenReturn(new FileOutputStream(fileOut.toFile()));

        handler.handle(mockExchange);

        Mockito.verify(mockExchange).sendResponseHeaders(HttpURLConnection.HTTP_OK, 20);
        assertEquals(headers.get("Content-Type"), List.of("text/html"));
        assertEquals(-1, Files.mismatch(fileIn, fileOut));
    }

    @Test
    void accessDir() throws IOException {
        Path dir = tempDir.resolve("directory");
        Files.createDirectory(dir);
        Mockito.when(mockExchange.getRequestURI()).thenReturn(URI.create("http://localhost/directory"));

        handler.handle(mockExchange);

        Mockito.verify(mockExchange).sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
    }

    @Test
    void receivePostRequest() throws IOException {
        Mockito.when(mockExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(mockExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("".getBytes()));
        Mockito.when(mockExchange.getResponseHeaders()).thenReturn(new Headers());

        handler.handle(mockExchange);

        Mockito.verify(mockExchange).sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
    }


    @Test
    void accessMissingFile() throws IOException {
        Mockito.when(mockExchange.getRequestURI()).thenReturn(URI.create("http://localhost/missing.html"));

        handler.handle(mockExchange);

        Mockito.verify(mockExchange).sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
    }

    @Test
    void test() throws IOException {
        Path fileIn = tempDir.resolve("page.html");
        FileOutputStream stream = new FileOutputStream(fileIn.toFile());
        stream.write("<h1>Hello world</h1>".getBytes());
        stream.close();

        Path fileOut = tempDir.resolve("output.html");
        FileOutputStream outputStream = new FileOutputStream(fileOut.toFile());
        outputStream.close();
        Headers headers = new Headers();

        Mockito.when(mockExchange.getRequestURI()).thenReturn(URI.create("http://localhost/page.html"));
        Mockito.when(mockExchange.getResponseHeaders()).thenReturn(headers);
        Mockito.when(mockExchange.getResponseBody()).thenReturn(outputStream);

        handler.handle(mockExchange);

        Mockito.verify(mockExchange).sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
    }
}