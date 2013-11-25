package automaatnehindaja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class GetCourseNamesServlet
 */
@WebServlet("/getcoursenames")
public class GetCourseNamesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String statement = null;

		String archived = request.getParameter("archived");
		if (archived == null || !archived.equalsIgnoreCase("true")) {
			archived = "false";
		}

		boolean archivedBoolean = Boolean.parseBoolean(archived);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = new SqlConnectionService().getConnection();

			statement = "SELECT courses.coursename, courses.active from courses "
					+ "LEFT JOIN users_courses "
					+ "ON courses.coursename=users_courses.coursename "
					+ "WHERE users_courses.username = ? ";
			if (!archivedBoolean) {
				statement = statement + " AND active = 1";
			}
			else {
				statement = statement + " ORDER BY courses.active DESC";
			}
			stmt = c.prepareStatement(statement);
			stmt.setString(1, request.getRemoteUser());

			rs = stmt.executeQuery();

			JSONObject json = new JSONObject();
			
			while (rs.next()) {
				json.append("coursenames", rs.getString(1));
				json.append("active", rs.getBoolean(2));
			}

			if (request.isUserInRole("admin")) {
				json.put("role", "admin");
			}

			c.close();

			response.setContentType("application/json");
			response.getWriter().write(json.toString());

		} catch (SQLException | ClassNotFoundException | JSONException e) {
			e.printStackTrace();
		}

	}

}
