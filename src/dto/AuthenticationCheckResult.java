package dto;

import Model.User;

import java.time.LocalDateTime;

public class AuthenticationCheckResult {
    public final User user;
    public final boolean isLocked;
    public final LocalDateTime time;
    public final boolean invalidInput;
    public final LocalDateTime lastUsed;

    public AuthenticationCheckResult(User user, boolean isLocked, LocalDateTime time,  boolean invalidInput, LocalDateTime lastUsed) {
        this.user = user;
        this.isLocked = isLocked;
        this.time = time;
        this.invalidInput = invalidInput;
        this.lastUsed = lastUsed;
    }
}
