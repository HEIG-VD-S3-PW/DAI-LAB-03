package ch.heigvd.dai.protocol;

public enum CommandResponseCode {
    OK(200),
    ERROR(500),
    NOT_FOUND(404),
    FORBIDDEN(403),
    UNAUTHORIZED(401);

    private final int code;

    CommandResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CommandResponseCode fromCode(int code) {
        for (CommandResponseCode c : CommandResponseCode.values()) {
            if (c.code == code) {
                return c;
            }
        }
        return null;
    }
}