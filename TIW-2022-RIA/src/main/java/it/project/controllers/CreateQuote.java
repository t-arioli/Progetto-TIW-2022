package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import it.project.bean.Option;
import it.project.bean.Product;
import it.project.bean.Quote;
import it.project.bean.User;
import it.project.dao.QuoteDAO;
import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * Gets all the details from the session, encapsulates them in a new Quote and
 * inserts in the DB
 */
@WebServlet("/CreateQuote")
public class CreateQuote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public CreateQuote() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
		this.templateEngine = TemplateHandler.setTemplate(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Product product = (Product) session.getAttribute("product");
		ArrayList<Option> options = (ArrayList<Option>) session.getAttribute("chosenOptions");
		String path = "";
		try {
			if (user != null && !user.isAdmin() && product != null && options != null && !options.isEmpty()) {
				Quote quote = new Quote();
				quote.setClient(user);
				quote.setProduct(product);
				quote.setOptions(options);
				quote.setDateCreation(Calendar.getInstance().getTime());

				QuoteDAO qDao = new QuoteDAO(connection);
				qDao.addNewQuote(quote);
				// Redirect to the Home page
				session.removeAttribute("chosenQuote");
				session.removeAttribute("product");
				session.removeAttribute("userQuotes");
				session.removeAttribute("chosenOptions");
				session.removeAttribute("availableOptions");
				path = "Home";
				response.sendRedirect(path);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			path = URLHandler.CLIENT_HOME;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}

}
