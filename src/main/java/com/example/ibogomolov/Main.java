package com.example.ibogomolov;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {
    public static final int THREADS_MAX_COUNT = 5;

    static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(80), 0);
            server.createContext("/", FileHandler.create("./dev-web-root"));
            server.setExecutor(new CustomThreadPoolExecutor());
            server.start();
            IO.println("The file server is started.");
        } catch (IOException e) {
            System.err.println("Error while starting the file server: " + e);
        }
    }
}