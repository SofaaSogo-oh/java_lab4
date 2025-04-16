package serv;

import com.sun.net.httpserver.Request;
import jdk.jshell.JShell;
import log.Logger;
import log.msg.CommonMessage;
import log.msg.RequestMessage;
import log.msg.ResponseMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Server {
    public int port;
    public String host;
    private ServerSocket socket_;
    public Logger logger;

    Server(Logger sv_logger, int sv_port, String sv_host) {
        logger = sv_logger;
        port = sv_port;
        host = sv_host;
        try {
            socket_ = new ServerSocket(port);
        }
        catch (IOException err) {
            System.err.printf("Не удаётеся открыть сокет для сервера: %s%n", err);
        }
    }

    public void go() {
        class Listener implements Runnable {
            final Socket socket;

            public Listener(Socket aSock) {
                socket = aSock;
            }

            @Override
            public void run() {
                try (var os  = new PrintWriter(socket.getOutputStream(), true);
                     var is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     var js = JShell.create()){
                    logger.leave_message(new CommonMessage("Listener is running..."));
                    String expression = is.readLine();
                    logger.leave_message(new RequestMessage(expression));
                    String result = "foo";
                    os.println(result);
                    logger.leave_message(new ResponseMessage(result));
                } catch (IOException err) {
                   System.err.printf("Exception caught: %s%n",  err);
                }
            }
        }
        logger.leave_message(new CommonMessage("Server is running..."));
        for (;;) {
            try {
                var socket = socket_.accept();
                var listener = new Listener(socket);
                var thread = new Thread(listener);
                thread.start();
            } catch (IOException err) {
                System.err.printf("Exception caught: %s\n%n", err);
            }
        }
    }

    public static void main(String[] args) {
    }
}
