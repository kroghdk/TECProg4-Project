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
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * This will run first time this servlet is runned and then load all Servlet settings inside ServletConf class.
	 */
	public void init(ServletConfig conf) throws ServletException{
		super.init(conf);
		//ServletConf.LoadSettings();
	}
	 
    public Login() {
        super();
    }
    
    /**
     * This method is when a client makes a get request to this servlet.
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HandleRequest(request, response);
	}

    /**
     * This method is when a client makes a post request to this servlet.
     */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void HandleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "text/xml; charset=UTF-8");
		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+" request URL = "+GetRequestUrl.getRequestUrl(request));
		XmlDoc xmlDoc = new XmlDoc();
		Database database = new Database(Runtime.config.GetOptionValue("jdbc_driver"),
				Runtime.config.GetOptionValue("db_url"),
				Runtime.config.GetOptionValue("db_user"),
				Runtime.config.GetOptionValue("db_pass")
				);
		if(database.hasConnection() == false){
			if(Runtime.console != null)Runtime.console.Debug("no db connection");
			xmlDoc.error("Not Able to connect to db");
			response.getWriter().append(xmlDoc.asXML());
			return;
		}
		
		//XmlDoc requestXml = new XmlDoc(GetRequestPost.getRequestPostXml(request));
		//String product_short_name = null;
		
		RequestQueryParam requestQueryParams[] = 
		{
			new RequestQueryParam("USERNAME", "", "username", Types.VARCHAR, 1, 100, false),
			new RequestQueryParam("PASSWORD", "", "password", Types.VARCHAR, 1, 100, false)
		};
		RequestQuery requestQuery = new RequestQuery(request, requestQueryParams);
		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - requestQuery= "+requestQuery.xmlDoc.asXML());
		xmlDoc = requestQuery.checkParams();
		if(requestQuery.checkParams().HasError() == true){
			response.getWriter().append(xmlDoc.asXML());
			return;
		}
		
		XmlDoc requestXmlDoc = new XmlDoc();
		Element requestXmlDocRows = requestXmlDoc.selectSingleNode("//ROWS");
		Element requestXmlDocRow = requestXmlDoc.addChild(requestXmlDocRows, "ROWS");
		requestXmlDoc.addChild(requestXmlDocRow, "PRODUCT_AUTH_KEY", Runtime.config.GetOptionValue("auth_key"));
		requestXmlDoc.addChild(requestXmlDocRow, "USERNAME", requestQuery.getParamValue("USERNAME"));
		requestXmlDoc.addChild(requestXmlDocRow, "PASSWORD",requestQuery.getParamValue("PASSWORD"));
		
		String sessionKey = null;
		try {
            
            
			sessionKey = SessionKey.GenerateSessionKey(Runtime.config.GetIntegerOptionValue("session_key_length"), Runtime.sessionController);
            
            PostMaster postMaster = new PostMaster(Runtime.console, Runtime.config.GetOptionValue("rg_api")+"/tvtrackerjs/ApiLogin");

    		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - requestXmlDoc= "+requestXmlDoc.asXML());
            if(postMaster.PostXml(requestXmlDoc, null) == false){
            	
            }
            
            XmlDoc postMasterXmlDoc = postMaster.getRespond();

    		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - postMasterXmlDoc.HasError= "+postMasterXmlDoc.HasError());
            if(postMasterXmlDoc.HasError() == true){
        		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+" - postMasterXmlDoc= "+postMasterXmlDoc.asXML());
        		response.getWriter().append(postMasterXmlDoc.asXML());
        		return;
            }
            Integer userId = Integer.parseInt(postMasterXmlDoc.getText("//USER_ID"));
            String name = postMasterXmlDoc.getText("//NAME");
            String username = postMasterXmlDoc.getText("//USERNAME");
            String email = postMasterXmlDoc.getText("//EMAIL");
            Date birthDate = null; //postMasterXmlDoc.getText("//BIRTH_DATE");
            Integer profilePicture = null;
            
            if(postMasterXmlDoc.getText("//PROFILE_PICTURE").length() > 0){
            	profilePicture = Integer.parseInt(postMasterXmlDoc.getText("//PROFILE_PICTURE"));
            }

            DSUser user = new DSUser(userId, name, username, email, birthDate, profilePicture);
            
            if(user.userExist(database, userId) == false){
        		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+". User doens't exists, creating new user");
        		if(user.createUser(database) == false){ //if(user.createUser(database, userId, username, name, email) == false){
            		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+". Failed to create new user, userId="+userId);
        			xmlDoc.error("Failed to create new user");
        			response.getWriter().append(xmlDoc.asXML());
        			return;
            	}
        		if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+". User has been created");
            }
            
            Session session = new Session(request.getRemoteHost(), user, sessionKey);
            if(Runtime.sessionController.AddSession(session, true) == false){ //if(SessionController.AddSession(session) == false){
    			xmlDoc.error("Not able to make server session.");
    			response.getWriter().append(xmlDoc.asXML());
    			return;
            }
            
            
		} catch (Exception e) {
			e.printStackTrace();
			xmlDoc.error("Server Error.");
			response.getWriter().append(xmlDoc.asXML());
			return;
		}
		database.DBCloseConnection();
		Element rows = xmlDoc.selectSingleNode("//ROWS");
		Element row = xmlDoc.addChild(rows, "ROW");
		xmlDoc.addChild(row, "SESSION_KEY", sessionKey);
		//xmlDoc.addChild(row, "USER_ID", userId);
		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+" Respond xmlDoc = "+xmlDoc.asXML());
		Cookie cookie = new Cookie("sessionKey", sessionKey);
		cookie.setSecure(true);
		cookie.setMaxAge(60 * 60 * 24); // 24h in seconds
		cookie.setPath("/");
		response.addCookie(cookie);
		
		response.getWriter().append(xmlDoc.asXML());
	}

}
