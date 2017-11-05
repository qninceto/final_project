package bg.ittalents.efficientproject.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.ittalents.efficientproject.model.exception.DBException;
import bg.ittalents.efficientproject.model.exception.EffPrjDAOException;
import bg.ittalents.efficientproject.model.exception.EffProjectException;
import bg.ittalents.efficientproject.model.interfaces.DAOStorageSourse;
import bg.ittalents.efficientproject.model.interfaces.IEpicDAO;
import bg.ittalents.efficientproject.model.interfaces.IProjectDAO;
import bg.ittalents.efficientproject.model.interfaces.ISprintDAO;
import bg.ittalents.efficientproject.model.interfaces.IUserDAO;
import bg.ittalents.efficientproject.model.pojo.Epic;
import bg.ittalents.efficientproject.model.pojo.Project;
import bg.ittalents.efficientproject.model.pojo.Sprint;
import bg.ittalents.efficientproject.model.pojo.User;

/**
 * Servlet implementation class projectDetail
 */
@WebServlet("/projectdetail")
public class ProjectDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DAOStorageSourse SOURCE_DATABASE = DAOStorageSourse.DATABASE;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {

			if (request.getSession(false) == null) {
				response.sendRedirect("./LogIn");
				return;
			}
			if (request.getSession().getAttribute("user")!=null && request.getParameter("projectId") != null) {
				User user = (User) request.getSession().getAttribute("user");

				if (!user.isAdmin()) {
					request.getRequestDispatcher("errorNotAuthorized.jsp").forward(request, response);
					return;
				}
				int projectId = Integer.parseInt(request.getParameter("projectId"));
				/**
				 * check if the project is of this admin
				 */
				if (!IProjectDAO.getDAO(SOURCE_DATABASE).isThisProjectOfThisUser(projectId, user.getId())) {
					request.getRequestDispatcher("errorNotAuthorized.jsp").forward(request, response);
					return;
				}
				Sprint currentSprint = ISprintDAO.getDAO(DAOStorageSourse.DATABASE).getCurrentSprint(projectId);
				if (currentSprint != null) {
					request.setAttribute("currentSprint", currentSprint);
				}

				boolean projectFinished=IProjectDAO.getDAO(DAOStorageSourse.DATABASE).isProjectFinished(projectId);
				request.setAttribute("projectFinished", projectFinished);
				Project currentProject = IProjectDAO.getDAO(DAOStorageSourse.DATABASE).getProjectByID(projectId);
				request.setAttribute("project", currentProject);

				List<Epic> epics = IEpicDAO.getDAO(DAOStorageSourse.DATABASE).getAllEpicsByProject(projectId);
				request.setAttribute("epics", epics);

				List<User> users = IProjectDAO.getDAO(DAOStorageSourse.DATABASE)
						.getAllWorkersWorkingOnAProject(projectId);
				request.setAttribute("workers", users);
				

				// get current sprintby id---->if there is no current sprint--->message and
				// redirect to create new one
				// if there is current sprnt--->show the sprint

				RequestDispatcher rd = request.getRequestDispatcher("/projectDetail.jsp");
				rd.forward(request, response);
			} else {
				request.getRequestDispatcher("error2.jsp").forward(request, response);
			}
		} catch (DBException | EffPrjDAOException | IOException | ServletException e) {
			try {
				response.sendRedirect("error.jsp");
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
