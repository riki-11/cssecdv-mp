package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import Constants.LogEventTypes;
import dto.AuthenticationCheckResult;
import dto.PasswordUpdateResult;

public class SQLite {
    
    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";

    // Password hashing configuration
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private String lastValidationMessage = "";

    // Generate  random salt
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public String getLastValidationMessage() {
        return lastValidationMessage;
    }


    // Hash a password using PBKDF2
    private String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Combine salt and hash, then encode to Base64
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify a password against its hash
    private boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extract salt and hash
            byte[] salt = new byte[16];
            byte[] hash = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, hash, 0, hash.length);

            // Hash the provided password with the extracted salt
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Compare the hashes
            return slowEquals(hash, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime incrementUserFailedAttempts(String username, Connection conn) {
        String updateSql = "UPDATE users set failedAttempts = failedAttempts + 1 WHERE username = ?";
        String selectSql = "SELECT failedAttempts from users WHERE username = ?";
        String lockSql = "UPDATE users set lockedUntil = ?, failedAttempts = 0 WHERE username = ?";

        try {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = WAL");
            }

            try(PreparedStatement update = conn.prepareStatement(updateSql)) {
                update.setString(1, username);
                update.executeUpdate();
            }

            int failedAttempts = 0;

            try(PreparedStatement select = conn.prepareStatement(selectSql)){
                select.setString(1, username);
                try(ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        failedAttempts = rs.getInt("failedAttempts");
                    }
                }
            }

            if(failedAttempts >= 5) {
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(15);
                try (PreparedStatement lockStmt = conn.prepareStatement(lockSql)) {
                    lockStmt.setTimestamp(1, Timestamp.valueOf(lockUntil));
                    lockStmt.setString(2, username);
                    lockStmt.executeUpdate();
                }
                return lockUntil;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void resetUserFailedAttemptsAndLockout(String username, Connection conn) {
        String sql = "UPDATE users set failedAttempts = 0, lockedUntil = NULL WHERE username = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PasswordUpdateResult updateUserPassword(String username, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        String sql = "UPDATE users SET password = ?, lastPasswordUpdate = ? WHERE username = ?";

        LocalDateTime lastUpdated = LocalDateTime.now();
        Timestamp cooldownTimestamp = Timestamp.valueOf(lastUpdated);

        if (isPreviousPassword(username, newPassword)) {
            System.err.println("Password matches a previously used password");
            return new PasswordUpdateResult(false, true);
        }

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hashedPassword);
            pstmt.setTimestamp(2, cooldownTimestamp);
            pstmt.setString(3, username);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Password updated successfully for user: " + username);

                String historySql = "INSERT INTO password_history (user_id, password_hash, changed_at) "
                        + "SELECT id, ?, ? FROM users WHERE username = ?";

                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                try (PreparedStatement historyStmt = conn.prepareStatement(historySql)) {
                    historyStmt.setString(1, hashedPassword);
                    historyStmt.setTimestamp(2, now);
                    historyStmt.setString(3, username);
                    historyStmt.executeUpdate();
                }


                return new PasswordUpdateResult(true, false);
            } else {
                System.out.println("No user found with username: " + username);
                return new PasswordUpdateResult(false, false);
            }

        } catch (Exception ex) {
            System.err.println("Error updating password: " + ex.getMessage());
            return new PasswordUpdateResult(false, false);
        }
    }

    private boolean isPreviousPassword(String username, String newPassword)  {
        String sql = "SELECT ph.password_hash FROM password_history ph "
                + "JOIN users u ON ph.user_id = u.id "
                + "WHERE u.username = ? "
                + "ORDER BY ph.changed_at DESC "
                + "LIMIT 5";  // Check against last 5 passwords

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if (verifyPassword(newPassword, rs.getString("password_hash"))) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true; //fail secure and assume password exists
        }
    }

    // Constant-time comparison to prevent timing attacks
    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    public void testDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            System.err.println("Please add sqlite-jdbc jar to your classpath");
        }
    }
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void createPasswordTable() {
        String createPasswordHistoryTable = "CREATE TABLE IF NOT EXISTS password_history (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " user_id INTEGER NOT NULL,\n"
                + " password_hash TEXT NOT NULL,\n"
                + " changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,\n"
                + " FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,\n"
                + " CONSTRAINT unique_user_password UNIQUE (user_id, password_hash)\n"
                + ");";

        String createIndex = "CREATE INDEX IF NOT EXISTS idx_password_history_user "
                + "ON password_history(user_id);";

        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement()) {

            // Execute all table creation statements
            stmt.execute(createPasswordHistoryTable);
            stmt.execute(createIndex);

            System.out.println("Tables password_history created successfully.");

        } catch (Exception ex) {
            System.err.println("Error creating tables: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
     
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL UNIQUE,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " failedAttempts INTEGER DEFAULT 0,\n"
            + " lockedUntil DATETIME DEFAULT NULL,\n"
            + " lastUsed DATETIME DEFAULT NULL,\n"
            + " hashedAnswerFriend TEXT NOT NULL,\n"
            + " hashedAnswerCar TEXT NOT NULL,\n"
            + " lastPasswordUpdate DATETIME DEFAULT NULL"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void dropPasswordTable() {
        String sql = "DROP TABLE IF EXISTS password_history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table password_history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void addHistory(String username, String name, int stock, double price, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,price,timestamp) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setInt(3, stock);
            pstmt.setDouble(4, price);
            pstmt.setString(5, timestamp);
            pstmt.executeUpdate();

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    // Also update the existing addProduct method to use PreparedStatement for security
    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name, stock, price) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);

            pstmt.executeUpdate();
            System.out.println("Product added successfully: " + name);

        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE constraint failed")) {
                throw new RuntimeException("Product with name '" + name + "' already exists");
            }
            System.err.println("Error adding product: " + ex.getMessage());
            throw new RuntimeException("Failed to add product", ex);
        }
    }

    public void updateProduct(String oldName, String newName, int stock, double price) {
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);
            pstmt.setString(4, oldName);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No product found with name: " + oldName);
            }

            System.out.println("Product updated successfully: " + oldName + " -> " + newName);

        } catch (SQLException ex) {
            System.err.println("Error updating product: " + ex.getMessage());
            throw new RuntimeException("Failed to update product", ex);
        }
    }

    public void deleteProduct(String name) {
        String sql = "DELETE FROM product WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No product found with name: " + name);
            }

            System.out.println("Product deleted successfully: " + name);

        } catch (SQLException ex) {
            System.err.println("Error deleting product: " + ex.getMessage());
            throw new RuntimeException("Failed to delete product", ex);
        }
    }

    public boolean addUser(String username, String password, String friend, String car) {
        String hashedPassword = hashPassword(password);
        String hashedFriend = hashPassword(friend);
        String hashedCar = hashPassword(car);
        String sql = "INSERT INTO users(username,password,hashedAnswerFriend,hashedAnswerCar) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, hashedFriend);
            pstmt.setString(4, hashedCar);
            pstmt.executeUpdate();

            System.out.println("User '" + username + "' added successfully.");
            ResultSet rs = pstmt.getGeneratedKeys();
            int userId = rs.getInt(1);

            String historySql = "INSERT INTO password_history(user_id, password_hash) VALUES(?, ?)";
            try (PreparedStatement historyStmt = conn.prepareStatement(historySql)) {
                historyStmt.setInt(1, userId);
                historyStmt.setString(2, hashedPassword);
                historyStmt.executeUpdate();
            }

            System.out.println("User '" + username + "' added successfully with password history.");

            return true;

        } catch (Exception ex) {
            System.err.println("Error adding user: " + ex.getMessage());
            return false;
        }
    }

    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, price, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getDouble("price"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return products;
    }

    public Product getProduct(String name){
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            product = new Product(rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getFloat("price"));
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return product;
    }

    public void processPurchase(String productName, int quantity, double price, String username) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(driverURL);
            conn.setAutoCommit(false); // Start transaction

            // First, get current product info
            Product product = getProduct(productName);
            if (product == null) {
                throw new SQLException("Product not found: " + productName);
            }

            // Check if sufficient stock
            if (product.getStock() < quantity) {
                throw new SQLException("Insufficient stock. Available: " + product.getStock() + ", Requested: " + quantity);
            }

            // Calculate new stock
            int newStock = product.getStock() - quantity;

            // Add to purchase history
            addHistory(username, productName, quantity, price, new Timestamp(new Date().getTime()).toString());

            // Update product stock
            updateProductStock(productName, newStock);
            System.out.println("Updated stock for '" + productName + "': " + newStock + " remaining");

            conn.commit(); // Commit transaction
            System.out.println("Purchase processed successfully: " + price + "x " + quantity + " of " + productName + " by " + username);

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error processing purchase: " + ex.getMessage());
            throw new RuntimeException("Purchase failed: " + ex.getMessage(), ex);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error closing connection: " + ex.getMessage());
            }
        }
    }

    public void updateProductStock(String name, int newStock) {
        String sql = "UPDATE product SET stock = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newStock);
            pstmt.setString(2, name);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No product found with name: " + name);
            }

        } catch (SQLException ex) {
            System.err.println("Error updating product stock: " + ex.getMessage());
            throw new RuntimeException("Failed to update stock", ex);
        }
    }

    //TODO : update to match user fields
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, failedAttempts, lockedUntil, lastUsed, hashedAnswerFriend, hashedAnswerCar, lastPasswordUpdate FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("failedAttempts"),
                                   rs.getTimestamp("lockedUntil"),
                                   rs.getTimestamp("lastUsed"),
                                   rs.getString("hashedAnswerCar"),
                                   rs.getString("hashedAnswerFriend"),
                                   rs.getTimestamp("lastPasswordUpdate")));
            }
        } catch (Exception ex) {}
        return users;
    }
    
    public void addUser(String username, String password, int role, String friend, String car) {
        String hashedPassword = hashPassword(password);
        String hashedFriend = hashPassword(friend);
        String hashedCar = hashPassword(car);
        String sql = "INSERT INTO users(username,password,role,hashedAnswerFriend,hashedAnswerCar) VALUES(?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setInt(3, role);
            pstmt.setString(4, hashedFriend);
            pstmt.setString(5, hashedCar);
            pstmt.executeUpdate();

            System.out.println("User '" + username + "' added successfully.");
            ResultSet rs = pstmt.getGeneratedKeys();
            int userId = rs.getInt(1);

            String historySql = "INSERT INTO password_history(user_id, password_hash) VALUES(?, ?)";
            try (PreparedStatement historyStmt = conn.prepareStatement(historySql)) {
                historyStmt.setInt(1, userId);
                historyStmt.setString(2, hashedPassword);
                historyStmt.executeUpdate();
            }

            System.out.println("User '" + username + "' added successfully with password history.");


            
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username='" + username + "';";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been deleted.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    //TODO ensure follows fields of user
    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, password, role, failedAttempts, lockedUntil, lastUsed, hashedAnswerFriend, hashedAnswerCar, lastPasswordUpdate FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("role"),
                        rs.getInt("failedAttempts"),
                        rs.getTimestamp("lockedUntil"),
                        rs.getTimestamp("lastUsed"),
                        rs.getString("hashedAnswerFriend"),
                        rs.getString("hashedAnswerCar"),
                        rs.getTimestamp("lastPasswordUpdate"));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    // Add this method to your SQLite class for secure logging with PreparedStatement
    // Overloaded version of addSecurityLog that uses an existing connection
    public void addSecurityLog(String event, String username, String description, Connection conn) {
        String sql = "INSERT INTO logs(event, username, desc, timestamp) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event);
            pstmt.setString(2, username != null ? username : "UNKNOWN");
            pstmt.setString(3, description);
            pstmt.setString(4, new Timestamp(new Date().getTime()).toString());

            pstmt.executeUpdate();

            if (DEBUG_MODE == 1) {
                System.out.println("Security Log: " + event + " - " + username + " - " + description);
            }
        } catch (SQLException ex) {
            System.err.println("Critical: Failed to write security log (shared conn): " + ex.getMessage());
        }
    }
    // fallback for when you don't have access to a shared connection
    public void addSecurityLog(String event, String username, String description) {
        String sql = "INSERT INTO logs(event, username, desc, timestamp) VALUES(?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event);
            pstmt.setString(2, username != null ? username : "UNKNOWN");
            pstmt.setString(3, description);
            pstmt.setString(4, new Timestamp(new Date().getTime()).toString());

            pstmt.executeUpdate();

            if (DEBUG_MODE == 1) {
                System.out.println("Security Log: " + event + " - " + username + " - " + description);
            }
        } catch (SQLException ex) {
            System.err.println("Critical: Failed to write fallback security log: " + ex.getMessage());
        }
    }

    // Enhanced authentication method with logging
    public AuthenticationCheckResult authenticateUser(String username, String password) {
        //TODO: ensure follows fields of user
        String sql = "SELECT id, username, password, role, failedAttempts, lockedUntil, lastUsed, hashedAnswerFriend, hashedAnswerCar, lastPasswordUpdate FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Log authentication attempt
            addSecurityLog(LogEventTypes.AUTH_SUCCESS, username, "Authentication attempt initiated", conn);

            // Input validation
            if (username == null || username.trim().isEmpty()) {
                addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, "UNKNOWN", "Username is empty", conn);
                return new AuthenticationCheckResult(null, false, null, true, null);
            }

            if (password == null || password.isEmpty()) {
                addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username, "Password is empty", conn);
                return new AuthenticationCheckResult(null, false, null, true, null);
            }

            if (username.length() > 50) {
                addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username, "Username too long", conn);
                return new AuthenticationCheckResult(null, false, null, true, null);
            }

            if (password.length() > 200) {
                addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username, "Password too long", conn);
                return new AuthenticationCheckResult(null, false, null, true,null);
            }

            // Basic SQL injection pattern check
            String[] sqlPatterns = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE"};
            for (String pattern : sqlPatterns) {
                if (username.toUpperCase().contains(pattern.toUpperCase())) {
                    addSecurityLog(LogEventTypes.SECURITY_VIOLATION, username, "Suspicious input: " + pattern, conn);
                    return new AuthenticationCheckResult(null, false, null, true, null);
                }
            }

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp lastUsed = rs.getTimestamp("lastUsed");

                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE users SET lastUsed = ? WHERE username = ?")) {
                    updateStmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                }

                if (rs.getTimestamp("lockedUntil") != null && rs.getTimestamp("lockedUntil").toLocalDateTime().isAfter(LocalDateTime.now())) {
                    addSecurityLog(LogEventTypes.AUTH_ACCOUNT_LOCKED, username, "Account is locked", conn);
                    return new AuthenticationCheckResult(null, true, rs.getTimestamp("lockedUntil").toLocalDateTime(), false, null);

                }

                String storedHashedPassword = rs.getString("password");

                if (verifyPassword(password, storedHashedPassword)) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            storedHashedPassword,
                            rs.getInt("role"),
                            rs.getInt("failedAttempts"),
                            rs.getTimestamp("lockedUntil"),
                            lastUsed,
                            rs.getString("hashedAnswerFriend"),
                            rs.getString("hashedAnswerCar"),
                            rs.getTimestamp("lastPasswordUpdate")
                    );

                    addSecurityLog(LogEventTypes.AUTH_SUCCESS, username, "Login success - Role: " + user.getRole(), conn);
                    resetUserFailedAttemptsAndLockout(username, conn);
                    LocalDateTime temp = null;
                    if (lastUsed != null) {
                        temp = lastUsed.toLocalDateTime();
                    }
                    return new AuthenticationCheckResult(user, false, null, false, temp);
                } else {
                    addSecurityLog(LogEventTypes.AUTH_FAILURE, username, "Invalid password", conn);
                    LocalDateTime locked = incrementUserFailedAttempts(username, conn);

                    boolean flag = locked != null;

                    return new AuthenticationCheckResult(null, flag, locked, false, null);
                }
            } else {
                addSecurityLog(LogEventTypes.AUTH_FAILURE, username, "Username not found", conn);
                return new AuthenticationCheckResult(null, false, null, true, null);
            }

        } catch (Exception ex) {
            System.err.println("Database error during authentication: " + ex.getMessage());
            // Use fallback logging method (outside of connection)
            addSecurityLog(LogEventTypes.AUTH_FAILURE, username, "DB error: " + ex.getMessage());
            return null;
        }
    }


    // Method to validate and log input validation failures
    public boolean validateUserInput(String fieldName, String value, String username, int maxLength, boolean allowEmpty) {
        if (value == null) {
            addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                    "Input validation failed - " + fieldName + " is null");
            return false;
        }

        if (!allowEmpty && value.trim().isEmpty()) {
            addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                    "Input validation failed - " + fieldName + " is empty");
            return false;
        }

        if (value.length() > maxLength) {
            addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                    "Input validation failed - " + fieldName + " exceeds maximum length (" + maxLength + ")");
            return false;
        }

        // Check for potential XSS/injection patterns
        String[] dangerousPatterns = {"<script", "</script", "javascript:", "onload=", "onerror=",
                "'", "\"", ";", "--", "/*", "*/"};
        for (String pattern : dangerousPatterns) {
            if (value.toLowerCase().contains(pattern.toLowerCase())) {
                addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                        "Input validation failed - " + fieldName + " contains suspicious pattern: " + pattern);
                return false;
            }
        }

        return true;
    }

    // Method to check and log access control violations
    public boolean checkUserAccess(String username, int userRole, String requestedAction, int requiredRole) {
        if (userRole >= requiredRole) {
            addSecurityLog(LogEventTypes.ACCESS_GRANTED, username,
                    "Access granted - Action: " + requestedAction + " (User role: " + userRole + ", Required: " + requiredRole + ")");
            return true;
        } else {
            addSecurityLog(LogEventTypes.ACCESS_DENIED, username,
                    "Access denied - Action: " + requestedAction + " (User role: " + userRole + ", Required: " + requiredRole + ")");
            return false;
        }
    }

    // Enhanced user registration with input validation logging
    public boolean registerUserWithValidation(String username, String password, String confirmPassword, String securityAnswerFriend, String securityAnswerCar) {
        String currentUser = "REGISTRATION_SYSTEM";

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;  // Don't set lastValidationMessage → triggers generic UI error
        }

        // Validate username
        if (!validateUserInput("username", username, currentUser, 50, false)) {
            return false;
        }

        // Validate password basic format
        if (!validateUserInput("password", password, username, 200, false)) {
            return false;
        }

        // Validate confirm password
        if (!validateUserInput("confirmPassword", confirmPassword, username, 200, false)) {
            return false;
        }

        // Validate confirm password
        if (!validateUserInput("securityAnswerFriend", securityAnswerFriend, username, 200, false)) {
            return false;
        }

        // Validate confirm password
        if (!validateUserInput("securityAnswerCar", securityAnswerCar, username, 200, false)) {
            return false;
        }



        if (!password.equals(confirmPassword)) {
            String msg = "Registration failed - Password and confirm password do not match";
            addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username, msg);
            lastValidationMessage = msg;
            return false;
        }

        if (getUserByUsername(username) != null) {
            addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                    "Registration failed - Username already exists");
            return false;
        }

        // All basic validations passed - return true to continue with password strength check in UI
        return true;
    }

    // Method to complete registration after password strength validation
    public boolean completeUserRegistration(String username, String password, String securityAnswerFriend, String securityAnswerCar) {
        boolean result = addUser(username, password, securityAnswerFriend, securityAnswerCar);
        if (result) {
            addSecurityLog(LogEventTypes.AUTH_SUCCESS, username,
                    "User registration completed successfully");
        } else {
            addSecurityLog(LogEventTypes.AUTH_FAILURE, username,
                    "User registration failed - Database error");
        }
        return result;
    }

    //TODO idk if u wanna add extra security logs pa
    public boolean verifySecurityAnswers(String username, String answerFriend, String answerCar) {
        String sql = "SELECT hashedAnswerFriend, hashedAnswerCar FROM users WHERE username = ?";
        ResultSet resultSet = null;

        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Log authentication attempt
            addSecurityLog(LogEventTypes.AUTH_SUCCESS, username, "Authentication attempt initiated", conn);


            // Basic SQL injection pattern check
            String[] sqlPatterns = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE"};
            for (String pattern : sqlPatterns) {
                if (answerFriend.toUpperCase().contains(pattern.toUpperCase()) || answerCar.toUpperCase().contains(pattern.toUpperCase())) {
                    addSecurityLog(LogEventTypes.SECURITY_VIOLATION, username, "Suspicious input: " + pattern, conn);
                    return false;
                }
            }

            pstmt.setString(1, username);
            resultSet = pstmt.executeQuery();

            if(resultSet.next()) {
                String storedFriend = resultSet.getString("hashedAnswerFriend");
                String storedCar = resultSet.getString("hashedAnswerCar");

                return verifyPassword(answerFriend, storedFriend) && verifyPassword(answerCar, storedCar);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}