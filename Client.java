import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

public class Client extends Thread {
    public static void main(String[] args) {
        System.out.println("Client is active!\n");
        // Я знаю, что использовать java.util.logging плохо, с logj2 и logback разобраться не успела,
        // подвязать не получилось. Пока что.
        try {
            socket = new Socket("localhost", 3000);
        } catch (IOException e) {
            System.err.println("Connection error");
        }
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            registration();
            read();
            write();
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
            errorOrExit();
        }
    }

    private static void registration() {
        System.out.print("Write your nickname: ");
        try {
            nickname = input.readLine();
            out.write("Welcome, " + nickname + "\n");
            out.flush();
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
        }
    }

    private static void errorOrExit() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
        }
    }

    private static void read() {
        try {
            while (true) {
                String str = in.readLine();
                if (str.equals("exit")) {
                    errorOrExit();
                    break;
                }
                System.out.println(str);
            }
        } catch (IOException e) {
            logger.log(new LogRecord(Level.INFO, e.getMessage()));
            errorOrExit();
        }
    }

    public static void write() {
        while (true) {
            try {
                Date date = new Date();
                String newInput = input.readLine();
                if (newInput.equals("exit")) {
                    errorOrExit();
                    break;
                } else {
                    out.write("[" + new SimpleDateFormat("HH:mm:ss").format(date) + "] " + nickname + ": " + newInput + "\n");
                }
                out.flush();
            } catch (IOException e) {
                logger.log(new LogRecord(Level.INFO, e.getMessage()));
                errorOrExit();
            }
        }
    }

    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static BufferedReader input;
    private static String nickname;
    static Logger logger = Logger.getLogger(Client.class.getName());
}