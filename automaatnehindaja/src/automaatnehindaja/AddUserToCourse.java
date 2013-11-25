package automaatnehindaja;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet("/addtocourse")
public class AddUserToCourse extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AddUserToCourse.class);

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String coursename = request.getParameter("coursename");
		if (request.isUserInRole("admin")
				|| request.isUserInRole("responsible")) {
			Connection c = null;
			PreparedStatement stmt = null;
			response.setContentType("text/plain");
			PrintWriter pw = response.getWriter();
			logger.info("Trying to add user: " + username + ", to course: "
					+ coursename + ", request by: " + request.getRemoteUser());
			try {
				Class.forName("com.mysql.jdbc.Driver");
				c = new SqlConnectionService().getConnection();
				String statement = "Insert into users_courses VALUES (?,?);";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, username);
				stmt.setString(2, coursename);
				stmt.executeUpdate();
				stmt.close();
				c.close();
				pw.write("Kasutaja lisamine õnnestus!");
				logger.info("User adding to couse succeeded!");
			} catch (ClassNotFoundException e) {
				logger.error(
						"User aading to course failed, ClassNotFoundException",
						e);
			} catch (SQLException e) {
				logger.error("User aading to course failed, SQLException. Probably user existed on course");
				pw.write("Kasutaja lisamine ebaõnnestus, ilmselt on kasutaja juba kursusel");
				try {
					c.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			logger.info("Unauthorized access! request by: "+ request.getRemoteUser());
		}

	}
}
