package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;
import it.project.bean.Quote;
import it.project.bean.User;
import it.project.dao.QuoteDAO;
/**
 * If the session is from a client, return the Client HTML Home page, with the own list of quotes.
 * If the session is from a employee, return the Employee HTML home page, with the list of quotes previously handled,
 * plus the list of quotes still unassigned. The quotes are saved in the session in the attribute 'userQuotes'
 * while the unassigned quotes in the Employee session are saved in the 'newQuotes' attribute.
 */
@WebServlet("/Home")
public class GetHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    public GetHomePage() {
        super();
    }
    
    public void init() throws ServletException {
    	this.connection = ConnectionHandler.getConnection(getServletContext());
    	//thymeleaf init
    	this.templateEngine = TemplateHandler.setTemplate(getServletContext());
	}
    
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String path = "";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		//get own preventives
		User user = (User) session.getAttribute("user");
		List<Quote> userQuotes = null;
		try {
			QuoteDAO qDao = new QuoteDAO(connection);
			userQuotes = new ArrayList<>();
			userQuotes = qDao.getQuotesByUser(user);
			session.setAttribute("userQuotes", userQuotes);
			//get html page
			if(user.isAdmin()) {
				//get new quotes
				List<Quote> newQuotes = new ArrayList<>();
				newQuotes = qDao.getUnassignedQuotes(user);
				session.setAttribute("newQuotes", newQuotes);	
			}		
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			if(user.isAdmin())
				path = URLHandler.EMPLOYEE_HOME;
			else
				path = URLHandler.CLIENT_HOME;	
			templateEngine.process(path, ctx, response.getWriter());
		}
	}
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			//e.printStackTrace();
		}
	}

}
