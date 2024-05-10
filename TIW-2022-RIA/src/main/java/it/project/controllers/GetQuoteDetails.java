package it.project.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.project.bean.Quote;
import it.project.bean.User;

/**
 * If the session is from a client user, gets the quote from 'userQuotes' with
 * the same is as the parameter and sets it as the 'chosenQuote'. If the session
 * is from an employee user, gets the quote from 'newQuotes' and does the same,
 * then redirects to the HTML Page where could be set the price.
 */
@WebServlet("/GetQuoteDetails/*")
public class GetQuoteDetails extends HttpServlet {
/*	private static final long serialVersionUID = 1L;

	public GetQuoteDetails() {
		super();
	}

	public void init() throws ServletException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	
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
	 
	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		//String path = "";
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
			session.setAttribute("chosenQuote", chosenQuote);
			String json = new Gson().toJson(chosenQuote);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong id");
		} 
	}*/
}
