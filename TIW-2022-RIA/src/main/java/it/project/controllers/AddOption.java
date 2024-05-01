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
import it.project.bean.Option;
import it.project.utils.TemplateHandler;
import it.project.utils.URLHandler;

/**
 * Adds a new option to the new quote, before it will be added to the DB
 */
@WebServlet("/AddOption/*")
public class AddOption extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	public AddOption() {
		super();
	}

	public void init() throws ServletException {
		this.templateEngine = TemplateHandler.setTemplate(getServletContext());
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
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ArrayList<Option> chosenOptions = (ArrayList<Option>) session.getAttribute("chosenOptions"); // could be null
		ArrayList<Option> availableOptions = (ArrayList<Option>) session.getAttribute("availableOptions");
		int optionCode = Integer.parseInt(request.getParameter("optionCode"));
		Option option = null;

		if (chosenOptions == null) {
			chosenOptions = new ArrayList<>();
		}
		if (availableOptions != null) {
			for (Option o : availableOptions) {
				if (o.getCode() == optionCode) {
					option = o;
					break;
				}
			}
			if (option != null) {
				boolean found = false;
				for (Option o : chosenOptions) {
					if (o.getCode() == option.getCode())
						found = true;
				}
				if (!found)
					chosenOptions.add(option);
				session.setAttribute("chosenOptions", chosenOptions);
			}
		}
		String path = URLHandler.CLIENT_HOME;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
}
