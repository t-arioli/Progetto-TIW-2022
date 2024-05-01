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
import it.project.dao.UserDAO;
import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * Checks the request parameters, if they're correct inserts the new client in
 * the DB then return to Login HTML page
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public Register() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
		// Thymeleaf initialization
		this.templateEngine = TemplateHandler.setTemplate(getServletContext());
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
		String answer = null;
		String path = URLHandler.LOGIN;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		try {
			username = request.getParameter("username");
			password = request.getParameter("password");
			passwordConfirm = request.getParameter("passwordConfirm");
			if (username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || username == null
					|| password == null || passwordConfirm == null)
				throw new InputMismatchException("Parametri mancanti");
			if (!password.equals(passwordConfirm))
				throw new InputMismatchException("Le password non corrispondono");
			UserDAO uDao = new UserDAO(this.connection);
			if (uDao.checkCredentials(username, password) == null) {
				code = uDao.register(username, password);
				if (code != 0)
					answer = "Registrazione avvenuta con successo!";
				else
					throw new SQLException();
			}

		} catch (InputMismatchException | SQLException e) {
			System.out.println(e.getMessage());
			answer = "Errore durante la registrazione";
		} finally {
			ctx.setVariable("signupMessage", answer);
			templateEngine.process(path, ctx, response.getWriter());
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
