package automaatnehindaja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String statement = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			statement = "SELECT coursename from users_courses where username = ?;";
			stmt = c.prepareStatement(statement);
			stmt.setString(1, request.getRemoteUser());
			
			System.out.println(stmt.toString());
			
			rs = stmt.executeQuery();
			
			JSONObject json = new JSONObject();
			
			while (rs.next()){
				json.append("coursenames", rs.getString(1));
			}
			
			if (request.isUserInRole("admin")){
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
