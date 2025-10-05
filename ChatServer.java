import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
    private int port;
    private Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        }
    }

    public void broadcast(String message, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c != sender) {
                    c.send(message);
                }
            }
        }
    }

    public void remove(ClientHandler c) {
        clients.remove(c);
    }

    public static void main(String[] args) throws IOException {
        int port = 12345; // default port
        ChatServer server = new ChatServer(port);
        server.start();
    }

    // -- inner handler --
    static class ClientHandler implements Runnable {
        private Socket socket;
        private ChatServer server;
        private PrintWriter out;
        private BufferedReader in;
        private String name = "Anonymous";

        ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
        }

        public void send(String msg) {
            if (out != null) out.println(msg);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Welcome! Please enter your name:");
                name = in.readLine();
                if (name == null || name.isBlank()) {
                    name = "Anonymous";
                }
                server.broadcast(name + " has joined.", this);

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("/quit")) break;
                    server.broadcast(name + ": " + line, this);
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                server.remove(this);
                server.broadcast(name + " has left.", this);
            }
        }
    }
}
