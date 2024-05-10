package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.project.bean.Option;
import it.project.bean.Product;
import it.project.bean.Quote;
import it.project.bean.User;
import it.project.dao.ProductDAO;
import it.project.dao.QuoteDAO;
import it.project.utils.ConnectionHandler;

/**
 * Gets all the details from the session, encapsulates them in a new Quote and
 * inserts in the DB
 */
@WebServlet("/CreateQuote")
@MultipartConfig
public class CreateQuote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateQuote() {
		super();
	}

	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		//Product product = (Product) session.getAttribute("product");
		//ArrayList<Option> options = (ArrayList<Option>) session.getAttribute("chosenOptions");
		//String path = "";
		Product product = null;
		ArrayList<Option> options = null;
		System.out.println("Servlet CHIAMATA");
		try {
			if (user != null && !user.isAdmin()) {
				ProductDAO pDao = new ProductDAO(this.connection);
				//System.out.println(request.getParameter("prodId"));
				int productCode = Integer.parseInt(request.getParameter("prodId"));
				//tocheck
				String[] optsParams = request.getParameterValues("optId");
				System.out.println(productCode);
				
				product = pDao.getProductById(productCode);
				options = new ArrayList<Option>();
				System.out.println("Servlet CreateQuote: "+product.toString());
				if(optsParams.length == 0 || product == null)
					throw new Exception("parameters problem");
				
				for(String s: optsParams) {
					int optionCode = Integer.parseInt(s);
					System.out.println("Option n: "+optionCode);
					ArrayList<Option> availableOptions = product.getAvailableOptions();
					for(Option o: availableOptions) {
						if(o.getCode() == optionCode) {
							options.add(o);
							break;
						}
					}
				}
				
				//System.out.println(product.toString());
				
				Quote quote = new Quote();
				quote.setClient(user);
				quote.setProduct(product);
				quote.setOptions(options);
				quote.setDateCreation(Calendar.getInstance().getTime());
				//System.out.println("NEW QUOTE: "+quote.toString());

				QuoteDAO qDao = new QuoteDAO(connection);
				qDao.addNewQuote(quote);
				// Redirect to the Home page
				//session.removeAttribute("chosenQuote");
				//session.removeAttribute("product");
				session.removeAttribute("userQuotes");
				//session.removeAttribute("chosenOptions");
				//session.removeAttribute("availableOptions");
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			//System.out.println(product.toString());
			//System.out.println(options.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("BAD REQUEST");
			return;
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
