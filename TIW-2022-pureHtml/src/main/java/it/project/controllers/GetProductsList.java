package it.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import it.project.bean.Product;
import it.project.dao.ProductDAO;
import it.project.utils.ConnectionHandler;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * Gets the List of products from the DB and stores it in the session
 */
@WebServlet("/GetProductsList")
public class GetProductsList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
  
    public GetProductsList() {
        super();
    }
    
    public void init() throws ServletException {
    	//db connection
    	this.connection = ConnectionHandler.getConnection(getServletContext());
    	//thymeleaf init
    	this.templateEngine = TemplateHandler.setTemplate(getServletContext());
	}

    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductDAO pDao = new ProductDAO(connection);
		HttpSession session = request.getSession();
				
		List<Product> prods = null;
		try {
			prods = pDao.getProductsList();
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			session.setAttribute("productsList", prods);
			String path = URLHandler.CLIENT_HOME;
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			//e.printStackTrace();
		}
	}
}
