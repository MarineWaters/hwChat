import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class Server extends Thread {
    public static List<Server> list = new LinkedList<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(3000)) {
            System.out.println("Server is active!\n");
            while (true) {
                Socket socket = server.accept();
                try {
                    list.add(new Server(socket));
                } catch (IOException e) {
                    logger.log(new LogRecord(Level.INFO, e.getMessage()));
                    socket.close();
                    break;
                }
            }
        }
    }

    public Server(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            String inp = in.readLine();
            try {
                out.write(inp + "\n");
                out.flush();
            } catch (IOException e) {
                logger.log(new LogRecord(Level.INFO, e.getMessage()));
            }
            try {
                while (true) {
                    inp = in.readLine();
                    if (inp.equals("exit")) {
                        errorOrExit();
                        break;
                    }
                    System.out.println("Log: " + inp);
                    for (Server serv : list) {
                        serv.send(inp);
                    }
                }
            } catch (Exception e) {
                logger.log(new LogRecord(Level.INFO, e.getMessage()));
            }
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
            errorOrExit();
        }
    }

    private void send(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
        }

    }

    private void errorOrExit() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Server serv : list) {
                    if (serv.equals(this))
                        serv.interrupt();
                    list.remove(this);
                }
            }
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
        }
    }

    static Logger logger = Logger.getLogger(Server.class.getName());
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
}