package com.hypnotech.airport_pis.servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PublicDisplay
 */
public class PublicDisplay extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String PAGE = "publicDisplay";

	public PublicDisplay() {
		super();
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("page", this.PAGE);
		this.getServletContext().getRequestDispatcher("/WEB-INF/pages/PublicDisplay.jsp").forward(request, response);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("page", this.PAGE);
		this.getServletContext().getRequestDispatcher("/WEB-INF/pages/PublicDisplay.jsp").forward(request, response);
	}

}
