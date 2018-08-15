/**
* Copyright Riftgen 
* $Revision: 490 $
* $Date: 2018-03-22 16:06:15 +0100 (to, 22 mar 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/aejs/src/com/riftgen/advancedentity/GetBoolean.java $
*/

package datingsite;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rglib.*;
import rglib.GetRequestUrl;
import goflib.*;
import goflib.session.Session;



/**
 * Servlet implementation class GetAccount
 */
public class Test extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	/*
	public Boolean setRequireSession(){
    	return true;
    }
    */
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		    PreparedStatement preparedStatement = database.getConnection().prepareStatement("SELECT * FROM user");
		    
		    SQLQueryResult sqlQueryResult[] = {
		    		new SQLQueryResult("USER_ID", null, "user_id", "getInt"),
					new SQLQueryResult("USERNAME", null, "username", "getString"),
					new SQLQueryResult("FIRST_NAME", null, "first_name", "getString"),
					new SQLQueryResult("LAST_NAME", null, "last_name", "getString"),
					new SQLQueryResult("EMAIL", null, "email", "getString")
		    		};
		    
		    SQLQuery sqlQuery = new SQLQuery(database, Runtime.console, preparedStatement, sqlQueryResult);
		    XmlDoc rowsDoc = sqlQuery.runSelectStatement("ROWS", "ROW", -1);
		    xmlDoc.appendChildNode(xmlDoc.selectSingleNode("//ROWS"), rowsDoc.selectSingleNode("//ROWS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
