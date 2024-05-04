package it.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.project.bean.User;
import it.project.utils.URLHandler;

/**
 * Servlet implementation class ErrorHandler. Redirect all wrong URLs to the
 * same page
 */
@WebServlet("/ErrorHandler")
public class ErrorHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ErrorHandler() {
		super();
	}

	public void init() throws ServletException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = "";
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if(session != null && user != null) {
			if(user.isAdmin())
				path = URLHandler.EMPLOYEE_HOME;
			else
				path = URLHandler.CLIENT_HOME;
		}
		else {
			path = URLHandler.LOGIN;
		}
		
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.sendRedirect(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
