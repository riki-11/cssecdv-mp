
package View;

import Controller.SQLite;
import Model.User;
import dto.AuthenticationCheckResult;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends javax.swing.JPanel {

    public Frame frame;
    
    public Login() {
        initComponents();
    }
    public SQLite sqlite = new SQLite();

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new JLabel();
        usernameFld = new JTextField();
        passwordFld = new JPasswordField();
        registerBtn = new JButton();
        loginBtn = new JButton();

        jLabel1.setFont(new Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");
        jLabel1.setToolTipText("");

        usernameFld.setBackground(new Color(240, 240, 240));
        usernameFld.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        usernameFld.setHorizontalAlignment(JTextField.CENTER);
        usernameFld.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 0), 2, true), "USERNAME", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 12))); // NOI18N

        passwordFld.setBackground(new Color(240, 240, 240));
        passwordFld.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        passwordFld.setHorizontalAlignment(JTextField.CENTER);
        passwordFld.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 0), 2, true), "PASSWORD", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 12))); // NOI18N

        registerBtn.setFont(new Font("Tahoma", 1, 24)); // NOI18N
        registerBtn.setText("REGISTER");
        registerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });

        loginBtn.setFont(new Font("Tahoma", 1, 24)); // NOI18N
        loginBtn.setText("LOGIN");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                loginBtnActionPerformed(evt);
            }
        });

        // Add this block right after loginBtn setup
        changePasswordBtn = new JButton();
        changePasswordBtn.setFont(new Font("Tahoma", 0, 12)); // Smaller font
        changePasswordBtn.setText("Change your Password");
        changePasswordBtn.setBorderPainted(false);
        changePasswordBtn.setContentAreaFilled(false);
        changePasswordBtn.setFocusPainted(false);
        changePasswordBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                frame.passwordResetNav();
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(200, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(registerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(usernameFld)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(passwordFld, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(changePasswordBtn)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap(200, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(88, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changePasswordBtn)
                                .addContainerGap(100, Short.MAX_VALUE))  // Reduced from 126 to 100 to accommodate the new button
        );
    }// </editor-fold>//GEN-END:initComponents
    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {
        String username = usernameFld.getText();
        String password = new String(passwordFld.getPassword());

        // Use the enhanced authentication method with logging
        AuthenticationCheckResult result = sqlite.authenticateUser(username, password);
        User user = result.user;

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
            int role = user.getRole();

//            frame.setCurrentUser(username);

            frame.hideButtons(role);
            frame.mainNav(role, username, result.lastUsed);
        } else if(result.invalidInput) {
            JOptionPane.showMessageDialog(this, "Ensure proper input (No empty fields, too many characters, or suspicious sql input)", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
        else if(result.isLocked) {
            JOptionPane.showMessageDialog(this, "Too many failed login attempts. Account is locked until " + result.time, "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(this, "Invalid username and/or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }

        // Clear fields for security
        passwordFld.setText("");
        usernameFld.setText("");
    }

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerBtnActionPerformed
        frame.registerNav();
    }//GEN-LAST:event_registerBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton loginBtn;
    private javax.swing.JPasswordField passwordFld;
    private javax.swing.JButton registerBtn;
    private javax.swing.JTextField usernameFld;
    private javax.swing.JButton changePasswordBtn;  // Add this line
    // End of variables declaration//GEN-END:variables
}
