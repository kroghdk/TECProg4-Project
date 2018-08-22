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
public class FindUsers extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
    	return true;
    }
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestQueryParam requestQueryParams[] =
				{
						new RequestQueryParam("ME", null, "user_id", Types.INTEGER, 1, 11, true)
				};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		if(Runtime.console != null) Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(xmlDoc.HasError() == true){
			return;
		}

		try {
		    PreparedStatement preparedStatement = null;

		    if(true){
				preparedStatement = null; database.getConnection().prepareStatement("");
				preparedStatement.setInt(1, user.getUserId());
			}else{
		    	xmlDoc.error("Missing search criterials");
		    	return;
			}
		    
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
