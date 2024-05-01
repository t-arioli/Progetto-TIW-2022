package it.project.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import it.project.bean.Quote;
import it.project.bean.User;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * If the session is from a client user, gets the quote from 'userQuotes' with
 * the same is as the parameter and sets it as the 'chosenQuote'. If the session
 * is from an employee user, gets the quote from 'newQuotes' and does the same,
 * then redirects to the HTML Page where could be set the price.
 */
@WebServlet("/GetQuoteDetails/*")
public class GetQuoteDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public GetQuoteDetails() {
		super();
	}

	public void init() throws ServletException {
		// thymeleaf init
		this.templateEngine = TemplateHandler.setTemplate(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Sets the 'chosenQuote' from a quote List already saved in the session, then
	 * process to another page
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		String path = "";
		Quote chosenQuote = null;
		try {
			ArrayList<Quote> quotes = null;
			if (user.isAdmin())
				// this servlet is used for checking the new quotes that need to be priced
				quotes = (ArrayList<Quote>) session.getAttribute("newQuotes");
			else
				// in this case is used by a client to see own quotes details
				quotes = (ArrayList<Quote>) session.getAttribute("userQuotes");

			int prevId = Integer.parseInt(request.getParameter("prevId"));
			for (Quote q : quotes) {
				if (q.getId() == prevId) {
					chosenQuote = q;
					break;
				}
			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		} finally {
			session.setAttribute("chosenQuote", chosenQuote);
			if (user.isAdmin()) {
				if (chosenQuote == null)
					path = URLHandler.EMPLOYEE_HOME;
				else
					path = URLHandler.QUOTE_PRICE;
			} else
				path = URLHandler.CLIENT_HOME;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
		}
	}
}
