package models;
import javax.swing.ListModel;

import net.jini.core.entry.*;

/**
 * This class is the model class for all 
 * the message entries that are written to the javaspace
 * @author u1358629
 *
 */
public class IH1MessageModel implements Entry{
	// Variables

	public String messageSent;
	public String destinationChatClient;
	public String theName;
	public ListModel<?> jobList;
	public String image;
	public String theGroup;


	// No arg contructor
	public IH1MessageModel (){
	}

	// consturctor for profile picture
	public IH1MessageModel (String img){
		image = img;
	}

	// Constructor for sending a message
	public IH1MessageModel (String msgSent, String client,String name, String group ){
		messageSent = msgSent;
		destinationChatClient = client;
		theName = name;
		theGroup = group;

	}

	// constructor for saving message
	public IH1MessageModel (String msgSent, String name){
		messageSent = msgSent;
		theName = name;

	}



}
