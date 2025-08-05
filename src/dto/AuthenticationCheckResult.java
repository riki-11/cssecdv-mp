package dto;

import Model.User;

import java.time.LocalDateTime;

public class AuthenticationCheckResult {
    public final User user;
    public final boolean isLocked;
    public final LocalDateTime time;
    public final boolean invalidInput;

    public AuthenticationCheckResult(User user, boolean isLocked, LocalDateTime time,  boolean invalidInput) {
        this.user = user;
        this.isLocked = isLocked;
        this.time = time;
        this.invalidInput = invalidInput;
    }
}
