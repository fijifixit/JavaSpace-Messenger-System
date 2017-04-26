package controllers;

import java.rmi.RemoteException;

import models.AccountModel1;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.*;
import utils.SpaceUtils;

/**
 * This class is the account controller
 * it handles all the account entries to the javaspace
 * it has the register, login and cancel methods
 * @author u1358629
 *
 */
public class AccountController {


	private String username;
	private String password;
	private JavaSpace05 space;
	private TransactionManager mgr;
	MessageController msgController;


	public AccountController(String username, String password)
	{	//find javaspace
		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		//set username and password parameters
		this.username = username;
		this.password = password;

	}


	/**
	 * This is the register method. THe user can register with a username and password
	 * If the username exists, it will give an error
	 */
	public void register(){
		//create template
		AccountModel1 template = new AccountModel1(username, password);
		try {
			//readIfExists the template with the entries
			if(space.readIfExists(template, null, JavaSpace.NO_WAIT) != null){
				//error if username exists
				System.out.println("Username  " + username + "already exists");

			}else {
				//else write the username and password to the space
				space.write(template, null, Lease.FOREVER);
				System.out.println("Your account has been registered");
			}

		} catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public boolean login(){
		//create a new template
		AccountModel1 template = new AccountModel1(username, password);
		//set account to null
		AccountModel1 account = null;
		boolean login = false;
		try {
			//readIfExists the templates 
			account= (AccountModel1) space.readIfExists(template, null, JavaSpace.NO_WAIT);
			//if account equals nothing
			if(account== null){
				//error
				System.out.println("Username does not exist");

			} else{
				System.out.println("logged in");
				//set login to true
				login = true;

			}

		} catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return login;
	}
}
