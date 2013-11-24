package automaatnehindaja;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class CloseAttemptsServlet
 */
@WebServlet("/closeCourse")
public class CloseCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CloseCourseServlet.class);
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String course = request.getParameter("coursename");
		PrintWriter pw = response.getWriter();
		response.setContentType("text/plain");
		if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection c = new SqlConnectionService().getConnection();
				logger.info("Closing course - "  + course + " , request by: " + request.getRemoteUser());
				String statement = "UPDATE courses SET active = FALSE WHERE coursename = ?;";
				PreparedStatement stmt = c.prepareStatement(statement);
				stmt.setString(1, course);
				stmt.executeUpdate();
				stmt.close();
				statement = "UPDATE tasks SET active = FALSE WHERE coursename = ?;";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, course);
				stmt.executeUpdate();
				stmt.close();
				statement = "SELECT id FROM tasks WHERE coursename = ?;";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, course);				
				ResultSet rs = stmt.executeQuery();
				statement = "UPDATE attempt SET active = FALSE WHERE task = ?;";
				PreparedStatement stmt2 = c.prepareStatement(statement);
				while (rs.next()){
					stmt2.setInt(1, rs.getInt(1));
					stmt2.addBatch();
				}
				stmt2.executeBatch();
				stmt2.close();
				stmt.close();
				c.close();
				logger.info("Success! closed course - " + course +".");
				pw.write("Kursus on arhiveeritud");
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException", e);
				pw.write("Midagi läks valesti, võtke ühendust administraatoriga.");
			} catch (SQLException e) {
				logger.error("SQLException", e);
				pw.write("Midagi läks valesti, võtke ühendust administraatoriga.");
			}
			
		}
		else {
			logger.warn("Unauthorized access, request by: " + request.getRemoteUser());
			pw.write("Teil pole volitusi seda toimingut teha");
		}
	}

}
