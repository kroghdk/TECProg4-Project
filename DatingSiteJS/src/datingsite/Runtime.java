/**
* Copyright Riftgen 
* $Revision: 548 $
* $Date: 2018-04-16 14:45:53 +0200 (ma, 16 apr 2018) $
* $Author: rkb $
* $HeadURL: http://svn.riftgen.com/scm/svn/Riftgen/trunk/lbs/src/program/Servlets/tvtrackerjs/src/com/riftgen/tvtrackerjs/Runtime.java $
*/

package datingsite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.jasper.runtime.ProtectedFunctionMapper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

import goflib.session.Session;
import goflib.session.SessionController;
import goflib.site.Press;
import goflib.site.Site;
import rglib.PostMaster;
import rglib.Time;
import rglib.XmlDoc;
import rglib.general.Config;
import rglib.general.ConsoleLog;

public class Runtime implements ServletContextListener {

	
	public static Config config;
	public static ConsoleLog console;
	public static SessionController sessionController;
	public static Site site;
	//protected ScheduledExecutorService ses = null;
	protected ScheduledExecutorService processingService;
	//protected int processingPoolSize = 2;
	//protected long processingInitialDelay = 10;
	//protected long processingInterval = 120;
	
	@Override
	  public void contextDestroyed(ServletContextEvent arg0) {
	     final String strMethod = getClass().getName() + ".contextDestroyed";

	      //ConsoleLog.Log("{}: Called", strMethod);
	      
	      if (processingService != null)
	      {
	         processingService.shutdownNow();

	         try
	         {
	            boolean terminated = processingService.awaitTermination(5, TimeUnit.SECONDS);
	            
	            if (terminated){
	            	//ConsoleLog.Log("{}: Processing threads terminated", strMethod);
	            }else{
	            	//ConsoleLog.Log("{}: Processing threads not terminated", strMethod);
	            }
	         }
	         catch (InterruptedException ie)
	         {
	        	 //ConsoleLog.Log("{}: Exception waiting for processing threads to terminate", strMethod, ie);
	         }
	      }
		//ses.shutdownNow(); //ses.shutdown();
		sessionController.ClearSessions();
	  }

	  @Override
	  public void contextInitialized(ServletContextEvent arg0) {
		  
		  // do all the tasks that you need to perform just after the server starts

		  //Notification that the web application initialization process is starting
		  
		  //System.out.println(this.getClass().getName()+" servlet is starting.");
		  
		  Runtime.config = new Config("/srv/tvtrackerjs/conf/config.xml", Config.ConfigTypes.XML);
		  Runtime.console = new ConsoleLog(config);

		  if(Runtime.console != null)Runtime.console.Debug("configOptions:" + Runtime.config.getConfitOptions().size());
		  /*
		  for (ConfigOption configOption : Runtime.config.getConfitOptions()) {
			if(Runtime.console != null)Runtime.console.Debug("Config.LoadConfig, " + configOption.getName() + "=" + configOption.getValue());
		  }
		  */
		  
		  Runtime.sessionController = new SessionController();
		  
		  if(Runtime.console != null)console.Print(this.getClass().getName()+" server has been started.");
		  
		  if(Runtime.config.GetIntegerOptionValue("process_pool_size") > 0 & Runtime.config.GetIntegerOptionValue("process_interval") > 0){
			  processingService = Executors.newScheduledThreadPool(Runtime.config.GetIntegerOptionValue("process_pool_size"));
	      	processingService.scheduleAtFixedRate(new ProcessingExecutor(), Runtime.config.GetIntegerOptionValue("process_init_delay"), Runtime.config.GetIntegerOptionValue("process_interval"), TimeUnit.SECONDS);
		  }else{
			  if(Runtime.console != null)console.Print(this.getClass().getName()+".contextInitialized - process config error");
		  }
		  

		  try{
			  XmlDoc requestXmlDoc = new XmlDoc();
			  Element requestXmlDocRows = requestXmlDoc.selectSingleNode("//ROWS");
			  Element requestXmlDocRow = requestXmlDoc.addChild(requestXmlDocRows, "ROWS");
			  requestXmlDoc.addChild(requestXmlDocRow, "PRODUCT_AUTH_KEY", Runtime.config.GetOptionValue("auth_key"));
			  PostMaster postMaster = new PostMaster(Runtime.console, Runtime.config.GetOptionValue("rg_api")+"/riftgenjs/GetSite");

			  if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - requestXmlDoc= "+requestXmlDoc.asXML());
			  if(postMaster.PostXml(requestXmlDoc, null) == false){
			  }
          
			  XmlDoc postMasterXmlDoc = postMaster.getRespond();

			  if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+" - postMasterXmlDoc.HasError= "+postMasterXmlDoc.HasError());
			  if(postMasterXmlDoc.HasError() == true){
  				
			  }
			  Element siteElement = postMasterXmlDoc.selectSingleNode("//PAGE/ROWS/ROW/SITE");

			  Element siteIdElement = postMasterXmlDoc.selectSingleNode(siteElement, "SITE_ID");
			  Integer siteId = Integer.parseInt(postMasterXmlDoc.getText(siteIdElement));
			  
			  Element siteNameElement = postMasterXmlDoc.selectSingleNode(siteElement, "SITE_NAME");
			  String siteName = postMasterXmlDoc.getText(siteNameElement);
			  
			  Element siteShortNameElement = postMasterXmlDoc.selectSingleNode(siteElement, "SITE_SHORT_NAME");
			  String siteShortName = postMasterXmlDoc.getText(siteShortNameElement);
			  
			  Element siteHostElement = postMasterXmlDoc.selectSingleNode(siteElement, "SITE_HOST");
			  String siteHost = postMasterXmlDoc.getText(siteHostElement);
			  
			  List<Press> presses = new ArrayList<Press>();
			  for (Node sitePressNode : postMasterXmlDoc.selectNodes("//PAGE/ROWS/ROW/SITE/SITE_PRESS")) {
				  Element pressNameElement = postMasterXmlDoc.selectSingleNode((Element)sitePressNode, "PRESS_NAME");
				  String pressName = postMasterXmlDoc.getText(pressNameElement);
				  
				  Element pressShortNameElement = postMasterXmlDoc.selectSingleNode((Element)sitePressNode, "PRESS_SHORT_NAME");
				  String pressShortName = postMasterXmlDoc.getText(pressShortNameElement);
				  
				  Element pressMailFileElement = postMasterXmlDoc.selectSingleNode((Element)sitePressNode, "PRESS_MAIN_FILE");
				  String pressMailFile = postMasterXmlDoc.getText(pressMailFileElement);
				  
				  Element pressRequireLoginElement = postMasterXmlDoc.selectSingleNode((Element)sitePressNode, "PRESS_REQUIRE_LOGIN");
				  Boolean pressRequireLogin = Boolean.parseBoolean(postMasterXmlDoc.getText(pressRequireLoginElement));
				  
				  Element pressRequireAdminElement = postMasterXmlDoc.selectSingleNode((Element)sitePressNode, "PRESS_REQUIRE_ADMIN");
				  Boolean pressRequireAdmin = Boolean.parseBoolean(postMasterXmlDoc.getText(pressMailFileElement));
				  
				  Press newSitePress = new Press(pressName, pressShortName, pressMailFile, pressRequireLogin, pressRequireAdmin);
				  presses.add(newSitePress);
			  }
			  Runtime.site = new Site(siteId, siteName, siteHost, siteShortName, null, null, presses);
          }catch(Exception e){
        	  e.printStackTrace();
          }
	  }
	  
	  public void CheckSessionController(){
		  //System.out.println(this.getClass().getName()+" running SessionController.");
		  if(Runtime.console != null)Runtime.console.Print("+"+this.getClass().getName());
		  if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+".CheckSessionController - Current Sesssions = "+Runtime.sessionController.getSessions().size());
		  if(Runtime.sessionController.getSessions().size() == 0){
			  if(console != null)console.Print("-"+this.getClass().getName());
			  return;
		  }
		  try{
			  List<Session> sessions = new ArrayList<Session>(Runtime.sessionController.getSessions());
			  if(sessions.size() == 0){
				  if(Runtime.console != null)Runtime.console.Print("-"+this.getClass().getName());
				  return;
			  }
			  for (Session session : sessions) {
				//ConsoleLog.Debug(this.getClass().getName()+" session lastAction = "+session.lastAction.toString());
				    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				    Date sessionDate = format.parse(session.getLastAction().toString());
				    Date serverDate = format.parse(Time.getCurrentTimeStamp());
				    long difference = serverDate.getTime() - sessionDate.getTime();
				    if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+".CheckSessionController session sessionDate = "+sessionDate.getTime());
				    if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+".CheckSessionController serverDate = "+serverDate.getTime());
				    if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+".CheckSessionController CheckSessionController - difference = "+difference);
				    if(Runtime.console != null)Runtime.console.Debug(this.getClass().getName()+".CheckSessionController CheckSessionController - sessionTimeOut value = "+config.GetIntegerOptionValue("session_timeout"));
				    //difference > session_timeout (60*60000)
				    if(difference > config.GetIntegerOptionValue("session_timeout")){
				    	Integer userId = session.GetUserId();
				    	String sessionKey = session.getSessionKey();
				    	if(Runtime.sessionController.RemoveSession(session) == true){
				    		if(Runtime.console != null)Runtime.console.Print(this.getClass().getName()+".CheckSessionController - Removed Session with userId="
				    			+userId+" and sessionKey="+sessionKey+", due to being innactive");
				    	}
				    }
			  }
		  }catch (Exception e) {
			  e.printStackTrace();
		  }
		  if(Runtime.console != null)Runtime.console.Print("-"+this.getClass().getName());
	  }
	  
	  /** Processing executor class.
	  */

	  public class ProcessingExecutor
	     implements Runnable
	  {
	     @Override
	     public void run()
	     {
	    	 try{
	    	  CheckSessionController();
	    	 }catch (Exception e) {
				e.printStackTrace();
		    	throw new RuntimeException(e);
			}
	     }
	  }
}

