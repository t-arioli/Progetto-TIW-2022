package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.project.bean.Option;
import it.project.bean.Product;
import it.project.dao.OptionDAO;
import it.project.utils.ConnectionHandler;

/**
 * Gets a product from the 'productsList' stored in the session and encapsulates
 * is in a new Product object, then stores it in the session.
 */
@WebServlet("/ChooseProduct/*")
public class ChooseProduct extends HttpServlet {
/*	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ChooseProduct() {
		super();

	}

	public void init() throws ServletException {
		// db connection
		this.connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * Calls the DAO and gets the product identified by request parameter
	 * 'productId'
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	
	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Product product = null;
		int productId = -1;
		ArrayList<Option> options = null;
		HttpSession session = request.getSession();
		ArrayList<Product> pList = null;
		try {
			productId = Integer.parseInt(request.getParameter("productId"));
			pList = (ArrayList<Product>) session.getAttribute("productsList");
			for (Product p : pList)
				if (p.getCode() == productId) {
					product = p;
					break;
				}
			if (product == null)
				throw new SQLException("Nessun prodotto nel db");
			
			//OptionDAO oDao = new OptionDAO(this.connection);
			//options = oDao.getOptionsByProduct(product.getCode());
			session.setAttribute("product", product);
			session.setAttribute("availableOptions", options);
			//session.setAttribute("chosenOptions", null);
			//System.out.println(product + " " + options);
			ArrayList<Object> resContent = new ArrayList<Object>();
			resContent.add(product);
			resContent.add(options);
			String json = new Gson().toJson(resContent);
			System.out.println(json);
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} catch (NumberFormatException | SQLException e) {
			// case bad request
			System.out.println(e.getMessage());
			session.removeAttribute("product");
			session.removeAttribute("availableOptions");
			session.removeAttribute("chosenOptions");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error");
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}*/
}
