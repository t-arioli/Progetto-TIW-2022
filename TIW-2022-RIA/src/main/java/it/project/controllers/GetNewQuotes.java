package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.project.utils.ConnectionHandler;
import it.project.bean.Quote;
import it.project.bean.User;
import it.project.dao.QuoteDAO;

/**
 * If the session is from a client, return the Client HTML Home page, with the
 * own list of quotes. If the session is from a employee, return the Employee
 * HTML home page, with the list of quotes previously handled, plus the list of
 * quotes still unassigned. The quotes are saved in the session in the attribute
 * 'userQuotes' while the unassigned quotes in the Employee session are saved in
 * the 'newQuotes' attribute.
 */
@WebServlet("/GetNewQuotes")
public class GetNewQuotes extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetNewQuotes() {
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
		HttpSession session = request.getSession();
		// get own preventives
		User user = (User) session.getAttribute("user");
		List<Quote> newQuotes = null;
		try {
			QuoteDAO qDao = new QuoteDAO(connection);
			newQuotes = qDao.getUnassignedQuotes(user);
			String json = new Gson().toJson(newQuotes);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			System.out.println(json);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			return;
		} 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}

}
