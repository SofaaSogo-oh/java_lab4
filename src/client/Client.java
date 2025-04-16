package client;

import log.Logger;
import log.msg.RequestMessage;
import log.msg.ResponseMessage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Consumer;

public class Client {
    static String filename = "client.conf";
    static final int CLIENT_COUNT = 3;
    public final Integer port;
    public final String host;
    Logger log_;

    public Client(Logger logger, int sv_port, String sv_host) throws RuntimeException {
        port = sv_port;
        host = sv_host;
        var cin = new Scanner(System.in);
        cin.nextLine();
        log_ = logger;
    }

    public void run() {
        try (var socket = new Socket(host, port);
             var os = new PrintWriter(socket.getOutputStream(), true);
             var is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var cin = new Scanner(System.in)) {
            for (; ; ) {
                System.out.println("Enter equation (or 'exit'): ");
                String usr_inp = cin.nextLine();
                if (usr_inp == null || "exit".equalsIgnoreCase(usr_inp))
                    break;
                os.println(usr_inp);
                log_.leave_message(new RequestMessage(usr_inp));

                var res = new ResponseMessage(is.readLine());
                System.out.println(res.message());
                log_.leave_message(res);
            }
            System.out.print("Exiting client.");
        } catch (UnknownHostException err) {
            System.err.printf("Don't know about the host %s%n", host);
        }
        catch (IOException err) {
            err.printStackTrace(System.err);
        }
    }

    public static void main(String []args) {
        int port;
        String host;
        try (var fr = new FileReader(filename)) {
            var props = new Properties();
            props.load(fr);
            port = Integer.parseInt(props.getProperty("PORT"));
            host = props.getProperty("HOST");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (var logger = new Logger("client.conf")) {
            for (int i = 0; i < CLIENT_COUNT; ++i) {
                var client = new Client(logger, port, host);

            }
        } catch (Exception err) {
            err.printStackTrace(System.err);
        }
    }
}
