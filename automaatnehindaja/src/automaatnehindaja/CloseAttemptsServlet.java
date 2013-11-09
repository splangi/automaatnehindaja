package automaatnehindaja;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
@WebServlet("/closeAttempts")
public class CloseAttemptsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CloseAttemptsServlet.class);
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String taskid = request.getParameter("taskid");
		PrintWriter pw = response.getWriter();
		response.setContentType("text/plain");
		if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection c = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/automaatnehindaja",
						"ahindaja", "k1rven2gu");
				logger.info("Closing all attempts in task - "  + taskid + " , request by: " + request.getRemoteUser());
				String statement = "UPDATE attempt SET active = FALSE WHERE task = ?;";
				PreparedStatement stmt = c.prepareStatement(statement);
				stmt.setString(1, taskid);
				stmt.executeUpdate();
				stmt.close();
				c.close();
				logger.info("Success! closed all attempts for task - " + taskid +".");
				pw.write("Kõik tudengite sooritused on arhiveeritud");
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException", e);
				pw.write("Midagi läks valesti, võtke ühendust administraatoriga.");
			} catch (SQLException e) {
				logger.error("SQLException", e);
				pw.write("Midagi läks valesti, võtke ühendust administraatoriga.");
			}
			
		}
		else {
			pw.write("Teil pole volitusi seda toimingut teha");
		}
	}

}
