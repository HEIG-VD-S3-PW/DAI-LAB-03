package ch.heigvd.dai.protocol;

public class CommandResponse {

    private final int code;
    private final String message;

    public CommandResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
