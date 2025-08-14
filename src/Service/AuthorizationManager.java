package Service;

import Model.User;
import javax.swing.JOptionPane;

/**
 * Single site-wide authorization component
 * Checks user permissions before any function execution
 */
public class AuthorizationManager {

    // Store the currently logged-in user
    private static User currentUser = null;

    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clear the current user (for logout)
     */
    public static void clearCurrentUser() {
        currentUser = null;
    }

    /**
     * Check if current user can delete users (Admin only)
     */
    public static boolean canDeleteUsers() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to delete users.");
            return false;
        }

        if (currentUser.getRole() < 5) { // Only Admin (role 5)
            showAccessDeniedMessage("You do not have the proper permissions for this action.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can edit user roles (Admin only)
     */
    public static boolean canEditUserRoles() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to edit user roles.");
            return false;
        }

        if (currentUser.getRole() < 5) { // Only Admin (role 5)
            showAccessDeniedMessage("You do not have the proper permissions for this action.");
            return false;
        }



        return true;
    }

    /**
     * Check if current user can change passwords
     */
    public static boolean canChangePasswords() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to change passwords.");
            return false;
        }
        return true;
    }

    /**
     * Check if current user can add products (Staff, Manager, Admin)
     */
    public static boolean canAddProducts() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to add products.");
            return false;
        }

        if (currentUser.getRole() != 3 || currentUser.getRole() != 4) { // Staff (3), Manager (4)
            showAccessDeniedMessage("You do not have permission to add products.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can edit products (Staff, Manager, Admin)
     */
    public static boolean canEditProducts() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to edit products.");
            return false;
        }

        if (currentUser.getRole() != 3 || currentUser.getRole() != 4) { // Staff (3), Manager (4)
            showAccessDeniedMessage("You do not have permission to edit products.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can delete products (Staff, Manager, Admin)
     */
    public static boolean canDeleteProducts() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to delete products.");
            return false;
        }

        if (currentUser.getRole() != 3 || currentUser.getRole() != 4) { // Staff (3), Manager (4)
            showAccessDeniedMessage("You do not have permission to delete products.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can purchase products (All users except disabled)
     */
    public static boolean canPurchaseProducts() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to purchase products.");
            return false;
        }

        if (currentUser.getRole() < 2) { // Client (2), Staff (3), Manager (4), Admin (5)
            showAccessDeniedMessage("Your account is disabled. Contact an administrator.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can view logs (Admin)
     */
    public static boolean canViewLogs() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to view logs.");
            return false;
        }

        if (currentUser.getRole() < 5) {
            showAccessDeniedMessage("You do not have permission to view logs.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can clear logs (Admin only)
     */
    public static boolean canClearLogs() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to clear logs.");
            return false;
        }

        if (currentUser.getRole() < 5) { // Only Admin (role 5)
            showAccessDeniedMessage("You do not have the proper permissions for this action.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can toggle debug mode (Admin only)
     */
    public static boolean canToggleDebugMode() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to toggle debug mode.");
            return false;
        }

        if (currentUser.getRole() < 5) { // Only Admin (role 5)
            showAccessDeniedMessage("You do not have the proper permissions for this action.");
            return false;
        }

        return true;
    }

    /**
     * Check if current user can view history (Staff, Manager, Admin)
     */
    public static boolean canViewHistory() {
        if (currentUser == null) {
            showAccessDeniedMessage("You must be logged in to view history.");
            return false;
        }

        if (currentUser.getRole() < 3) { // Staff (3), Manager (4), Admin (5)
            showAccessDeniedMessage("You do not have permission to view history.");
            return false;
        }

        return true;
    }

    /**
     * Show access denied message with consistent formatting
     */
    private static void showAccessDeniedMessage(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "ACCESS DENIED",
                JOptionPane.ERROR_MESSAGE);
    }
}