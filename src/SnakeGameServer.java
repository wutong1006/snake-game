import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.awt.Desktop;
import java.net.URI;

public class SnakeGameServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/")) path = "/index.html";

                File file = new File("web" + path);
                if (file.exists()) {
                    byte[] content = Files.readAllBytes(file.toPath());
                    String mime = "application/octet-stream";
                    if (path.endsWith(".html")) mime = "text/html; charset=utf-8";
                    else if (path.endsWith(".js")) mime = "application/javascript";
                    else if (path.endsWith(".css")) mime = "text/css";
                    else if (path.endsWith(".png")) mime = "image/png";
                    else if (path.endsWith(".ico")) mime = "image/x-icon";
                    exchange.getResponseHeaders().set("Content-Type", mime);
                    exchange.sendResponseHeaders(200, content.length);
                    exchange.getResponseBody().write(content);
                    exchange.getResponseBody().close();
                } else {
                    String resp = "<h1>404 Not Found</h1>";
                    byte[] b = resp.getBytes("utf-8");
                    exchange.sendResponseHeaders(404, b.length);
                    exchange.getResponseBody().write(b);
                    exchange.getResponseBody().close();
                }
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("========================================");
        System.out.println("  Snake Game Server Started!");
        System.out.println("  Open: http://localhost:" + port);
        System.out.println("  Press Ctrl+C to stop.");
        System.out.println("========================================");

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:" + port));
        }
    }
}
