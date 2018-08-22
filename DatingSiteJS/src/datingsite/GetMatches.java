/**
* Copyright Riftgen 
* $Revision: 490 $
* $Date: 2018-03-22 16:06:15 +0100 (to, 22 mar 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/aejs/src/com/riftgen/advancedentity/GetBoolean.java $
*/

package datingsite;

import goflib.SQLQuery;
import goflib.SQLQueryResult;
import rglib.XmlDoc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;


/**
 * Servlet implementation class GetAccount
 */
public class GetMatches extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
    	return true;
    }
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
		    PreparedStatement preparedStatement = database.getConnection().prepareStatement(
		    		"SELECT DISTINCT u.* FROM user u" +
							" WHERE u.user_id NOT IN (SELECT liked_user_id FROM user_like WHERE user_id=?) AND u.gender=(SELECT find_gender FROM user WHERE user_id=?)" +
                            " AND u.find_gender=(SELECT gender FROM user WHERE user_id=?) AND u.user_id<>?;");
			preparedStatement.setInt(1, user.getUserId());
            preparedStatement.setInt(2, user.getUserId());
            preparedStatement.setInt(3, user.getUserId());
            preparedStatement.setInt(4, user.getUserId());
		    
		    SQLQueryResult sqlQueryResult[] = {
					new SQLQueryResult("USER_ID", null, "user_id", "getInt"),
					new SQLQueryResult("USERNAME", null, "username", "getString"),
					new SQLQueryResult("FIRST_NAME", null, "first_name", "getString"),
					new SQLQueryResult("LAST_NAME", null, "last_name", "getString"),
					new SQLQueryResult("EMAIL", null, "email", "getString"),
					new SQLQueryResult("DESCRIPTION", null, "description", "getString"),
					new SQLQueryResult("AGE", null, "age", "getInt"),
					new SQLQueryResult("PROFILE_PIC", null, "profile_pic", "getString"),
		    		};
		    
		    SQLQuery sqlQuery = new SQLQuery(database, Runtime.console, preparedStatement, sqlQueryResult);
		    XmlDoc rowsDoc = sqlQuery.runSelectStatement("ROWS", "ROW", -1);
		    xmlDoc.appendChildNode(xmlDoc.selectSingleNode("//ROWS"), rowsDoc.selectSingleNode("//ROWS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
