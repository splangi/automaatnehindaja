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

/**
 * Servlet implementation class ResultTableServlet
 */
@WebServlet("/resulttable")
public class ResultTableServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String username = request.getUserPrincipal().getName();
		String statement;
		String course = request.getParameter("course");
		boolean archived = Boolean.parseBoolean(request.getParameter("archived"));

		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = new SqlConnectionService().getConnection();
			if (request.isUserInRole("tudeng")){
				statement = "SELECT users.fullname, tasks.name, attempt.time, attempt.result, "
						+ "attempt.language, tasks.id, attempt.active, tasks.deadline "
						+ "FROM attempt "
						+ "INNER JOIN tasks ON tasks.id = attempt.task "
						+ "INNER JOIN users on attempt.username = users.username "
						+ "WHERE attempt.username = ? and tasks.coursename = ?";
				if (!archived){
					statement = statement + " and attempt.active = TRUE";
				}
				stmt = c.prepareStatement(statement);
				stmt.setString(1, username);
				stmt.setString(2, course);
			}
			else if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
				statement = "SELECT users.fullname, tasks.name, attempt.time, attempt.result, "
						+ "attempt.language, tasks.id, attempt.active, tasks.deadline "
						+ "FROM attempt "
						+ "INNER JOIN tasks ON tasks.id = attempt.task "
						+ "INNER JOIN users on attempt.username = users.username and tasks.coursename = ?";
				if (!archived){
					statement = statement + " and attempt.active = TRUE";
				}
				stmt = c.prepareStatement(statement);
				stmt.setString(1, course);
			}
			
			rs = stmt.executeQuery();

			response.setContentType("application/json");

			JSONObject json = new JSONObject();
			//SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			while (rs.next()) {
				try {
					json.append("fullname", rs.getString(1));
					json.append("taskname", rs.getString(2));
					
					//Timestamp time = rs.getTimestamp(3);
					Date time = rs.getDate(3);
					json.append("time", time.toString());
					
					json.append("result", rs.getString(4));
					json.append("language", rs.getString(5));
					json.append("id", rs.getString(6));
					json.append("active", rs.getString(7));
					
					//Timestamp deadline = rs.getTimestamp(8);
					Date deadline = rs.getDate(8);
					
					if (time.after(deadline)) json.append("late", "true");
					else json.append("late", "false");
					
				} catch (JSONException e) {
					response.sendRedirect("/automaatnehindaja/error.html");
				}
			}
			
			response.getWriter().write(json.toString());
			
			c.close();
			
		} catch (ClassNotFoundException e) {
			response.sendRedirect("/automaatnehindaja/error.html");
			e.printStackTrace();
		} catch (SQLException e) {
			response.sendRedirect("/automaatnehindaja/error.html");
			e.printStackTrace();
		}

	}

}
