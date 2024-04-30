package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.project.dao.QuoteDAO;
import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * Adds the price to a quote in the DB from a Employee session's request.
 * If the DB update is successful, it redirects to the Employee HTML Home page, else redirects to the QuotePrice HTML page
 * (user will remain stuck in the same HTML page)
 */
@WebServlet("/AddPrice/*")
public class AddPrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
   
    public AddPrice() {
        super();
    }
    
    public void init() throws ServletException {
    	this.connection = ConnectionHandler.getConnection(getServletContext());	
    	this.templateEngine = TemplateHandler.setTemplate(getServletContext());
    }

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Quote quote = (Quote)session.getAttribute("chosenQuote");
		User admin = (User) session.getAttribute("user");
		float price = -1;
		String path = "";
		try {
			price = Float.parseFloat(request.getParameter("price"));
			QuoteDAO qDao = new QuoteDAO(connection);
			qDao.addPrice(admin, quote, price);
			session.removeAttribute("userQuotes");
			session.removeAttribute("chosenQuote");
			path = "Home";
			response.sendRedirect(path);	
			
		}catch(SQLException | NullPointerException | NumberFormatException e) {
			System.out.println(e.getMessage());
			path=URLHandler.QUOTE_PRICE;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
	}
}


