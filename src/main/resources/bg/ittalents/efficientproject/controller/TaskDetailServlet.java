package bg.ittalents.efficientproject.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.ittalents.efficientproject.model.exception.DBException;
import bg.ittalents.efficientproject.model.exception.EfficientProjectDAOException;
import bg.ittalents.efficientproject.model.interfaces.DAOStorageSourse;
import bg.ittalents.efficientproject.model.interfaces.IProjectDAO;
import bg.ittalents.efficientproject.model.interfaces.ITaskDAO;
import bg.ittalents.efficientproject.model.pojo.Epic;
import bg.ittalents.efficientproject.model.pojo.Project;
import bg.ittalents.efficientproject.model.pojo.Sprint;
import bg.ittalents.efficientproject.model.pojo.Task;
import bg.ittalents.efficientproject.model.pojo.User;
import bg.ittalents.efficientproject.util.IntegerChecker;

/**
 * Servlet implementation class TaskDetailServlet
 */
@WebServlet("/taskdetail")
public class TaskDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DAOStorageSourse SOURCE_DATABASE = DAOStorageSourse.DATABASE;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getSession(false) == null || request.getSession().getAttribute("user") == null) {
				response.sendRedirect("./LogIn");
				return;
			}
			
			String projectIdParam = request.getParameter("projectId");
			String taskIdParam = request.getParameter("taskId");
			if (projectIdParam != null && taskIdParam != null && IntegerChecker.isInteger(taskIdParam)
					&& IntegerChecker.isInteger(projectIdParam)) {
				int projectId = Integer.parseInt(projectIdParam);
				User user = (User) request.getSession().getAttribute("user");

				if (user.isAdmin()
						&& !IProjectDAO.getDAO(SOURCE_DATABASE).isThisProjectOfThisUser(projectId, user.getId())) {
					request.getRequestDispatcher("errorNotAuthorized.jsp").forward(request, response);
					return;
				}
				Project currentProject = IProjectDAO.getDAO(DAOStorageSourse.DATABASE).getProjectByID(projectId);
				request.setAttribute("project", currentProject);

				int taskId = Integer.parseInt(taskIdParam);
				Task task = ITaskDAO.getDAO(DAOStorageSourse.DATABASE).getTaskById(taskId);
				User assignee = task.getAssignee();
				User reporter = task.getReporter();
				Epic epic = task.getEpic();
				Sprint sprint = task.getSprint();
				request.setAttribute("assignee", assignee);
				System.out.println(assignee);
				request.setAttribute("reporter", reporter);
				request.setAttribute("epic", epic);
				request.setAttribute("task", task);
				request.setAttribute("sprint", sprint);
				RequestDispatcher rd = request.getRequestDispatcher("/taskDetail.jsp");
				rd.forward(request, response);
			} else {
				request.getRequestDispatcher("error2.jsp").forward(request, response);
			}

		} catch (IOException | DBException | EfficientProjectDAOException | ServletException e) {
			try {
				response.sendRedirect("error.jsp");
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

}
