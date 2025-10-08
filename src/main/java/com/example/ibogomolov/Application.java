package com.example.ibogomolov;

import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;


public class Application {

    public static final int MAX_THREADS_COUNT = 5;

    @Getter
    private int port;
    @Getter
    private Path webRoot;

    static void main(String[] args) {
        Application app = new Application();
        app.parseArgs(args);

        int port = app.getPort();
        Path webRoot = app.getWebRoot();

        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);
            server.createContext("/", FileHandler.create(webRoot));
            server.setExecutor(new CustomThreadPoolExecutor(MAX_THREADS_COUNT));
            server.start();
            IO.println("The file server is started.");
        } catch (IOException e) {
            System.err.printf("Error while starting the file server: %s%n", e);
            System.exit(1);
        }
    }

    private void parseArgs(String[] args) {
        Options options = getOptions();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            this.port = cmd.getParsedOptionValue("port");

            Path root = cmd.getParsedOptionValue("root");
            if (!isValidWebRoot(root)) {
                throw new ParseException("Invalid web root");
            }
            this.webRoot = root;
        } catch (ParseException pe) {
            System.err.printf("Error while parsing command line arguments: %s%n", pe);
            HelpFormatter formatter = HelpFormatter.builder().setShowSince(false).get();
            try {
                formatter.printHelp("./gradlew run --args='-port 80 -root ./webroot'", "", options, "", false);
            } catch (IOException ioe) {
                System.err.printf("Cannot print command line help: %s%n", ioe);
            }
            System.exit(1);
        }
    }

    private static boolean isValidWebRoot(Path root) {
        File file = root.toFile();
        return file.exists() && file.isDirectory() && file.canRead();
    }

    private static Options getOptions() {
        Option portOpt = Option.builder("port")
                .argName("int")
                .required()
                .hasArg()
                .desc("a port to listen on")
                .type(Integer.class)
                .get();

        Option rootOpt = Option.builder("root")
                .argName("path")
                .required()
                .hasArg()
                .desc("a path to the web root directory")
                .type(Path.class)
                .get();
        Options options = new Options();
        options.addOption(portOpt);
        options.addOption(rootOpt);
        return options;
    }
}