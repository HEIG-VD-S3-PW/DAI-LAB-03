package ch.heigvd.dai.protocol;

public class CommandResponse {

    private final CommandResponseCode code;
    private final String message;

    public CommandResponse(CommandResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code.getCode();
    }

    public String getMessage() {
        return message;
    }

}
