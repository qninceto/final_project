package bg.ittalents.efficientproject.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.activation.UnsupportedDataTypeException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.ittalents.efficientproject.model.exception.DBException;
import bg.ittalents.efficientproject.model.exception.EffPrjDAOException;
import bg.ittalents.efficientproject.model.interfaces.DAOStorageSourse;
import bg.ittalents.efficientproject.model.interfaces.IOrganizationDAO;
import bg.ittalents.efficientproject.model.interfaces.IProjectDAO;
import bg.ittalents.efficientproject.model.pojo.Organization;
import bg.ittalents.efficientproject.model.pojo.Project;
import bg.ittalents.efficientproject.model.pojo.User;

/**
 * Servlet implementation class CreateCategoryServlet
 */
@WebServlet("/createproject")
public class CreateProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		String organizationName = user.getOrganization().getName();
		request.setAttribute("organizationName", organizationName);

		RequestDispatcher dispatcher = request.getRequestDispatcher("./createProject.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = request.getParameter("name");
			String deadline = request.getParameter("deadline");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date((sdf.parse(deadline)).getTime());
			User user = (User) request.getSession().getAttribute("user");
			Organization org = IOrganizationDAO.getDAO(DAOStorageSourse.DATABASE)
					.getOrgById(user.getOrganization().getId());

			Project projectToAdd = new Project(name, date, org);

			int id = IProjectDAO.getDAO(DAOStorageSourse.DATABASE).addProject(projectToAdd,user.getId());
			// everything went well:
			response.sendRedirect("./dashboard");
		} catch (ParseException | EffPrjDAOException | DBException | IOException e) {
			try {
				response.sendRedirect("./error.jsp");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
