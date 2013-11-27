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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class PlagirismServlet
 */
@WebServlet("/getPlagiarismScores")
public class PlagiarismServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PlagiarismServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String statement;
		String course = request.getParameter("course");
		
		try {
			c = new SqlConnectionService().getConnection();
			if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
				statement =  "SELECT "
						+ "plagiarism.attempt1_id, "
						+ "(SELECT users.fullname FROM attempt INNER JOIN users ON attempt.username = users.username WHERE plagiarism.attempt1_id = attempt.id) AS '1st Student', "
						+ "plagiarism.attempt2_id, "
						+ "(SELECT users.fullname FROM attempt INNER JOIN users ON attempt.username = users.username WHERE plagiarism.attempt2_id = attempt.id) AS '2nd Student', "
						+ "rating, "
						+ "time "
						+ "FROM plagiarism "
						+ "INNER JOIN tasks "
						+ "ON tasks.id = plagiarism.task_id "
						+ "WHERE tasks.coursename = ?;";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, course);
				rs = stmt.executeQuery();
				response.setContentType("application/json");
				JSONObject json = new JSONObject();
				while (rs.next()){
					json.append("Attempt1ID", rs.getString(1));
					json.append("username1", rs.getString(2));
					json.append("Attempt2ID", rs.getString(3));
					json.append("username2", rs.getString(4));
					json.append("rating", rs.getInt(5));
					json.append("time", rs.getTimestamp(6));
				}
				response.getWriter().write(json.toString());
			}
			else{
				response.setContentType("text/plain");
				response.getWriter().write("Not Authorized!");
			}
			c.close();
		}
		catch (SQLException e){
			logger.error("SqlException", e);
		} catch (JSONException e) {
			logger.error("JSONException", e);
		}
	}

}
