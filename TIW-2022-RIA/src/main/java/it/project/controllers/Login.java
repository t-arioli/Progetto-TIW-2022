package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;

import it.project.utils.ConnectionHandler;
import it.project.bean.User;
import it.project.dao.UserDAO;

/**
 * Return the Login HTML page and, if the HTTP request contains the credentials,
 * redirect to Home
 */
@WebServlet("/Login")
@MultipartConfig
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Login() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Analyze the request parameters and redirect to Home, otherwise reload the
	 * HTML Login page
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		UserDAO uDao = new UserDAO(connection);
		User user = null;
		//gets parameters
		try {
			//username = request.getParameter("username");
			//password = request.getParameter("password");
			 username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			 password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (username.isEmpty() || password.isEmpty() || username == null || password == null) {
				throw new NullPointerException();
			}
		}catch(NullPointerException e) {
			//System.out.println(e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		// check the credentials in the DB
		try {
			user = uDao.checkCredentials(username, password);
		}
		catch(SQLException e) {
			//System.out.println(e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			return;
		}
		//check user exists
		if (user == null) {
			//System.out.println("USER NULL");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Wrong username or password");
			return;
		}else {
			String json = new Gson().toJson(user);
			request.getSession().setAttribute("user", user);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			//System.out.println(json);
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}

}
