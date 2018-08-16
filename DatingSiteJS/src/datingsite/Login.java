/**
* Copyright Riftgen 
* $Revision: 548 $
* $Date: 2018-04-16 14:45:53 +0200 (ma, 16 apr 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/tvtrackerjs/src/com/riftgen/tvtrackerjs/Login.java $
*/

package datingsite;

import rglib.*;
import rglib.GetRequestUrl;
import goflib.*;
import goflib.session.Session;
import goflib.session.SessionKey;

import java.sql.*;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;


//@WebServlet("/Login")
public class Login extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RequestQueryParam requestQueryParams[] = 
		{
			new RequestQueryParam("USERNAME", "", "username", Types.VARCHAR, 1, 100, false),
			new RequestQueryParam("PASSWORD", "", "password", Types.VARCHAR, 1, 100, false)
		};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		if(Runtime.console != null) Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(xmlDoc.HasError() == true){
			return;
		}

		String sessionKey = null;
		try {
			PreparedStatement preparedStatement = database.getConnection().prepareStatement(
					"SELECT * FROM user WHERE username=? AND password=?");
			preparedStatement.setString(1, requestQuery.getParamValue("USERNAME"));
			preparedStatement.setString(2, requestQuery.getParamValue("PASSWORD"));

			ResultSet resultSet = preparedStatement.executeQuery();
			if(database.getRowCount(resultSet) == 0){
				xmlDoc.error("Invalid username or password.");
				if(Runtime.console != null) Runtime.console.Print("No user found");
				return;
			}

			Integer userId = null;

			while (resultSet.next()) {
				//userId = Database.getResultInt(resultSet, "u.user_id");
				userId = resultSet.getInt("user_id");
			}
			user = new DSUser(userId);

			if(Runtime.sessionController.SessionExists(user.getUserId()) == true){
				Session oldSession = Runtime.sessionController.getSession(user.getUserId());
				Runtime.sessionController.RemoveSession(oldSession);
			}
			sessionKey = SessionKey.GenerateSessionKey(Runtime.config.GetIntegerOptionValue("session_key_length"), Runtime.sessionController);
            
            Session session = new Session(request.getRemoteHost(), user, sessionKey);
            if(Runtime.sessionController.AddSession(session, true) == false){ //if(SessionController.AddSession(session) == false){
    			xmlDoc.error("Not able to make server session.");
    			return;
            }
            
            
		} catch (Exception e) {
			e.printStackTrace();
			xmlDoc.error("Server Error.");
			return;
		}
		xmlDoc = new XmlDoc();
		Element rows = xmlDoc.selectSingleNode("//ROWS");
		Element row = xmlDoc.addChild(rows, "ROW");
		xmlDoc.addChild(row, "SESSION_KEY", sessionKey);
		xmlDoc.addChild(row, "USER_ID", user.getUserId());
	}

}
