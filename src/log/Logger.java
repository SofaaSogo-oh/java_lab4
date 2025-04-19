package log;

import log.msg.LogMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger implements AutoCloseable {
    PrintWriter pw_;
    public Logger(String filename) throws FileNotFoundException {
        pw_ = new PrintWriter(filename);
    }
    public void leave_message(LogMessage msg) {
        System.out.println(msg.what());
        pw_.println(msg.what());
        pw_.flush();
    }

    @Override
    public void close() {
        pw_.close();
    }
}
