import java.net.*;
import java.io.*;


public class ChatClient {
public static void main(String[] args) {
String host = args.length > 0 ? args[0] : "localhost";
int port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
try (Socket socket = new Socket(host, port);
BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {


// Thread to print server messages
new Thread(() -> {
try {
String s;
while ((s = in.readLine()) != null) System.out.println(s);
} catch (IOException e) { /*server closed*/ }
}).start();


// Send user input to server
String line;
while ((line = console.readLine()) != null) {
out.println(line);
if (line.equalsIgnoreCase("/quit")) break;
}


} catch (IOException e) {
System.err.println("Connection error: " + e.getMessage());
}
}
}