package Service;

import dto.PasswordCheckResult;

public class PasswordStrengthChecker {

    public static PasswordCheckResult checkStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordCheckResult(false, "Password cannot be empty.");
        }

        if (password.length() < 12) {
            return new PasswordCheckResult(false, "Password must be at least 12 characters long.");
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true; // any non-alphanumeric character
        }

        if (!hasUpper) return new PasswordCheckResult(false, "Password must contain at least one uppercase letter.");
        if (!hasLower) return new PasswordCheckResult(false, "Password must contain at least one lowercase letter.");
        if (!hasDigit) return new PasswordCheckResult(false, "Password must contain at least one digit.");
        if (!hasSpecial) return new PasswordCheckResult(false, "Password must contain at least one special character.");

        return new PasswordCheckResult(true, "Password is strong.");
    }
}