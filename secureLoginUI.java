/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lijennifersecurelogin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.EOFException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * website Register/login simulation
 *
 * @author jenniferli
 */
public class secureLoginUI extends javax.swing.JFrame {

    //file for user names, emails and passwords
    File file = new File("users.txt");

    //stores usernames
    ArrayList<String> userNames = new ArrayList<String>();
    //stores emails
    ArrayList<String> emails = new ArrayList<String>();
    //stores encrypted passwords
    ArrayList<String> passwords = new ArrayList<String>();
    //String for register label
    String edit = "";

    /**
     * Checks to make sure registrations/revisions to user data is within proper
     * conditions
     *
     * @param userName
     * @param email
     * @param password
     * @return false, if no username was empty or email doesn't contain "@"and
     * "." or password used to register is the same as a password from the
     * badPassword file
     */
    private boolean checkForRegisterErrors(String userName, String email, String password) {
        //checks to make sure that the username is not empty
        if (userName.isEmpty()) {
            //asks the user for the username again
            edit = "Please input a username";
            //clears the field so the user can enter text again
            newUserEntry.setText("");
            //exit the method
            return false;
        } //checks to make sure that the email contains an @ and a . and is not empty
        else if (!email.contains("@") || !email.contains(".") || email.isEmpty()) {
            //asks the user to input an email again
            edit = "please input a valid email";
            //clears the field
            newEmailEntry.setText("");
            //exit the method
            return false;
        }
        //load the list of bad passwords
        File badPasswords = new File("badPasswords.txt");

        try {
            //reads the bad password file
            Scanner badPassScan = new Scanner(badPasswords);

            //repeats while there's still more lines to read
            while (badPassScan.hasNextLine()) {
                //gets a line in the bad password file
                String line = badPassScan.nextLine();
                //if the password inputted is the same as a password from the bad password file do the following
                if (line.contains(password) || password.isEmpty()) {
                    //ask the user for a better password
                    edit = "please choose a stronger password";
                    //clear the password text field
                    newPasswordEntry.setText("");
                    //exit the method
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bad passwords file not found");
        }
        //if all the data matches the conditions, exit the method wihtout changes
        return true;
    }

    /**
     * writes all the registered user data into the users file
     */
    private void fileWrite() {
        //create a stringBuilder 
        StringBuilder fileOutput = new StringBuilder();
        //repeats while there's still registered users not added to the output string
        for (int i = 0; i < userNames.size(); i++) {
            //adds the username with a delimiter(semicolon)
            fileOutput.append(userNames.get(i));
            fileOutput.append(";");
            //adds the email with a delimiter(semicolon)
            fileOutput.append(emails.get(i));
            fileOutput.append(";");
            //adds the encrypted password with a new line
            fileOutput.append(passwords.get(i));
            fileOutput.append("\n");
        }

        try {
            //FileWriter writes to the file
            FileWriter appendtoFile = new FileWriter(file);
            //write all the registered user info to the file
            appendtoFile.write(fileOutput.toString());
            appendtoFile.close();
        } catch (IOException ex) {

        }

    }

    /**
     * Resets the password of an already registered user. Called when the reset
     * password button is pressed. checks to make sure new password meets
     * requirements
     *
     * @param name
     * @param email
     * @param password
     */
    private void resetPassword(String name, String email, String password) {
        //index of registered user that wants to change password
        int indexUser = 0;
        //checks to make sure the user is registered already
        if (userNames.contains(name)) {
            indexUser = userNames.indexOf(name);
            //checks to make sure the email matches what the user registered with
            if (indexUser == emails.indexOf(email)) {
                //if the email matches the username change the password arrayList to what was inputted
                passwords.set(indexUser, password);
                //update the file with the new password
                fileWrite();
                //let the user know the password change was successful
                LoginNotification.setText("your password has been changed");
                //hide the forget password tools
                forgotPassButton.setVisible(false);
                jLabel1.setVisible(false);
                forgotName.setVisible(false);
                jLabel2.setVisible(false);
                forgotEmail.setVisible(false);
                jLabel3.setVisible(false);
                forgotNewPass.setVisible(false);
                resetPassButton.setVisible(false);

            } else {
                //if the email entered doesn't match the registered user's data. Don't allow a password change and ask for it again.
                LoginNotification.setText("That's not the right email");
            }
        } else {
            //if the user isn't registered yet, ask them to register.
            LoginNotification.setText("There's no user with that name. Please register");

        }

    }

    /**
     * Encrypts the desired user password using SHA-256 algorithms
     *
     * @param password
     * @return encrypted password
     */
    private String digestPassword(String password) {
        try {
            //encrypts the password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte byteData[] = md.digest();
            String encryptedPassword = "";
            for (int i = 0; i < byteData.length; ++i) {
                encryptedPassword += (Integer.toHexString((byteData[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return encryptedPassword;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No  algorithm SHA-256 found.");
        }
        return "";
    }

    /**
     * Checks to see if the user name or email is already in the system
     *
     * @param user
     * @param email
     * @return false, if the username/email already exists. True, if the
     * username/email doesn't exist yet
     */
    private boolean existingUser(String user, String email) {
        //checks to see if the user name is already in the system
        if (userNames.contains(user)) {
            //asks the user for a new user name
            edit = "That user name already exists.";
            //clears the field
            newUserEntry.setText("");
            //exit the method
            return false;
        }
        if (emails.contains(email)) {
            //aks the user for another email
            edit = "That email already has an account";
            //clear the field
            newEmailEntry.setText("");
            //exit the method
            return false;
        }
        //if the user name and email isn't already registered returns true with no changes
        return true;
    }

    /**
     * Creates new form secureLoginUI
     */
    public secureLoginUI() throws FileNotFoundException, IOException, ClassNotFoundException, EOFException {
        initComponents();
        //hide the forget password tools
        forgotPassButton.setVisible(false);
        jLabel1.setVisible(false);
        forgotName.setVisible(false);
        jLabel2.setVisible(false);
        forgotEmail.setVisible(false);
        jLabel3.setVisible(false);
        forgotNewPass.setVisible(false);
        resetPassButton.setVisible(false);

        //reads the file
        Scanner readFile = new Scanner(file);

        String line = null;

        //continues to read the file until it reaches the end
        while (readFile.hasNextLine()) {
            //takes in each line of the file
            line = readFile.nextLine();
            //tokenizes the data of each line
            String[] fileLine = line.split(";");

            //adds the username of that line to the arraylist
            userNames.add(fileLine[0]);
            //adds the email of that line to the arraylist
            emails.add(fileLine[1]);
            //adds the encrypted password of that line to the arraylist
            passwords.add(fileLine[2]);
        }

        readFile.close();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Title = new javax.swing.JLabel();
        loginLabel = new javax.swing.JLabel();
        nameEntry = new javax.swing.JTextField();
        passwordEntry = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        LoginNotification = new javax.swing.JLabel();
        loginButton = new javax.swing.JButton();
        newUserEntry = new javax.swing.JTextField();
        newEmailEntry = new javax.swing.JTextField();
        newPasswordEntry = new javax.swing.JTextField();
        newUserTitle = new javax.swing.JLabel();
        newEmailLabel = new javax.swing.JLabel();
        newPasswordLabel = new javax.swing.JLabel();
        newUserLabel = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        newUserUpdateLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        forgotPassButton = new javax.swing.JButton();
        forgotName = new javax.swing.JTextField();
        forgotEmail = new javax.swing.JTextField();
        forgotNewPass = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        resetPassButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Title.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        Title.setText("Willy Wonka Chocolate Factory Applications");

        loginLabel.setText("Login Name:");

        passwordLabel.setText("Password:");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        newUserTitle.setText("New User Account");

        newEmailLabel.setText("Enter Your Email:");

        newPasswordLabel.setText("Enter Your Password:");

        newUserLabel.setText("Enter Your User Name:");

        registerButton.setText("Register");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        forgotPassButton.setText("Forgot password?");
        forgotPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgotPassButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("User Name");

        jLabel2.setText("Email");

        jLabel3.setText("New Password:");

        resetPassButton.setText("Reset Password");
        resetPassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetPassButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(newUserUpdateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(registerButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(newUserTitle)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addComponent(LoginNotification)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(forgotPassButton)
                                .addGap(18, 18, 18)
                                .addComponent(loginButton)
                                .addGap(60, 60, 60))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(loginLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nameEntry)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(passwordLabel)
                                        .addGap(18, 18, 18)
                                        .addComponent(passwordEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(forgotName, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(forgotEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(forgotNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(61, 61, 61))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(newUserLabel)
                                    .addComponent(newEmailLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(newUserEntry)
                                    .addComponent(newEmailEntry)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(newPasswordLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(newPasswordEntry)))
                        .addGap(229, 229, 229))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetPassButton)
                                .addGap(88, 88, 88))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(Title)
                                .addGap(109, 109, 109))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(Title)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(loginLabel)
                        .addComponent(passwordLabel))
                    .addComponent(passwordEntry)
                    .addComponent(nameEntry))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LoginNotification)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(forgotPassButton)
                        .addComponent(loginButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(forgotName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(forgotNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(forgotEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(resetPassButton)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newUserTitle)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newUserLabel)
                    .addComponent(newUserEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newEmailLabel)
                    .addComponent(newEmailEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newPasswordLabel)
                    .addComponent(newPasswordEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newUserUpdateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registerButton))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * When the login button is pressed check to make sure all the data matches,
     * if it doesn't ask the user to register or complete forgot password
     * process
     *
     * @param evt
     */
    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        //hides the forget password tools
        forgotPassButton.setVisible(false);
        jLabel1.setVisible(false);
        forgotName.setVisible(false);
        jLabel2.setVisible(false);
        forgotEmail.setVisible(false);
        jLabel3.setVisible(false);
        forgotNewPass.setVisible(false);
        resetPassButton.setVisible(false);

        //gets the inputted user name
        String loginUser = nameEntry.getText();
        //gets the inputted password
        String loginPassword = passwordEntry.getText();

        //checks to see if the user is registered
        if (userNames.contains(loginUser)) {
            //checks to see if the password matches the registered users' password in the file
            if (passwords.contains(digestPassword(loginPassword))) {
                //if the password matches, success
                LoginNotification.setText("Welcome back!");
                //clear all login fields
                nameEntry.setText("");
                passwordEntry.setText("");
            } else {
                //if the password doesn't match, tell the user it's the wrong password and show the forgot password tools
                LoginNotification.setText("That was the wrong password.");
                forgotPassButton.setVisible(true);
                //clear the password field
                passwordEntry.setText("");
            }
        } else {//if the user isn't regestered ask the user to regester and clear the fields
            LoginNotification.setText("That username deosn't exist. please register below");
            //clear all login fields
            nameEntry.setText("");
            passwordEntry.setText("");
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    /**
     * When the register button is pressed, check to make sure register
     * conditions are met if they aren't ask for them, if they are store them in
     * the user file
     *
     * @param evt
     */
    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        //hide forgot password tools
        forgotPassButton.setVisible(false);
        jLabel1.setVisible(false);
        forgotName.setVisible(false);
        jLabel2.setVisible(false);
        forgotEmail.setVisible(false);
        jLabel3.setVisible(false);
        forgotNewPass.setVisible(false);
        resetPassButton.setVisible(false);

        //reset the update labels
        newUserUpdateLabel.setText("");

        //checks to see if user name, email and password meet conditions
        boolean errors = checkForRegisterErrors(newUserEntry.getText(), newEmailEntry.getText(), newPasswordEntry.getText());
        //checks to make sure the username/email doesn't already exist
        boolean existingUsers = existingUser(newUserEntry.getText(), newEmailEntry.getText());
        //if all conditions are met, register them
        if (errors && existingUsers) {
            //add the username to the array list
            userNames.add(newUserEntry.getText());
            //add the email to the array list
            emails.add(newEmailEntry.getText());
            //encrypt the password and add it to the array list
            passwords.add(digestPassword(newPasswordEntry.getText()));
            
            //update the user data file
            fileWrite();
            
            //clera the text fields
            newUserEntry.setText("");
            newEmailEntry.setText("");
            newPasswordEntry.setText("");
        } else {
            //if conditions aren't met tell the user to meet them
            newUserUpdateLabel.setText(edit);
            edit = "";
        }

    }//GEN-LAST:event_registerButtonActionPerformed

    /**
     * When the forgot password button is pressed reveals all reset password tools
     * @param evt 
     */
    private void forgotPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgotPassButtonActionPerformed
        //sets all reset password tools to visible
        jLabel1.setVisible(true);
        forgotName.setVisible(true);
        jLabel2.setVisible(true);
        forgotEmail.setVisible(true);
        jLabel3.setVisible(true);
        forgotNewPass.setVisible(true);
        resetPassButton.setVisible(true);

    }//GEN-LAST:event_forgotPassButtonActionPerformed

    /**
     * when the reset password button is pressed, 
     * checks to see the username matches the email address
     * checks to see the new password matches conditions
     * @param evt 
     */
    private void resetPassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPassButtonActionPerformed
        //resets the update label
        LoginNotification.setText("");
        
        //checks to make sure the username, password and email meet all conditions
        boolean errors = checkForRegisterErrors(forgotName.getText(), forgotEmail.getText(), forgotNewPass.getText());
        //if all the conditions are met do the following
        if (errors) {
            String forgetUser = forgotName.getText();
            String forgetEmail = forgotEmail.getText();
            //digest the password 
            String forgotPassword = digestPassword(forgotNewPass.getText());
            //call the reset password method 
            resetPassword(forgetUser, forgetEmail, forgotPassword);

        } else {
            //if the conditions aren't met, tell the user to meet the conditions
            LoginNotification.setText(edit);
            edit = "";
        }
    }//GEN-LAST:event_resetPassButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(secureLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(secureLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(secureLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(secureLoginUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new secureLoginUI().setVisible(true);
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                } catch (ClassNotFoundException e) {

                }

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LoginNotification;
    private javax.swing.JLabel Title;
    private javax.swing.JTextField forgotEmail;
    private javax.swing.JTextField forgotName;
    private javax.swing.JTextField forgotNewPass;
    private javax.swing.JButton forgotPassButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JTextField nameEntry;
    private javax.swing.JTextField newEmailEntry;
    private javax.swing.JLabel newEmailLabel;
    private javax.swing.JTextField newPasswordEntry;
    private javax.swing.JLabel newPasswordLabel;
    private javax.swing.JTextField newUserEntry;
    private javax.swing.JLabel newUserLabel;
    private javax.swing.JLabel newUserTitle;
    private javax.swing.JLabel newUserUpdateLabel;
    private javax.swing.JTextField passwordEntry;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JButton registerButton;
    private javax.swing.JButton resetPassButton;
    // End of variables declaration//GEN-END:variables
}
