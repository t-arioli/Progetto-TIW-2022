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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import it.project.bean.User;
import it.project.dao.QuoteDAO;
import it.project.utils.ConnectionHandler;

/**
 * Adds the price to a quote in the DB from a Employee session's request. If the
 * DB update is successful, it redirects to the Employee HTML Home page, else
 * redirects to the QuotePrice HTML page (user will remain stuck in the same
 * HTML page)
 */
@WebServlet("/AddPrice/*")
@MultipartConfig
public class AddPrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddPrice() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User admin = (User) session.getAttribute("user");
		float price = -1;
		int quote;
		try {
			price = Float.parseFloat(StringEscapeUtils.escapeJava(request.getParameter("price")));
			quote = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("id")));
			QuoteDAO qDao = new QuoteDAO(connection);
			qDao.addPrice(admin, quote, price);
			response.setStatus(HttpServletResponse.SC_OK);

		} catch (SQLException | NullPointerException | NumberFormatException e) {
			System.out.println(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setCharacterEncoding("utf-8");
			response.getWriter().println("BAD REQUEST");
			response.setContentType("text/html");
			return;
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}
}
