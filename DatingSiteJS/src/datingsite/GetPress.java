/**
* Copyright Riftgen 
* $Revision: 548 $
* $Date: 2018-04-16 14:45:53 +0200 (ma, 16 apr 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/tvtrackerjs/src/com/riftgen/tvtrackerjs/GetPress.java $
*/

package datingsite;

import java.io.IOException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;
import org.eclipse.jdt.internal.compiler.ast.Argument;

import rglib.*;
import goflib.RequestQuery;
import goflib.RequestQueryParam;
import goflib.session.Session;
import goflib.site.*;

/**
 * Servlet implementation class GetAccount
 */
public class GetPress extends DSHttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		RequestQueryParam requestQueryParams[] = 
		{
			new RequestQueryParam("USERNAME", "", "username", Types.VARCHAR, 1, 100, false),
			new RequestQueryParam("PASSWORD", "", "password", Types.VARCHAR, 1, 100, false)
		};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		
		//XmlDoc requestXml = new XmlDoc(GetRequestPost.getRequestPostXml(request));
		String sessionKey = null;
		sessionKey = Cookie.GetCookie(Runtime.console, request, "sessionKey");
		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - sessionKey = "+sessionKey);
		Session session = Runtime.sessionController.getSession(sessionKey); //new Session(sessionKey);
		String getPress = request.getParameter("press");
		response.setContentType("text/xml");
		Press pressFound = null;
		Site siteFound = Runtime.site;
		/*
		String hostName = request.getServerName(); //request.getScheme() + "://" + request.getServerName();
		for (Site site : Runtime.sites) {
			if(site.getSiteHostName().equals(hostName))	siteFound = site;
		}
		*/
		if(siteFound == null){
			//if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+"-  hostname= "+hostName);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if(getPress == null | getPress == ""){
			getPress = siteFound.getSiteMainPress();
		}
		for (Press press : siteFound.getPresses()) {
			if(press.getShortName().equals(getPress))	pressFound = press;
		}
		if(pressFound == null){
			pressFound = siteFound.getPress("p_not_found");
		}
		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+" request method = "+request.getMethod());
		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+" request URL = "+GetRequestUrl.getRequestUrl(request));

		if(pressFound.requireLogin() == true & session == null){
			pressFound = siteFound.getPress("n_sign_in");
			if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+", an user is trying to access menu with login required.");
		}else if(pressFound.requireLogin() == true & session != null){
			/*
			if(pressFound.requireAdmin() == true & session.user.isAdmin(database) == false){
				pressFound = siteFound.getPress("p_not_found");
				if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+", "+session.getUser().getUsername()+" is trying to access admin menu.");
			}
			*/
		}

    	Map arguments = new HashMap();
    	//arguments.put( "href", pressFound.getMainFile());
    	arguments.put( "type", "text/xsl" );
    	//arguments.put( "href", "/xsl/main.xsl");
    	arguments.put( "href", pressFound.getMainFile());
		xmlDoc = new XmlDoc("xml-stylesheet", arguments);
		Element header = xmlDoc.selectSingleNode("//HEADER");
		//xmlDoc.getDocument().addProcessingInstruction("xml:stylesheet", arguments);

		xmlDoc.addChild(header, "RG_URL", Runtime.config.GetOptionValue("rg_url"));
		if(session != null){
			Integer userId = session.GetUserId();
			Element user = xmlDoc.addChild(header, "USER");
			xmlDoc.addChild(user, "USER_ID", userId);
			xmlDoc.addChild(user, "USERNAME", session.getUser().getUsername());
			xmlDoc.addChild(user, "PROFILE_PICTURE", session.getUser().getProfilePicture());
			xmlDoc.addChild(header, "LOGGED_IN", "true");
			//xmlDoc.addChild(header, "STRIPE_PUBLIC_KEY", ServletConf.stripePublicKey);
			//if(session.getUser().isAdmin(database) == true) xmlDoc.addChild(header, "IS_ADMIN", "true");
		}
		//xmlDoc.addChild(header, "XLS_URL", pressFound.getMainFile());
		
		database.DBCloseConnection();
		Element rows = xmlDoc.selectSingleNode("//ROWS");
		Element row = xmlDoc.addChild(rows, "ROW");
	}

}
