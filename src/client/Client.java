package client;

import log.Logger;
import log.msg.RequestMessage;
import log.msg.ResponseMessage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Scanner;

public class Client {
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
        try (var fr = new FileReader("client.conf")) {
            var props = new Properties();
            props.load(fr);
            var port = Integer.parseInt(props.getProperty("PORT"));
            var host = props.getProperty("HOST");
            var scanner = new Scanner(System.in);
            System.out.println("Enter the log filename: ");
            var log_filename = scanner.nextLine();
            try (var logger = new Logger(log_filename)) {
                var client = new Client(logger, port, host);
                client.run();
            } catch (Exception err) {
                err.printStackTrace(System.err);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
