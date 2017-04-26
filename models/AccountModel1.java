package models;
import net.jini.core.entry.*;

/**
 * This is the accountr model class
 * It is used for the account objects that are written to the javaspace
 * @author u1358629
 *
 */
public class AccountModel1 implements Entry{

	public String username;
	public String password;
	public String status;

	public AccountModel1(){

	}

	public AccountModel1(String username){

		this.username = username;
	}

	public AccountModel1(String username, String password){

		this.username = username;
		this.password = password;
	}

}
