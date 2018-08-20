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
import org.dom4j.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Types;


/**
 * Servlet implementation class GetAccount
 */
public class AjourLike extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
    	return true;
    }
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestQueryParam requestQueryParams[] =
				{
						new RequestQueryParam("USER_ID", null, "user_id", Types.INTEGER, 1, 11, false),
						new RequestQueryParam("LIKE", null, "like", Types.INTEGER, 1, 11, false)
				};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		if(Runtime.console != null) Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(xmlDoc.HasError() == true){
			return;
		}

		try {
		    PreparedStatement preparedStatement = database.getConnection().prepareStatement(
		    		"CALL sendmessage(?, ?, ?, @user_message_id);");
		    preparedStatement.setInt(1, user.getUserId());
			preparedStatement.setInt(2, requestQuery.getIntParamValue("MESSAGE_TO"));
			preparedStatement.setString(3, requestQuery.getParamValue("MESSAGE"));
			preparedStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
			xmlDoc.error("Failed to send message");
			return;
		}
		Element rows = xmlDoc.selectSingleNode("//ROWS");
		Element row = xmlDoc.addChild(rows, "ROW");
		xmlDoc.addChild(row, "RESPOND", "SUCCESS");
	}

}
