package View;

import Controller.Main;
import Model.User;
import Service.PasswordStrengthChecker;
import dto.PasswordCheckResult;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.WindowConstants;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class Frame extends javax.swing.JFrame {

    public Frame() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Container = new javax.swing.JPanel();
        HomePnl = new javax.swing.JPanel();
        Content = new javax.swing.JPanel();
        Navigation = new javax.swing.JPanel();
        adminBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        managerBtn = new javax.swing.JButton();
        staffBtn = new javax.swing.JButton();
        clientBtn = new javax.swing.JButton();
        changePassBtn = new javax.swing.JButton();
        logoutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));
        setMinimumSize(new java.awt.Dimension(800, 450));

        HomePnl.setBackground(new java.awt.Color(102, 102, 102));
        HomePnl.setPreferredSize(new java.awt.Dimension(801, 500));

        javax.swing.GroupLayout ContentLayout = new javax.swing.GroupLayout(Content);
        Content.setLayout(ContentLayout);
        ContentLayout.setHorizontalGroup(
                ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 544, Short.MAX_VALUE)
        );
        ContentLayout.setVerticalGroup(
                ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );

        Navigation.setBackground(new java.awt.Color(204, 204, 204));

        adminBtn.setBackground(new java.awt.Color(250, 250, 250));
        adminBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        adminBtn.setText("Admin Home");
        adminBtn.setFocusable(false);
        adminBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");
        jLabel1.setToolTipText("");

        managerBtn.setBackground(new java.awt.Color(250, 250, 250));
        managerBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        managerBtn.setText("Manager Home");
        managerBtn.setFocusable(false);
        managerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managerBtnActionPerformed(evt);
            }
        });

        staffBtn.setBackground(new java.awt.Color(250, 250, 250));
        staffBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        staffBtn.setText("Staff Home");
        staffBtn.setFocusable(false);
        staffBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staffBtnActionPerformed(evt);
            }
        });

        clientBtn.setBackground(new java.awt.Color(250, 250, 250));
        clientBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clientBtn.setText("Client Home");
        clientBtn.setFocusable(false);
        clientBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientBtnActionPerformed(evt);
            }
        });

        changePassBtn.setBackground(new java.awt.Color(250, 250, 250));
        changePassBtn.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        changePassBtn.setText("Change Password");
        changePassBtn.setFocusable(false);
        changePassBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePassBtnActionPerformed(evt);
            }
        });

        logoutBtn.setBackground(new java.awt.Color(250, 250, 250));
        logoutBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        logoutBtn.setText("LOGOUT");
        logoutBtn.setFocusable(false);
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NavigationLayout = new javax.swing.GroupLayout(Navigation);
        Navigation.setLayout(NavigationLayout);
        NavigationLayout.setHorizontalGroup(
                NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(NavigationLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(adminBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                                        .addComponent(managerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(staffBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(clientBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(changePassBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(logoutBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        NavigationLayout.setVerticalGroup(
                NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(NavigationLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(adminBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(managerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(staffBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clientBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changePassBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                                .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout HomePnlLayout = new javax.swing.GroupLayout(HomePnl);
        HomePnl.setLayout(HomePnlLayout);
        HomePnlLayout.setHorizontalGroup(
                HomePnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomePnlLayout.createSequentialGroup()
                                .addComponent(Navigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        HomePnlLayout.setVerticalGroup(
                HomePnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Navigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout ContainerLayout = new javax.swing.GroupLayout(Container);
        Container.setLayout(ContainerLayout);
        ContainerLayout.setHorizontalGroup(
                ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 980, Short.MAX_VALUE)
                        .addGroup(ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(HomePnl, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
        );
        ContainerLayout.setVerticalGroup(
                ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 500, Short.MAX_VALUE)
                        .addGroup(ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(HomePnl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

//    public void setCurrentUser(String username) {
//        this.currentLoggedInUser = username;
//    }

    private void adminBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminBtnActionPerformed
        adminHomePnl.showPnl("home");
        contentView.show(Content, "adminHomePnl");
    }//GEN-LAST:event_adminBtnActionPerformed

    private void managerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managerBtnActionPerformed
        managerHomePnl.showPnl("home");
        contentView.show(Content, "managerHomePnl");
    }//GEN-LAST:event_managerBtnActionPerformed

    private void staffBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staffBtnActionPerformed
        staffHomePnl.showPnl("home");
        contentView.show(Content, "staffHomePnl");
    }//GEN-LAST:event_staffBtnActionPerformed

    private void clientBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientBtnActionPerformed
        clientHomePnl.showPnl("home");
        contentView.show(Content, "clientHomePnl");
    }//GEN-LAST:event_clientBtnActionPerformed

    private void changePassBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePassBtnActionPerformed
        if (currentUsername == null || currentUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error: No user logged in.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPasswordField currentPassFld = new JPasswordField();
        JPasswordField newPassFld = new JPasswordField();
        JPasswordField confirmPassFld = new JPasswordField();

        designer(currentPassFld, "CURRENT PASSWORD");
        designer(newPassFld, "NEW PASSWORD");
        designer(confirmPassFld, "CONFIRM NEW PASSWORD");

        Object[] message = {
                "Change Password for: " + currentUsername,
                currentPassFld,
                newPassFld,
                confirmPassFld
        };

        int result = JOptionPane.showConfirmDialog(null, message, "CHANGE PASSWORD",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPassFld.getPassword());
            String newPassword = new String(newPassFld.getPassword());
            String confirmPassword = new String(confirmPassFld.getPassword());

            // Clear password fields for security
            currentPassFld.setText("");
            newPassFld.setText("");
            confirmPassFld.setText("");

            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify current password
//            User user = main.sqlite.getUserByCredentials(currentUsername, currentPassword);
//            if (user == null) {
//                JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }

            // Check if new passwords match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if new password is different from current
            if (currentPassword.equals(newPassword)) {
                JOptionPane.showMessageDialog(this, "New password must be different from current password.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate password strength
            PasswordCheckResult passwordResult = PasswordStrengthChecker.checkStrength(newPassword);
            if (!passwordResult.isValid) {
                JOptionPane.showMessageDialog(this, passwordResult.message, "Password Strength Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update password in database
            boolean success = main.sqlite.updateUserPassword(currentUsername, newPassword);
            if (success) {
                // Log the password change
                main.sqlite.addLogs("PASSWORD_CHANGE", currentUsername, "Password changed successfully",
                        new java.sql.Timestamp(new java.util.Date().getTime()).toString());

                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_changePassBtnActionPerformed

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        currentUsername = null;
        currentUserRole = 0;
        frameView.show(Container, "loginPnl");
    }//GEN-LAST:event_logoutBtnActionPerformed

    public Main main;
    public Login loginPnl = new Login();
    public Register registerPnl = new Register();

    private AdminHome adminHomePnl = new AdminHome();
    private ManagerHome managerHomePnl = new ManagerHome();
    private StaffHome staffHomePnl = new StaffHome();
    private ClientHome clientHomePnl = new ClientHome();

    private String currentUsername;
    private int currentUserRole;

    CardLayout contentView = new CardLayout();
    private CardLayout frameView = new CardLayout();

    // Helper method for styling text fields
    public void designer(JTextField component, String text){
        component.setSize(70, 600);
        component.setFont(new java.awt.Font("Tahoma", 0, 18));
        component.setBackground(new java.awt.Color(240, 240, 240));
        component.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        component.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), text, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12)));
    }

    public void init(Main controller) {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("CSSECDV - SECURITY Svcs");
        this.setLocationRelativeTo(null);

        this.main = controller;
        loginPnl.frame = this;
        registerPnl.frame = this;

//        adminHomePnl.init(main.sqlite);
//        clientHomePnl.init(main.sqlite);
//        managerHomePnl.init(main.sqlite);
//        staffHomePnl.init(main.sqlite);

        Container.setLayout(frameView);
        Container.add(loginPnl, "loginPnl");
        Container.add(registerPnl, "registerPnl");
        Container.add(HomePnl, "homePnl");
        frameView.show(Container, "loginPnl");

        Content.setLayout(contentView);
        Content.add(adminHomePnl, "adminHomePnl");
        Content.add(managerHomePnl, "managerHomePnl");
        Content.add(staffHomePnl, "staffHomePnl");
        Content.add(clientHomePnl, "clientHomePnl");

        this.setVisible(true);
    }

    public void mainNav(int role, String username) {
        setCurrentUser(username, role);

        // Re-initialize the home panels with the username
        adminHomePnl.init(main.sqlite, username);
        clientHomePnl.init(main.sqlite, username);
        managerHomePnl.init(main.sqlite, username);
        staffHomePnl.init(main.sqlite, username);

        frameView.show(Container, "homePnl");
        showPanel(role);
    }

    public void loginNav() {
        currentUsername = null;
        currentUserRole = 0;
        frameView.show(Container, "loginPnl");
    }

    public void registerNav() {
        frameView.show(Container, "registerPnl");
    }

    public boolean registerAction(String username, String password, String confpass) {
        return main.sqlite.addUser(username, password);
    }

    public void hideButtons(int role) {
        adminBtn.setVisible(false);
        managerBtn.setVisible(false);
        staffBtn.setVisible(false);
        clientBtn.setVisible(false);
        changePassBtn.setVisible(false);

        switch (role) {
            case 5:
                adminBtn.setVisible(true);
                changePassBtn.setVisible(true);
                break;
            case 4:
                managerBtn.setVisible(true);
                changePassBtn.setVisible(true);
                break;
            case 3:
                staffBtn.setVisible(true);
                changePassBtn.setVisible(true);
                break;
            case 2:
                clientBtn.setVisible(true);
                changePassBtn.setVisible(true);
                break;
        }
    }

    public void showPanel(int role) {
        // Show the appropriate panel based on role and navigate to it
        switch (role) {
            case 5:
                adminHomePnl.showPnl("home");
                contentView.show(Content, "adminHomePnl");
                break;
            case 4:
                managerHomePnl.showPnl("home");
                contentView.show(Content, "managerHomePnl");
                break;
            case 3:
                staffHomePnl.showPnl("home");
                contentView.show(Content, "staffHomePnl");
                break;
            default:
                clientHomePnl.showPnl("home");
                contentView.show(Content, "clientHomePnl");
                break;
        }
    }

    public void setCurrentUser(String username, int role) {
        this.currentUsername = username;
        this.currentUserRole = role;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public int getCurrentUserRole() {
        return currentUserRole;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Container;
    javax.swing.JPanel Content;
    private javax.swing.JPanel HomePnl;
    private javax.swing.JPanel Navigation;
    private javax.swing.JButton adminBtn;
    private javax.swing.JButton clientBtn;
    private javax.swing.JButton changePassBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JButton managerBtn;
    private javax.swing.JButton staffBtn;
    // End of variables declaration//GEN-END:variables
}