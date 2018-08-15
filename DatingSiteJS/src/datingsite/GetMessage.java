/**
* Copyright Riftgen 
* $Revision: 490 $
* $Date: 2018-03-22 16:06:15 +0100 (to, 22 mar 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/aejs/src/com/riftgen/advancedentity/GetBoolean.java $
*/

package datingsite;

import goflib.RequestQuery;
import goflib.RequestQueryParam;
import goflib.SQLQuery;
import goflib.SQLQueryResult;
import rglib.XmlDoc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Types;


/**
 * Servlet implementation class GetAccount
 */
public class GetMessage extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
    	return true;
    }
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestQueryParam requestQueryParams[] =
				{
						new RequestQueryParam("MESSAGE_ID", null, "user_message_id", Types.INTEGER, 1, 11, true)
				};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		if(Runtime.console != null) Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(xmlDoc.HasError() == true){
			return;
		}

		try {
		    PreparedStatement preparedStatement = database.getConnection().prepareStatement(
		    		"SELECT * FROM user_message um, user_message_recipient umr" +
							" WHERE um.user_message_id=umr.user_message_id AND umr.user_message_id=? AND umr.user_id=?");
			preparedStatement.setInt(1, requestQuery.getIntParamValue("MESSAGE_ID"));
			preparedStatement.setInt(2, user.getUserId());
		    
		    SQLQueryResult sqlQueryResult[] = {
		    		new SQLQueryResult("RECEIVER", null, "receiver", "getInt"),
					new SQLQueryResult("SENDER", null, "sender", "getString"),
					new SQLQueryResult("CREATED", null, "created", "getString")
		    		};
		    
		    SQLQuery sqlQuery = new SQLQuery(database, Runtime.console, preparedStatement, sqlQueryResult);
		    XmlDoc rowsDoc = sqlQuery.runSelectStatement("ROWS", "ROW", -1);
		    xmlDoc.appendChildNode(xmlDoc.selectSingleNode("//ROWS"), rowsDoc.selectSingleNode("//ROWS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
