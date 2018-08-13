package datingsite;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import goflib.GOFHttpServlet;

public class DSHttpServlet extends GOFHttpServlet{
	private static final long serialVersionUID = 1L;

	public DSUser user = null;
	
	public void init() throws ServletException{
		config = Runtime.config;
		console = Runtime.console;
		sessionController = Runtime.sessionController;
	}
	
    public Boolean setRequireSession(){
    	return false;
    }
	
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		if(session != null){
			user = new DSUser(session.GetUserId());
		}
		doRequest(request, response);
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		this.xmlDoc.error("Missing server request action");
	}
	
}
