package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private int port;
    public static void main(String[] args) throws IOException{
        HttpServer main = new HttpServer();
        main.configServer();
        ServerSocket server = new ServerSocket(main.port);
        while (true) {
            Socket client = server.accept();
            System.out.println("CLI Connected: " + client.getRemoteSocketAddress());
            handle(client);
        }
    }

    private void configServer() {
        this.port = ServerConfig.PORT.value;
    }

    static void handle(Socket client) throws IOException{
        BufferedReader in = new BufferedReader(
            new InputStreamReader(client.getInputStream())
        );

        String line;
        while ((line = in.readLine()) != null) {
            if(line.isEmpty()) {
                break;
            }

            

            System.out.println(line);
        }

        String route = in.readLine();
        String[] parts = route.split(" ");
        String method = parts[0];
        String path = parts[1];
        String http = parts[2];
    }
}

enum ServerConfig {
    PORT(8080);

    public final int value;

    ServerConfig(int value) {
        this.value = value;
    }
}
