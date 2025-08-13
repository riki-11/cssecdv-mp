package View;

import Constants.LogEventTypes;
import Controller.SQLite;
import Model.User;
import Service.PasswordStrengthChecker;
import dto.PasswordCheckResult;

import javax.swing.*;

public class Register extends javax.swing.JPanel {

    public Frame frame;
    private SQLite sqLite = new SQLite();

    public Register() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        registerBtn = new javax.swing.JButton();
        passwordFld = new javax.swing.JPasswordField();
        usernameFld = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        confpassFld = new javax.swing.JPasswordField();
        backBtn = new javax.swing.JButton();

        // Add security question fields
        securityQuestion1Label = new javax.swing.JLabel();
        securityAnswer1Fld = new javax.swing.JTextField();
        securityQuestion2Label = new javax.swing.JLabel();
        securityAnswer2Fld = new javax.swing.JTextField();

        registerBtn.setFont(new java.awt.Font("Tahoma", 1, 24));
        registerBtn.setText("REGISTER");
        registerBtn.addActionListener(this::registerBtnActionPerformed);

        passwordFld.setBackground(new java.awt.Color(240, 240, 240));
        passwordFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        passwordFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        passwordFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "PASSWORD",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        usernameFld.setBackground(new java.awt.Color(240, 240, 240));
        usernameFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        usernameFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernameFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "USERNAME",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");
        jLabel1.setToolTipText("");

        confpassFld.setBackground(new java.awt.Color(240, 240, 240));
        confpassFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        confpassFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        confpassFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "CONFIRM PASSWORD",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        backBtn.setFont(new java.awt.Font("Tahoma", 1, 12));
        backBtn.setText("<Back");
        backBtn.addActionListener(this::backBtnActionPerformed);

        // Security question 1 setup
        securityQuestion1Label.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityQuestion1Label.setText("What was the name of your childhood best friend?");

        securityAnswer1Fld.setBackground(new java.awt.Color(240, 240, 240));
        securityAnswer1Fld.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityAnswer1Fld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "ANSWER",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        // Security question 2 setup
        securityQuestion2Label.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityQuestion2Label.setText("What was the model of your first car?");

        securityAnswer2Fld.setBackground(new java.awt.Color(240, 240, 240));
        securityAnswer2Fld.setFont(new java.awt.Font("Tahoma", 0, 14));
        securityAnswer2Fld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "ANSWER",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(150, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(usernameFld)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(passwordFld, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(confpassFld, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(securityQuestion1Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(securityAnswer1Fld, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(securityQuestion2Label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(securityAnswer2Fld, javax.swing.GroupLayout.Alignment.LEADING))
                                .addContainerGap(150, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(backBtn)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(backBtn)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(confpassFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(securityQuestion1Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityAnswer1Fld, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(securityQuestion2Label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(securityAnswer2Fld, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(30, Short.MAX_VALUE))
        );
    }

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {
        String username = usernameFld.getText().trim();
        String password = new String(passwordFld.getPassword());
        String confirmPassword = new String(confpassFld.getPassword());
        String securityAnswerFriend = securityAnswer1Fld.getText().trim();
        String securityAnswerCar = securityAnswer2Fld.getText().trim();

        // Validate security answers
        if (securityAnswerFriend.isEmpty() || securityAnswerCar.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please answer both security questions",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 1: Basic input validation with logging
        boolean basicValidationResult = sqLite.registerUserWithValidation(username, password, confirmPassword, securityAnswerFriend, securityAnswerCar);

        if (!basicValidationResult) {
            String msg = sqLite.getLastValidationMessage();
            final String mismatchMsg = "Registration failed - Password and confirm password do not match";

            if (mismatchMsg.equals(msg)) {
                JOptionPane.showMessageDialog(this, mismatchMsg, "Registration Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please check your input and try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }

            // Clear sensitive fields
            clearSensitiveFields();
            return;
        }

        // Step 2: Password strength validation
        PasswordCheckResult passwordResult = PasswordStrengthChecker.checkStrength(password);

        if (!passwordResult.isValid) {
            sqLite.addSecurityLog(LogEventTypes.INPUT_VALIDATION_FAILURE, username,
                    "Registration failed - Password strength validation: " + passwordResult.message);

            JOptionPane.showMessageDialog(this,
                    passwordResult.message,
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);

            clearSensitiveFields();
            return;
        }

        // Step 3: Complete the registration process with security questions
        boolean registrationResult = sqLite.completeUserRegistration(username, password, securityAnswerFriend, securityAnswerCar);

        if (registrationResult) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! You can now login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearSensitiveFields();
            frame.loginNav();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed due to a system error. Please try again.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            clearSensitiveFields();
        }
    }

    private void clearSensitiveFields() {
        passwordFld.setText("");
        confpassFld.setText("");
        securityAnswer1Fld.setText("");
        securityAnswer2Fld.setText("");
    }

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
        clearSensitiveFields();
        frame.loginNav();
    }

    // Variables declaration
    private javax.swing.JButton backBtn;
    private javax.swing.JPasswordField confpassFld;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPasswordField passwordFld;
    private javax.swing.JButton registerBtn;
    private javax.swing.JTextField usernameFld;
    private javax.swing.JLabel securityQuestion1Label;
    private javax.swing.JTextField securityAnswer1Fld;
    private javax.swing.JLabel securityQuestion2Label;
    private javax.swing.JTextField securityAnswer2Fld;
}