package serv;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import log.Logger;
import log.msg.CommonMessage;
import log.msg.RequestMessage;
import log.msg.ResponseMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
                    js.onSnippetEvent(snip -> {
                        if (snip.status() == Snippet.Status.VALID) {
                            if (snip.value() != null && !snip.value().isEmpty()) {
                                os.println(snip.value());
                                logger.leave_message(new ResponseMessage(snip.value()));
                            } else {
                                os.println("OK");
                                logger.leave_message(new ResponseMessage("OK"));
                            }
                        } else {
                            var resp = String.format("Error: %s", snip.status());
                            os.println(resp);
                            logger.leave_message(new ResponseMessage(resp));
                        }
                    });

                    String expression;
                    while ((expression = is.readLine()) != null) {
                        logger.leave_message(new RequestMessage(expression));
                        var evs = js.eval(expression);
                        if (evs.isEmpty()) {
                            os.println("OK");
                            logger.leave_message(new ResponseMessage("OK"));
                        }
                    }
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
        var scanner = new Scanner(System.in);
        System.out.println("Enter log filename: ");
        var log_filename = scanner.nextLine();
        try (var lgr = new Logger(log_filename)) {
            var srv = new Server(lgr, Integer.parseInt(args[0]), "127.0.0.1");
            srv.go();
        } catch (Exception err) {
            err.printStackTrace(System.err);
        }
    }
}
