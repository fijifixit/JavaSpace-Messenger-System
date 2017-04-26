package views;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controllers.MessageController;
import models.IH1MessageModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;
import utils.SpaceUtils;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 * This is the Main View class. 
 * It provides the view that is loaded when the user logs in
 * I includes the action listeners for all the buttons, fields, lists and checkboxes.
 * @author u1358629
 *
 */
public class MainView extends JFrame  {

	private JPanel contentPane;
	private static JavaSpace05 space;
	private JLabel messageLabel,personNameLabel,lblMessageRece,lblSavedMessages,lblTimeout;
	private JButton sendMessageButton,deleteIncomingMessage,saveMessage,
	viewSavedMessages,deleteAllSavedMessages,deleteSaveMessages,deleteAllIncomingMessages,logoutBtn,btnUploadProfilePicture;
	private JCheckBox airplaneCheckBox,notifcationsCheckbox;
	private DefaultListModel saveModel, listModel, onlineModel;
	private JTextField personNameIn,timeoutField;
	private JTextArea messageNameIn; 
	private JList incomingList,saveList;
	MessageController msgController;
	Image img;
	static JLabel profile;
	/**
	 * Implementing getter and setter methods
	 * Allows me to set all fields to private
	 * @return
	 */
	public String getPersonNameIn(){
		return personNameIn.getText();

	}


	public String getTimeoutField(){
		return timeoutField.getText();

	}

	public String getMessageNameIn(){
		return messageNameIn.getText();

	}


	public DefaultListModel getSaveModel(){
		return saveModel;

	}

	public DefaultListModel getListModel(){
		return listModel;

	}

	public DefaultListModel getOnlineModel(){
		return onlineModel;

	}

	public JList getIncomingList(){
		return incomingList;

	}

	public JList getSaveList(){
		return saveList;

	}

	public JCheckBox getAirplaneCheckBox(){
		return airplaneCheckBox;

	}

	public JCheckBox notifcationsCheckbox(){
		return notifcationsCheckbox;

	}

	public void setMessageNameIn(String newMess){
		messageNameIn.setText(newMess);

	}

	public void setPersonNameIn(String newPers){
		personNameIn.setText(newPers);

	}


	public MainView(MessageController msgController){
		setResizable(false);
		this.msgController = msgController;
		launchInterface();
		setListeners();
	}


	public void launchInterface() {
		setTitle ("New Chat | Your Username: " + msgController.getClientMessageName());
		//layout
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 926, 453);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(128,128,128));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		messageLabel = new JLabel("Message to Send:");
		messageLabel.setBounds(442, 20, 160, 15);
		contentPane.add(messageLabel);

		personNameLabel = new JLabel("Persons Name:");
		personNameLabel.setBounds(38, 20, 129, 15);
		contentPane.add(personNameLabel);

		lblMessageRece = new JLabel("Message Received");	
		lblMessageRece.setBounds(133, 141, 214, 15);
		contentPane.add(lblMessageRece);

		lblSavedMessages = new JLabel("Saved Messages");
		lblSavedMessages.setBounds(555, 141, 160, 15);
		contentPane.add(lblSavedMessages);

		lblTimeout = new JLabel("Expiration Time (m)");
		lblTimeout.setBounds(200, 20, 148, 15);
		contentPane.add(lblTimeout);

		personNameIn = new JTextField();
		personNameIn.setText("");
		personNameIn.setBounds(22, 46, 160, 25);
		contentPane.add(personNameIn);
		personNameIn.setColumns(10);

		messageNameIn = new JTextArea();
		messageNameIn.setLineWrap(true);
		messageNameIn.setText("");
		messageNameIn.setBounds(369, 46, 291, 77);
		contentPane.add(messageNameIn);

		timeoutField = new JTextField();
		timeoutField.setBounds(194, 46, 160, 25);
		contentPane.add(timeoutField);
		timeoutField.setColumns(10);


		listModel = new DefaultListModel();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 168, 355, 149);
		contentPane.add(scrollPane);
		incomingList = new JList();
		scrollPane.setViewportView(incomingList);
		incomingList.setModel(listModel);


		onlineModel = new DefaultListModel();


		saveModel = new DefaultListModel();
		saveList = new JList();
		saveList.setBounds(435, 168, 400, 149);
		contentPane.add(saveList);
		saveList.setModel(saveModel);


		/*
		 * Messages Received
		 */
		deleteIncomingMessage = new JButton("Delete Message");
		deleteIncomingMessage.setBounds(38, 329, 157, 25);
		contentPane.add(deleteIncomingMessage);

		saveMessage = new JButton("Save Message");
		saveMessage.setBounds(208, 329, 165, 25);
		contentPane.add(saveMessage);


		deleteAllIncomingMessages = new JButton("Delete All Messages");
		deleteAllIncomingMessages.setBounds(42, 366, 335, 30);
		contentPane.add(deleteAllIncomingMessages);


		/*
		 * Saved Messages
		 */

		deleteSaveMessages = new JButton("Delete Message");
		deleteSaveMessages.setBounds(425, 329, 205, 25);
		contentPane.add(deleteSaveMessages);

		viewSavedMessages = new JButton("View Saved Messages");
		viewSavedMessages.setBounds(642, 329, 199, 25);
		contentPane.add(viewSavedMessages);

		deleteAllSavedMessages = new JButton("Delete All Saved Messages");
		deleteAllSavedMessages.setBounds(425, 366, 416, 25);
		contentPane.add(deleteAllSavedMessages);


		/*
		 * Rest of layout
		 */

		sendMessageButton = new JButton("Send Message");
		sendMessageButton.setBounds(22, 98, 335, 25);
		contentPane.add(sendMessageButton);

		airplaneCheckBox = new JCheckBox("Airplane Mode Off");
		airplaneCheckBox.setSelected(true);
		airplaneCheckBox.setBounds(721, 20, 148, 23);
		contentPane.add(airplaneCheckBox);

		notifcationsCheckbox = new JCheckBox("Notifications");
		notifcationsCheckbox.setSelected(true);
		notifcationsCheckbox.setBounds(721, 47, 148, 23);
		contentPane.add(notifcationsCheckbox);

		btnUploadProfilePicture = new JButton("Upload Profile Picture");
		btnUploadProfilePicture.setBounds(697, 109, 199, 25);
		contentPane.add(btnUploadProfilePicture);

		logoutBtn = new JButton("Logout");
		logoutBtn.setBounds(697, 72, 199, 25);
		contentPane.add(logoutBtn);

		profile = new JLabel();
		profile.setBounds(903, 20, 100, 100);
		contentPane.add(profile);


		btnUploadProfilePicture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					main(null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

	}

	/**
	 * This method allows the user to upload a profile picture
	 * It makes the use of byte arrays.
	 * The image is converted into a byte array
	 * It is the written to the javaspace
	 * The matching template is found and retreived
	 * It is then converted from a byte array back to an image
	 * @param args
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//find javaspace
		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		//set a new file chooser
		JFileChooser fc = new JFileChooser();
		int result = fc.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			//get selected file from file chooser
			File file = fc.getSelectedFile();
			//create a new file input stream and 
			FileInputStream fis = new FileInputStream(file);
			//create FileInputStream which obtains input bytes from a file in a file system
			//FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			try {
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					//Writes to this byte array output stream
					bos.write(buf, 0, readNum); 
					System.out.println("read " + readNum + " bytes,");
				}
				//error handling
			} catch (IOException ex) {
				Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
			}
			//save bos which is converted to a byte array to bytes
			byte[] bytes = bos.toByteArray();
			try{

				byte[] biter = bytes;
				// convert bytes to a string
				String biteString  = biter.toString();
				//create a new template and write the saves byte to string to space.
				IH1MessageModel saveList = new IH1MessageModel(biteString);		//changed this
				space.write(saveList, null, Lease.FOREVER);
			}catch(Exception e){
				e.printStackTrace();
			}

			//retrive from space
			byte[] mr = bytes;
			IH1MessageModel newTemplate = new IH1MessageModel();
			newTemplate.image = mr.toString();
			try {
				space.read(newTemplate, null, Long.MAX_VALUE);

				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");

				//ImageIO is a class containing static methods for locating ImageReaders
				//and ImageWriters, and performing simple encoding and decoding. 
				ImageReader reader = (ImageReader) readers.next();
				Object source = bis; 
				ImageInputStream iis = ImageIO.createImageInputStream(source); 
				reader.setInput(iis, true);
				ImageReadParam param = reader.getDefaultReadParam();
				Image image = reader.read(0, param);
				//got an image file

				BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
				//bufferedImage is the RenderedImage to be written

				Graphics2D g2 = bufferedImage.createGraphics();
				g2.drawImage(image, null, null);

				//get selected file
				File imageFile = fc.getSelectedFile();
				//write the converted image to the label
				ImageIO.write(bufferedImage, "jpg", imageFile);
				ImageIcon icon = new ImageIcon(image); 

				profile.setIcon(icon);
			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}


	public void setListeners(){

		/*
		 * Messages Received
		 */
		incomingList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {

				int index = incomingList.getSelectedIndex();
				if(index >= 0){ //Remove only if a particular item is selected
					msgController.reply();
				}
			}});


		deleteIncomingMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int index = incomingList.getSelectedIndex();
				if(index >= 0){ //Remove only if a particular item is selected
					listModel.removeElementAt(index);
					msgController.deleteSingleIncomingMessages();
				}
			}});

		deleteAllIncomingMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgController.deleteAllIncomingMessages();
				System.out.println("All viewed Messages have been deleted");
				{
					listModel.clear();
				}
			}
		});

		saveMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgController.saveMessage();
			}
		});


		/*
		 * Saved Messages
		 */

		deleteSaveMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = saveList.getSelectedIndex();
				if(index >= 0){ //Remove only if a particular item is selected
					saveModel.removeElementAt(index);
					msgController.deleteSingleSaveMessages();

				}
			}});

		deleteAllSavedMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgController.deleteAllSavedMessage();
				System.out.println("All Saved Messages have been deleted");
				{
					saveModel.clear();
				}
			}
		});


		viewSavedMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgController.viewSavedMessages();
			}
		});



		/*
		 * Rest of layout
		 */

		sendMessageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				msgController.checkMessageType();
			}
		});


		logoutBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onlineModel.clear();
				System.exit(ABORT);


			}
		});


		airplaneCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//if state has changed to deselected
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					//set color
					contentPane.setBackground(new Color(255,102,102));
					//set title
					setTitle ("✈ AIRPLANE MODE ENABLED" + "" +"|" +  "New Chat | Your Username: " + msgController.getClientMessageName());
					System.out.println("Airplane mode has been enabled. Disable Airplane mode to view messages");
					try {
						//run cancel method
						msgController.cancel();
					} catch (UnknownLeaseException | RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//if the state is back to selected
				} else if (e.getStateChange() == ItemEvent.SELECTED){
					//set the title
					setTitle ("New Chat | Your Username: " + msgController.getClientMessageName());
					System.out.println("Airplane mode has been disabled");
					//run the airplane mode method
					msgController.airplaneMode();
					contentPane.setBackground(new Color(128,128,128));
					try {
						//run the renew lease method
						msgController.renewLease();
					} catch (UnknownLeaseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});


		addWindowListener (new java.awt.event.WindowAdapter () {
			@Override
			public void windowClosing (java.awt.event.WindowEvent evt) {
				System.exit(0);

			}
		}   );


		setVisible(true);

	}
}
