package controllers;



import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
//import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;
import utils.SpaceUtils;
import views.MainView;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import models.IH1MessageModel;
import models.ReadModel1;

/**
 * 
 * This class will be running all the message methods.
 * It is used as the main channel for the communication
 * all the writing and taking from the space of the messages is done in this method
 * It includes some of the basic code provided by Dr Gary Allen
 * @author u1358629
 *
 */
@SuppressWarnings("serial")
public class MessageController implements RemoteEventListener {
	private JavaSpace05 space;
	private TransactionManager mgr;
	private RemoteEventListener theStub;
	private Component temporaryLostComponent;
	private EventRegistration notifyLease, notifyLeaseRead;
	private String clientMessageName;
	MainView main;

	/**
	 * This main method sets the look and feel of the application
	 * If it cannot find it it will throw an error
	 * @param args
	 * @throws RemoteException
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws RemoteException{
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());



	}
	public String getClientMessageName(){
		return clientMessageName;
	}

	/**
	 * find the javaspace otherwise error and exit
	 * get the transaction manager otherwise error and exit
	 * consttuctor also creates the exporter for the remote event
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MessageController(String cName){

		// sets the clientMessageName to the cname parameter
		clientMessageName = cName;
		//sets the main view to be accesed from this class
		main = new MainView(this);

		//find javaspace else throw error
		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		//find transaction manager else throw error
		mgr = SpaceUtils.getManager();
		if (mgr == null){
			System.err.print("Failed to find the transaction manager");
			System.exit(1);
		}

		// create the exporter
		Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
				new BasicILFactory(), false, true);
		try {
			// register this as a remote objectsendMessageButton
			// and get a reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// add the listener
			IH1MessageModel template = new IH1MessageModel();
			//capture the notifiy into a lease
			notifyLease = space.notify(template, null, this.theStub, Lease.FOREVER, new MarshalledObject("message"));

			ReadModel1 read = new ReadModel1();
			//capture the notifiyLeaseRead into a lease
			notifyLeaseRead = space.notify(read, null, this.theStub, Lease.FOREVER, new MarshalledObject("readMessage"));

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		//run the takeAll method
		takeAll();
	}


	/**
	 * This is the notify method which is called when we are notified of an object of interest.
	 * is it for retrieve messages that were sent by a client
	 * It invokes multiple notify methods which allow the users to retrieve messages that were sent to them
	 * and also send a notification back to the user when the message has been read
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void notify(RemoteEvent ev) {
		// create a marshalled object
		MarshalledObject obj = ev.getRegistrationObject();
		String notifyType = null;
		try{
			notifyType = (String) obj.get();
		}catch(ClassNotFoundException| IOException io){
			io.printStackTrace();
		} 
		//notify for getting a message
		if (notifyType.equals("message")){

			if(main.notifcationsCheckbox().isSelected()){

				//      this is the method called when we are notified
				//      of an object of interest
				//		create a retriving template
				IH1MessageModel template = new IH1MessageModel();
				template.destinationChatClient = clientMessageName;

				try {
					//take the template from the space
					IH1MessageModel result = (IH1MessageModel)space.take(template, null, Long.MAX_VALUE);

					//get the message that was sent and the name of the sender and add it to the appropriate variables
					String msgSent = result.messageSent;
					String thePersonMessageFrom = result.theName;

					//append the name of the sender and the message that was sent.
					main.getListModel().addElement("Message From: " + "♣"+thePersonMessageFrom+ "♣"+ " Message: " + msgSent +"♣" +result.theGroup+ "♣"+"\n" );

					//Notification in a jOptionPane
					JOptionPane.showMessageDialog(temporaryLostComponent, "You have a Message From:"+ ""+thePersonMessageFrom,"New Message",JOptionPane.PLAIN_MESSAGE);
					//cancel the readMessages Lease
					notifyLeaseRead.getLease().cancel();
					//initiaite the writeReadToSpace method and pass through nextJobName nJobName parameters
					writeReadToSpace(msgSent, thePersonMessageFrom);
					// start the notify method for readMessage
					ReadModel1 read = new ReadModel1();
					notifyLeaseRead = space.notify(read, null, this.theStub, Lease.FOREVER, new MarshalledObject("readMessage"));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/**
			 *same as the above method except it does not notify the user 
			 *
			 */
			else{
				IH1MessageModel template = new IH1MessageModel();
				template.destinationChatClient = clientMessageName;

				try {
					IH1MessageModel result = (IH1MessageModel)space.take(template, null, Long.MAX_VALUE);
					String msgSent = result.messageSent;
					String thePersonMessageFrom = result.theName;
					main.getListModel().addElement("Message From: " + "♣"+thePersonMessageFrom+ "♣"+ " Message: " + msgSent +"♣" +result.theGroup+ "♣" +"\n" );
					writeReadToSpace(msgSent, thePersonMessageFrom);
				} catch (Exception e) {
					e.printStackTrace();
				}




			}


		}

		//notify for readMessage
		if (notifyType.equals("readMessage")){

			try{
				//get the template to take
				ReadModel1 template = new ReadModel1();
				template.theName = clientMessageName;
				//takeIfExists and wait 3 seconds
				ReadModel1 result = (ReadModel1)space.takeIfExists(template, null, 1000*3);
				//error handling
				if (result != null){
					String thePersonReadBy = result.destinationChatClient;
					String messageThatIsRead = result.msgRead;
					//print message
					System.out.println(clientMessageName + " says:  Message read by: "+ thePersonReadBy + " The message that was read:"+ messageThatIsRead);
				}

			}catch(Exception e){
				e.printStackTrace();

			}
		}
	}


	/**
	 * method to write read reciepts to space
	 * it will pass through the person who has read the message along with the message that was read to the space
	 * @param mess
	 * @param to
	 */
	public void writeReadToSpace(String mess, String to){

		try{
			String theName = clientMessageName;
			String msgSent = mess;
			String thePersonMessageFrom = to;
			ReadModel1 saveList = new ReadModel1(msgSent,theName,thePersonMessageFrom);
			//write the name and message to the space
			space.write(saveList, null, Lease.FOREVER);
		}catch(Exception e){
			e.printStackTrace();

		}

	}




	/**
	 * This method is called when the user enables airplane mode. 
	 * It uses the lease that was created on the listener and cancels it.
	 * This queues all the messages and stops the notify method running
	 * @throws UnknownLeaseException
	 * @throws RemoteException
	 */
	public void cancel () throws UnknownLeaseException, RemoteException {

		try{
			//get the notifyLease and cancel itould you like green eggs and ham?
			notifyLease.getLease().cancel();
		}
		//error handling
		catch(UnknownLeaseException e1){

		}
		catch(RemoteException e3){
		}
	}

	/**
	 * This method does the opposite of the cancel method.
	 * when the user disables airplane mode, it gets the listener
	 * and renews the lease on it.
	 * This enables all the messages to come through as well as retrieving all the messages.
	 * @throws UnknownLeaseException
	 * @throws RemoteException
	 */
	public void renewLease () throws UnknownLeaseException, RemoteException {
		IH1MessageModel template = new IH1MessageModel();
		try {
			//renew the listener
			notifyLease = space.notify(template, null, this.theStub, Lease.FOREVER, null);
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Method for saving Messages
	 * get selected value from the list and store as string to the variable saveToSpace
	 * set the clientMessageName as a string to the parameter theName
	 * create template and write to space
	 */
	public void saveMessage(){
		try{
			// get selected message from the list
			String saveToSpace = (String) main.getIncomingList().getSelectedValue();
			String theName = clientMessageName;
			//create new tenplate and write the items to space
			IH1MessageModel saveList = new IH1MessageModel(saveToSpace,theName);
			space.write(saveList, null, Lease.FOREVER);
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	/**
	 * Create a collection of templates as a new Array
	 * create template for matching objects
	 * add all the objects that want to be found to the collection
	 * take the collection from the space
	 * iterate to the next matching object / retrive / iterate / retrive
	 * set the saved message to the correct model
	 * write the template back to the space to hold the saved messages until further viewing
	 * throw exception if any errors
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void viewSavedMessages(){
		try {
			//create a new transaction
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, 1000);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);;
			}
			//transaction name
			Transaction txn = trc.transaction; 
			//create a new collection of templates
			Collection<IH1MessageModel> newTemplate = new ArrayList<IH1MessageModel>();
			IH1MessageModel template = new IH1MessageModel();
			template.theName = clientMessageName;
			String m = template.messageSent;
			//add all the templates to the collection
			newTemplate.add(template);

			//retrive all the matching templates from the space
			Collection<?> results =  space.take(newTemplate, txn, 100, Long.MAX_VALUE);
			//iterate all the templates found
			Iterator<?> i = results.iterator();
			while (i.hasNext()) {
				IH1MessageModel save = (IH1MessageModel) i.next();
				//add the messager to the save list
				main.getSaveModel().addElement(save.messageSent + "\n");

			}
			//abort transaction
			txn.abort();

		}catch(Exception e){
			e.printStackTrace();
		}

	}


	/**
	 * This method allows the user to delete all the saved messages 
	 * It uses a collection of templates
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteAllSavedMessage(){
		//create a new collection of templates
		Collection<IH1MessageModel> templates = new ArrayList<IH1MessageModel>();
		IH1MessageModel template = new IH1MessageModel();
		template.theName = clientMessageName;
		//add the templates to the collection
		templates.add(template);
		try {
			//take the matching templates from the space
			Collection<?> results = space.take(templates, null,100, Long.MAX_VALUE);
			Iterator<?> i = results.iterator();
			while (i.hasNext()) {
				IH1MessageModel s = (IH1MessageModel) i.next();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	/**
	 * this method allows the user to delete single saved messages 
	 * it is called when the delete buttton is clicked for single saved messages
	 */
	public void deleteSingleSaveMessages(){
		//create a new template
		IH1MessageModel newTemplate = new IH1MessageModel();
		newTemplate.theName = clientMessageName;
		try {
			//take the entries from the space
			space.take(newTemplate, null, Long.MAX_VALUE);
			//error handling
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * this method allows the user to delete single incoming messages 
	 * it is called when the delete buttton is clicked for single incoming messages
	 */
	public void deleteSingleIncomingMessages(){
		//create a new template
		IH1MessageModel newTemplate = new IH1MessageModel();
		newTemplate.theName = clientMessageName;
		try {
			//take the entries from the space if they exist
			space.takeIfExists(newTemplate, null, Long.MAX_VALUE);
			//error handling
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This method allows the user to delete all the incoming messages 
	 * It uses a collection of templates
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteAllIncomingMessages(){
		//create a new collection of templates
		Collection<IH1MessageModel> templates = new ArrayList<IH1MessageModel>();

		IH1MessageModel template = new IH1MessageModel();
		template.theName = clientMessageName;
		templates.add(template);
		try {
			//take the matching templates from the space
			Collection<?> results = space.take(templates, null,100, Long.MAX_VALUE);
			//iterate between the matching templates
			Iterator<?> i = results.iterator();
			while (i.hasNext()) {
				IH1MessageModel s = (IH1MessageModel) i.next();
				System.out.println("All Messages have been deleted");
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}



	/**
	 * This is the method that is called when a user logs in.
	 * It checks to see if there are any messages in the JavaSpace for that user
	 * If any messages are found it will retrive all of them
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void takeAll(){
		try{	
			//create a new transaction
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, 1000);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);;
			}
			Transaction txn = trc.transaction; 
			//Create a collection of templates
			Collection<IH1MessageModel> templates = new ArrayList<IH1MessageModel>();
			//create the templates
			IH1MessageModel template = new IH1MessageModel();
			template.destinationChatClient = clientMessageName;
			//add the entries to the collection
			templates.add(template);
			//take the collection of templates from the space.
			Collection<?> results = space.take(templates, txn,100,Long.MAX_VALUE);
			// print out the results and iterate through the templates
			Iterator<?> i = results.iterator();
			while (i.hasNext()) {
				IH1MessageModel s = (IH1MessageModel) i.next();
				//add the name of the sender and the message to the list
				main.getListModel().addElement("Message From: " +"♣" +s.theName+"♣" + " Message: " + s.messageSent +"♣" +s.theGroup+ "♣"+ "\n" );
			}
			//commit transaction
			txn.commit();
			//error handling
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * this is the method that is called when the user disables airplane mode
	 * it retrieves all the messages that they got whilst in airplane mode
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void airplaneMode(){
		//create a collection of templates
		Collection<IH1MessageModel> templates = new ArrayList<IH1MessageModel>();
		IH1MessageModel template = new IH1MessageModel();
		template.destinationChatClient = clientMessageName;
		templates.add(template);
		try{
			//take the collection of templates from the space
			Collection<?> results = space.take(templates, null,10,200);
			// print out the results and iterate through templates
			Iterator<?> i = results.iterator();
			while (i.hasNext()) {
				IH1MessageModel s = (IH1MessageModel) i.next();
				//add the messages recieved along with the name of the person it was sent from 
				main.getListModel().addElement("Message From: " +"♣" +s.theName+"♣" + " Message: " + s.messageSent +"♣" +s.theGroup+ "♣"+ "\n" );
			}
			//error handling
		}	catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this is the method that is called when the user selects a message from the list
	 * It appends the persons name into the text box ready to send a message to them
	 */
	public void reply(){
		//if nothing is selected
		if(main.getIncomingList().getSelectedValue() == null){
			//print string
			System.out.println("Select a message to reply");
		}

		else {

			//get the model of the list
			main.getIncomingList().getModel();
			//get the selected message and convert it to a string
			String getMessage = main.getIncomingList().getSelectedValue().toString();
			//split the message with the club symbol
			String [] username = getMessage.split("♣");
			//trim the username from the message
			String getUsername = username[1].trim();
			// split the third parameter of the array
			String[] arr = username[3].split(",");
			if(arr.length>1){
				System.out.println("This is  group message");
				int n = JOptionPane.showConfirmDialog(
						main,
						"Would you like to reply to group?",
						"Group Reply",
						JOptionPane.YES_NO_OPTION);
				if(n == JOptionPane.YES_OPTION){
					//insert comma between names
					getUsername = getUsername +","+ username[3];
				}
			}else{
				System.out.println("This is not agroup message");

			}
			main.setPersonNameIn(getUsername);
		}
	}

	/**
	 * This method is called when the user clicks the send message button
	 * IT checks if there is more than one string seperated by a comma in the textbox
	 * if it isnt then it runs the send message method. 
	 * else it splits the strings into an array and then runs the send message method
	 */
	public void checkMessageType(){
		//split person name in into an array
		String[] arr = main.getPersonNameIn().split(",");
		//if array length is equal to 1
		if(arr.length==1){
			//run method
			sendMessage(arr[0]);
		}else {
			//loops round and gets the number of users
			for(int i = 0;i<arr.length;i++){
				//run method
				sendMessage(arr[i]);
			}
			System.out.println(arr.length + " numer of users sent to");

		}
	}

	/**
	 * This is the method called when someone clicks the send message button
	 * It uses leases to tell the user when the message will expire
	 * It also lets the user add their own expiration time.
	 */
	@SuppressWarnings("unchecked")
	public void sendMessage(String to){
		try {
			//create a new transaction
			Transaction.Created trc = null;
			try {
				trc = TransactionFactory.create(mgr, 1000);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);;
			}
			Transaction txn = trc.transaction; 
			//get the entries to write to the space.
			String messageToSend = main.getMessageNameIn();
			String personName = to;
			String theName = clientMessageName;
			String timeout = main.getTimeoutField();
			long theLease;
			//if the timeout field is empty and equal to 0
			if (main.getTimeoutField() == null || main.getTimeoutField().isEmpty()){
				//set the parameter "theLease" to long.Max_Value
				theLease = Long.MAX_VALUE;
			}

			else{
				//else get the value of the timeout field and parse it to long
				theLease = Long.parseLong(main.getTimeoutField());
				//get the value entered and multiply by 1000*60  (1 minute)
				theLease = theLease * 1000 *60;
			}
			IH1MessageModel newJob = new IH1MessageModel(messageToSend, personName, theName, main.getPersonNameIn());
			Lease myLease = space.write(newJob, txn, theLease);
			//display who you sent a message to 
			main.getListModel().addElement("You Sent To" + ":"+personName + " Message: " + messageToSend +"\n" + "also sent to:" + main.getPersonNameIn());

			long expirationTime = myLease.getExpiration();
			// returns the expiration time as a count of milliseconds left on the lease
			// since midnight on 1st Jan 1970.
			// the following may be useful ways of manipulating this information
			long currentTime = System.currentTimeMillis();
			long remainingMillisOnLease = expirationTime - currentTime;
			// or
			System.out.print("Lease will end at: "  + new Date(expirationTime) + "\n");
			txn.commit();
			//after message is sent, set the text area to blank
			//main.setMessageNameIn("");

		}  catch ( Exception e) {
			e.printStackTrace();
			System.out.print("Transaction failed " + e);
		}
	}
}
