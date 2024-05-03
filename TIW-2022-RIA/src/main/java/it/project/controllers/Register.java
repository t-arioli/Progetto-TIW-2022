package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.project.dao.UserDAO;
import it.project.utils.ConnectionHandler;

/**
 * Checks the request parameters, if they're correct inserts the new client in
 * the DB then return to Login HTML page
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public Register() {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		String passwordConfirm = null;
		int code;
		
		username = request.getParameter("username");
		password = request.getParameter("password");
		passwordConfirm = request.getParameter("passwordConfirm");
		if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || username == null
				|| password == null || passwordConfirm == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		
		if (!password.equals(passwordConfirm)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Passwords does not match each other");
			return;
		}
			
		try {
			UserDAO uDao = new UserDAO(this.connection);
			if (!uDao.checkUsername(username)) {
				code = uDao.register(username, password);
				if (code != 0) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().println("Success!");
				}
				else {
					throw new SQLException();
				}
				
			}
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			return;
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
