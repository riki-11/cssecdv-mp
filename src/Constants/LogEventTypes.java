package Constants;

public class LogEventTypes {
    // Authentication events
    public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    public static final String AUTH_FAILURE = "AUTH_FAILURE";
    public static final String AUTH_ACCOUNT_LOCKED = "AUTH_LOCKED";

    // Input validation events
    public static final String INPUT_VALIDATION_FAILURE = "INPUT_INVALID";

    // Access control events
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String ACCESS_GRANTED = "ACCESS_GRANTED";

    // General security events
    public static final String SECURITY_VIOLATION = "SECURITY_VIOLATION";
}