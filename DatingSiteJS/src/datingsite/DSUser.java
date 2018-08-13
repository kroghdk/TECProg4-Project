package datingsite;

import java.sql.Date;

import goflib.Database;
import goflib.user.*;

public class DSUser extends User{

	public DSUser(Integer userId) {
		super(userId);
		// TODO Auto-generated constructor stub
	}

	public DSUser(Integer userId, String name, String username, String email, Date birthDate, Integer profilePicture) {
		super(userId, name, username, email, birthDate, profilePicture);
		// TODO Auto-generated constructor stub
	}
	
	public Boolean userExist(Database database, Integer userId){
		return false;
	}
	
	public Boolean createUser(Database database){
		return false;
	}

}
