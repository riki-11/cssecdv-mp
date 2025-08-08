package View;

import Controller.SQLite;
import Model.User;
import Service.PasswordStrengthChecker;
import dto.PasswordCheckResult;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PasswordChange extends javax.swing.JPanel {

    public Frame frame;
    private SQLite sqlite;
    private String currentUsername; // To track user during reset process
    int resetStep = 1; // 1=verify identity, 2=answer questions, 3=new password

    public PasswordChange(SQLite sqlite, Frame frame) {
        this.sqlite = sqlite;
        this.frame = frame;
        initComponents();
        updateFormState();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        usernameFld = new javax.swing.JTextField();
        securityQuestion1Label = new javax.swing.JLabel();
        securityAnswer1Fld = new javax.swing.JTextField();
        securityQuestion2Label = new javax.swing.JLabel();
        securityAnswer2Fld = new javax.swing.JTextField();
        newPasswordFld = new javax.swing.JPasswordField();
        confirmPasswordFld = new javax.swing.JPasswordField();
        nextBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 36));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("PASSWORD RESET");

        usernameFld.setBackground(new java.awt.Color(240, 240, 240));
        usernameFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        usernameFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernameFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "USERNAME",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        // Security questions that don't have common answers
        securityQuestion1Label.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityQuestion1Label.setText("What was the name of your childhood best friend?");

        securityAnswer1Fld.setBackground(new java.awt.Color(240, 240, 240));
        securityAnswer1Fld.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityAnswer1Fld.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        securityQuestion2Label.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityQuestion2Label.setText("What was the model of your first car?");

        securityAnswer2Fld.setBackground(new java.awt.Color(240, 240, 240));
        securityAnswer2Fld.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityAnswer2Fld.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        newPasswordFld.setBackground(new java.awt.Color(240, 240, 240));
        newPasswordFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        newPasswordFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        newPasswordFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "NEW PASSWORD",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        confirmPasswordFld.setBackground(new java.awt.Color(240, 240, 240));
        confirmPasswordFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        confirmPasswordFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        confirmPasswordFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "CONFIRM PASSWORD",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        nextBtn.setFont(new java.awt.Font("Tahoma", 1, 18));
        nextBtn.setText("NEXT");
        nextBtn.addActionListener(this::nextBtnActionPerformed);

        backBtn.setFont(new java.awt.Font("Tahoma", 1, 18));
        backBtn.setText("BACK");
        backBtn.addActionListener(this::backBtnActionPerformed);

        cancelBtn.setFont(new java.awt.Font("Tahoma", 1, 18));
        cancelBtn.setText("CANCEL");
        cancelBtn.addActionListener(this::cancelBtnActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(150, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(usernameFld)
                                        .addComponent(securityQuestion1Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(securityAnswer1Fld)
                                        .addComponent(securityQuestion2Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(securityAnswer2Fld)
                                        .addComponent(newPasswordFld)
                                        .addComponent(confirmPasswordFld)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(150, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(50, Short.MAX_VALUE)
                                .addComponent(titleLabel)
                                .addGap(30, 30, 30)
                                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityQuestion1Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityAnswer1Fld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityQuestion2Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityAnswer2Fld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(newPasswordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(confirmPasswordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
    }

    void updateFormState() {
        // Show/hide components based on current step
        usernameFld.setVisible(resetStep == 1);
        securityQuestion1Label.setVisible(resetStep == 2);
        securityAnswer1Fld.setVisible(resetStep == 2);
        securityQuestion2Label.setVisible(resetStep == 2);
        securityAnswer2Fld.setVisible(resetStep == 2);
        newPasswordFld.setVisible(resetStep == 3);
        confirmPasswordFld.setVisible(resetStep == 3);

        backBtn.setVisible(resetStep > 1);

        if (resetStep == 1) {
            nextBtn.setText("VERIFY");
        } else if (resetStep == 2) {
            nextBtn.setText("ENTER");
        } else {
            nextBtn.setText("RESET");
        }
    }

    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {
        switch (resetStep) {
            case 1: // Verify username
                String username = usernameFld.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter your username", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get user including password change timestamp
                User user = sqlite.getUserByUsername(username);
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "Username not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if password was recently changed
                if (user.getLastPasswordUpdate() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime cooldownEnd = user.getLastPasswordUpdate().plusHours(24); // 24-hour cooldown

                    if (now.isBefore(cooldownEnd)) {
                        long hoursRemaining = ChronoUnit.HOURS.between(now, cooldownEnd);
                        JOptionPane.showMessageDialog(this,
                                "Password was recently changed. Please wait " + hoursRemaining + " more hours before changing again.",
                                "Cooldown Active",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                usernameFld.setText("");
                currentUsername = username;
                resetStep = 2;
                updateFormState();
                break;

            case 2: // Verify security questions
                String answerFriend = securityAnswer1Fld.getText().trim();
                String answerCar = securityAnswer2Fld.getText().trim();

                if (answerFriend.isEmpty() || answerCar.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please answer both security questions", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!sqlite.verifySecurityAnswers(currentUsername, answerFriend, answerCar)) {
                    JOptionPane.showMessageDialog(this, "Security answers don't match our records", "Error", JOptionPane.ERROR_MESSAGE);
                    securityAnswer1Fld.setText("");
                    securityAnswer2Fld.setText("");
                    return;
                }

                securityAnswer1Fld.setText("");
                securityAnswer2Fld.setText("");
                resetStep = 3;
                updateFormState();
                break;

            case 3: // Set new password
                String newPassword = new String(newPasswordFld.getPassword());
                String confirmPassword = new String(confirmPasswordFld.getPassword());

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter and confirm your new password", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PasswordCheckResult passwordResult = PasswordStrengthChecker.checkStrength(newPassword);

                if (!passwordResult.isValid) {

                    JOptionPane.showMessageDialog(this,
                            passwordResult.message,
                            "Reset Error",
                            JOptionPane.ERROR_MESSAGE);

                    newPasswordFld.setText("");
                    confirmPasswordFld.setText("");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                if (sqlite.updateUserPassword(currentUsername, newPassword)) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    frame.loginNav(); // Return to login screen
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
                }
                newPasswordFld.setText("");
                confirmPasswordFld.setText("");
                break;
        }
    }

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
        resetStep--;
        updateFormState();
    }

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {
        frame.loginNav();
    }

    // Variables declaration
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameFld;
    private javax.swing.JLabel securityQuestion1Label;
    private javax.swing.JTextField securityAnswer1Fld;
    private javax.swing.JLabel securityQuestion2Label;
    private javax.swing.JTextField securityAnswer2Fld;
    private javax.swing.JPasswordField newPasswordFld;
    private javax.swing.JPasswordField confirmPasswordFld;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton backBtn;
    private javax.swing.JButton cancelBtn;
}

