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
						new RequestQueryParam("ME", null, "user_id", Types.INTEGER, 1, 11, true),
						new RequestQueryParam("USER_ID", null, "liked_user_id", Types.INTEGER, 1, 11, true),
						new RequestQueryParam("LIKE", null, "liked", Types.INTEGER, 1, 11, false)
				};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		requestQuery.getParam("ME").setValue(user.getUserId().toString());
		if(Runtime.console != null) Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(xmlDoc.HasError() == true){
			return;
		}
		SQLQuery sqlQuery = new SQLQuery(database, Runtime.console);
		xmlDoc = sqlQuery.runAjourStatement("user_like", requestQuery);
	}

}
