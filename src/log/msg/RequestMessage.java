package log.msg;

public class RequestMessage extends CommonMessage {
    public RequestMessage(String message) {
        super(message);
    }

    @Override
    protected String type() {
        return "request";
    }
}
