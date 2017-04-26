package models;
import net.jini.core.entry.*;

/**
 * This class is the model class for all 
 * the message entries that are written to the javaspace
 * @author u1358629
 *
 */
public class ReadModel1 implements Entry{
	// Variables

	public String msgRead;
	public String destinationChatClient;
	public String theName;



	// No arg contructor
	public ReadModel1 (){
	}

	// No arg contructor
	public ReadModel1 ( String msgRead, String client, String name){
		destinationChatClient = client;
		theName = name;
	}





}
