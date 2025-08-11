package dto;

public class PasswordUpdateResult {
    public final boolean isSuccesful;
    public final boolean isOldPassword;

    public PasswordUpdateResult(boolean isSuccesful, boolean isOldPassword) {
        this.isSuccesful = isSuccesful;
        this.isOldPassword = isOldPassword;
    }
}
