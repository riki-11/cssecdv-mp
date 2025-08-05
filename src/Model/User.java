package Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String password;
    private int role = 2;
    private int failedAttempts = 0;
    private LocalDateTime lockedUntil;

    public User(int id, String username, String password, int role, int failedAttempts, Timestamp lockedUntil){
        this.username = username;
        this.password = password;
        this.role = role;
        this.failedAttempts = failedAttempts;

        if(lockedUntil == null) {
            this.lockedUntil = null;
        } else {
            this.lockedUntil = lockedUntil.toLocalDateTime();
        }
    }
    
    public User(int id, String username, String password, int role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
}
