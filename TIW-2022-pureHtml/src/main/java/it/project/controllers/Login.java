package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;
import it.project.bean.User;
import it.project.dao.UserDAO;
/**
 * Return the Login HTML page and, if the HTTP request contains the credentials, redirect to Home
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    public Login() {
        super();
    }
    
    public void init() throws ServletException{
    	this.connection  = ConnectionHandler.getConnection(getServletContext());
    	//Thymeleaf initialization
    	this.templateEngine = TemplateHandler.setTemplate(getServletContext());
    }
    
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost(request, response);
    }

    /**
     * Analyze the request parameters and redirect to Home, otherwise reload the HTML Login page
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		String path = URLHandler.LOGIN;
		UserDAO uDao = new UserDAO(connection);
		User user = null;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		try {
			username = request.getParameter("username");
			password = request.getParameter("password");
			if(username.isEmpty() || password.isEmpty() || username == null || password  == null)
				throw new NullPointerException();
			//check the credentials in the DB
			user = uDao.checkCredentials(username, password);
			if(user == null)
				throw new InputMismatchException("Username o password errata");
			request.getSession().setAttribute("user", user);
			//ctx.setVariable("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}catch(NullPointerException e) {
			//this case is for URL mismatch
			templateEngine.process(path, ctx, response.getWriter());
		}catch(InputMismatchException e) {
			ctx.setVariable("errorMessage", e.getMessage());
			templateEngine.process(path, ctx, response.getWriter());
		}catch(SQLException e) {
			ctx.setVariable("errorMessage", "Errore di sistema");
			templateEngine.process(path, ctx, response.getWriter());
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
	}

}
