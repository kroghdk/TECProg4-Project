/**
* Copyright Riftgen 
* $Revision: 548 $
* $Date: 2018-04-16 14:45:53 +0200 (ma, 16 apr 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/tvtrackerjs/src/com/riftgen/tvtrackerjs/Login.java $
*/

package datingsite;

import goflib.RequestQuery;
import goflib.RequestQueryParam;
import goflib.session.Session;
import goflib.session.SessionKey;
import org.dom4j.Element;
import rglib.XmlDoc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;


public class ValidateSession extends DSHttpServlet {
	private static final long serialVersionUID = 1L;

	public Boolean setRequireSession(){
		return true;
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*
		if(user == null){
			Element rows = xmlDoc.selectSingleNode("//ROWS");
			Element row = xmlDoc.addChild(rows, "ROW");
			xmlDoc.addChild(row, "LOGGED_IN", "False");
			return;
		}
		*/

		Element rows = xmlDoc.selectSingleNode("//ROWS");
		Element row = xmlDoc.addChild(rows, "ROW");
		xmlDoc.addChild(row, "LOGGED_IN", "True");
		xmlDoc.addChild(row, "USER_ID", user.getUserId());
	}

}
