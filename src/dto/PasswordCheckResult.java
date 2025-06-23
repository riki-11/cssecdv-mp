package dto;

public class PasswordCheckResult {
    public final boolean isValid;
    public final String message;

    public PasswordCheckResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }
}