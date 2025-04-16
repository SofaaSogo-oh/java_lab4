package log.msg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonMessage implements LogMessage {
    String msg;
    protected Date date;
    private final SimpleDateFormat date_format = new SimpleDateFormat("hh:mm::ss zzz yyyy");

    public CommonMessage(String message) {
        msg = message;
        date = new Date();
    }

    protected String type() {
        return "common";
    }
    protected String date_format() {
        return date_format.format(date);
    }

    @Override
    public String what() {
        return String.format("[%s](%s): %s", type(), date_format(), message());
    }

    @Override
    public String message() {
        return msg;
    }
}
