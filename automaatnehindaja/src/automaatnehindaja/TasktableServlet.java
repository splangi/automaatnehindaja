package automaatnehindaja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/tasktable")
public class TasktableServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TasktableServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String username = request.getUserPrincipal().getName();
		String taskid = request.getParameter("id");
		boolean archived = Boolean.parseBoolean(request
				.getParameter("archived"));

		try {

			Class.forName("com.mysql.jdbc.Driver");
			c = new SqlConnectionService().getConnection();
			String statement;
			if (request.isUserInRole("tudeng")) {
				statement = "select users.fullname, attempt.time, attempt.result, "
						+ "attempt.language, attempt.id, tasks.deadline "
						+ "FROM attempt "
						+ "INNER JOIN users ON users.username=attempt.username  "
						+ "INNER JOIN tasks ON tasks.id = attempt.task "
						+ "WHERE attempt.username = ? "
						+ "and attempt.task = ?";
				if (!archived) {
					statement = statement + " and attempt.active = TRUE";
				}
				stmt = c.prepareStatement(statement);
				stmt.setString(1, username);
				stmt.setString(2, taskid);
			} else if (request.isUserInRole("admin")||request.isUserInRole("responsible")) {
				statement = "select users.fullname, attempt.time, attempt.result, "
						+ "attempt.language, attempt.id, tasks.deadline "
						+ "FROM attempt "
						+ "INNER JOIN users ON users.username=attempt.username "
						+ "INNER JOIN tasks ON tasks.id = attempt.task "
						+ "WHERE attempt.task = ?";
				if (!archived) {
					statement = statement + " and attempt.active = TRUE";
				}
				stmt = c.prepareStatement(statement);
				stmt.setString(1, taskid);
			}

			rs = stmt.executeQuery();

			response.setContentType("application/json");

			JSONObject json = new JSONObject();
			try {
				if (request.isUserInRole("tudeng")) {
					json.put("role", "tudeng");
				}
				else {
					json.put("role", "admin");
				}

				while (rs.next()) {

					json.append("fullname", rs.getString(1));
					json.append("result", rs.getString(3));
					
					Date time = rs.getDate(2);
					json.append("time", time.toString());
					
					json.append("language", rs.getString(4));
					json.append("attemptId", rs.getInt(5));
					
					Date deadline = rs.getDate(6);
					
					if (time.after(deadline)) json.append("late", "true");
					else json.append("late", "false");
				}
			} catch (JSONException e) {
				response.sendRedirect("/automaatnehindaja/error.html");
			}

			c.close();
			response.getWriter().write(json.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (ClassNotFoundException f) {
			f.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		}

	}
}
