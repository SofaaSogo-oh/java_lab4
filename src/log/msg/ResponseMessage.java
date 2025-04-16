package log.msg;

public class ResponseMessage extends CommonMessage {
    public ResponseMessage(String message) {
        super(message);
    }

    @Override
    protected String type() {
        return "response";
    }
}
