package views;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controllers.AccountController;
import controllers.MessageController;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;


/**
 * This is the Login view class
 * it is the main class that is run.
 * It allows the user to register and login and provides the GUI	 
 * @author u1358629
 *
 */
public class LoginView extends JFrame {

	private JPanel contentPane;
	private JTextField usernameField;
	private JLabel lblUsername,lblPassword;
	private JPasswordField passwordField;
	private JButton btnLogin,btnRegister, btnCancel;
	MessageController msgController;


	/**
	 * Launch the application.
	 * Main Method
	 */
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}
		LoginView frame = new LoginView();
		frame.setVisible(true);


	}

	/**
	 * Create the frame.
	 * Layout of the login view
	 */
	public LoginView() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		usernameField = new JTextField();
		usernameField.setBounds(12, 23, 415, 35);
		contentPane.add(usernameField);
		usernameField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(12, 114, 415, 35);
		contentPane.add(passwordField);

		lblUsername = new JLabel("Username");
		lblUsername.setBounds(193, 0, 81, 17);
		contentPane.add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setBounds(193, 81, 81, 15);
		contentPane.add(lblPassword);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(12, 161, 415, 25);
		contentPane.add(btnRegister);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(12, 196, 415, 25);
		contentPane.add(btnLogin);

		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(12, 233, 415, 25);
		contentPane.add(btnCancel);


		/**
		 * Action listeners 
		 */
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//get entered text in username field
				AccountController  account = new AccountController(usernameField.getText(),
						//get entered text in password field
						passwordField.getText());
				//if account equals login
				if(account.login()){
					//set visible
					setVisible(false);
					MessageController aa = 	new MessageController(usernameField.getText());
				}
			}
		});

		// add action listener to register button
		btnRegister.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//get username textfield
				AccountController  account = new AccountController(usernameField.getText(),
						//get password field
						passwordField.getText());
				//run the register method
				account.register();
				System.out.println(usernameField.getText() +
						passwordField.getText());
			}
		});

		//abort and close window
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(ABORT);
			}});

	}
}
