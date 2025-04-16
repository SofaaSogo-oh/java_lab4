package log;

import log.msg.LogMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Logger implements AutoCloseable {
    PrintWriter pw_;
    public Logger(String filename) throws FileNotFoundException {
        pw_ = new PrintWriter(filename);
    }
    public Logger(PrintWriter pw) {
        pw_ = pw;
    }
    public void leave_message(LogMessage msg) {
        pw_.println(msg.what());
    }

    @Override
    public void close() throws Exception {
        pw_.close();
    }
}
