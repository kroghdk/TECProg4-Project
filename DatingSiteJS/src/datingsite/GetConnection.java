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
public class GetConnection extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
    	return true;
    }
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
		    PreparedStatement preparedStatement = database.getConnection().prepareStatement(
		    		"SELECT DISTINCT u.* FROM user u" +
							" LEFT JOIN user_message_recipient umr ON (u.user_id = umr.user_id)" +
							" LEFT JOIN user_message_recipient umr2 ON (umr.user_message_id = umr2.user_message_id)" +
							" LEFT JOIN user_like ul ON (u.user_id=ul.liked_user_id)" +
							" LEFT JOIN user_like ul2 ON (ul.user_id=ul2.liked_user_id)" +
							" WHERE (ul.liked_user_id=ul2.user_id AND ul.user_id=?) OR (umr2.user_id <> umr.user_id) AND umr2.user_id=?;");
			preparedStatement.setInt(1, user.getUserId());//preparedStatement.setInt(1, 1);
			preparedStatement.setInt(2, user.getUserId());//preparedStatement.setInt(1, 1);
		    
		    SQLQueryResult sqlQueryResult[] = {
					new SQLQueryResult("USER_ID", null, "user_id", "getInt"),
					new SQLQueryResult("USERNAME", null, "username", "getString")
		    		};
		    
		    SQLQuery sqlQuery = new SQLQuery(database, Runtime.console, preparedStatement, sqlQueryResult);
		    XmlDoc rowsDoc = sqlQuery.runSelectStatement("ROWS", "ROW", -1);
		    xmlDoc.appendChildNode(xmlDoc.selectSingleNode("//ROWS"), rowsDoc.selectSingleNode("//ROWS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
