import com.example.ibogomolov.CustomThreadPoolExecutor;
import com.example.ibogomolov.FileHandler;
import com.sun.net.httpserver.HttpServer;

public static final int THREADS_MAX_COUNT = 5;

void main() {
    try {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(80), 0);
        server.createContext("/", new FileHandler("./web-root"));
        server.setExecutor(new CustomThreadPoolExecutor());
        server.start();
        IO.println("The file server is started.");
    } catch (IOException e) {
        System.err.println("Error while starting the file server: " + e);
    }
}