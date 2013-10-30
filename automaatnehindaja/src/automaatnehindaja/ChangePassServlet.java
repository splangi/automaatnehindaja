package automaatnehindaja;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

import org.apache.log4j.Logger;


/**
 * Servlet implementation class ChangePassServlet
 */
@WebServlet("/ChangePassServlet")
public class ChangePassServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ChangePassServlet.class);
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getRemoteUser();
		
		String oldPass = request.getParameter("oldPass");
		String newPass = request.getParameter("newPass");
		
		Connection c = null;
		PreparedStatement stmt = null;
		String statement;
		ResultSet rs;
		
		logger.info("Change password initiated by: " + request.getRemoteUser());
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			
			statement = "SELECT password FROM users WHERE username = ?;";
			
			stmt = c.prepareStatement(statement);
			stmt.setString(1, username);
			rs = stmt.executeQuery();

			while (rs.next()) {
				 if (rs.getString(1).equals(oldPass)) {
					 
					statement = "UPDATE users SET password = ? WHERE username = ?;";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, newPass);
					stmt.setString(2, username);
					
					stmt.executeUpdate();
					response.getWriter().write("success");
					logger.info("Password changed, request by: " + request.getRemoteUser());
				 }
				 else {
					 logger.info("Wrong old password, request by: " + request.getRemoteUser());
					 response.getWriter().write("wrongPass");
				 }
			}
			
			c.close();

		} catch (SQLException e) {
			logger.error("SQLException, request by: " + request.getRemoteUser(), e);
		} catch (ClassNotFoundException f) {
			logger.error("ClassNotFountEcveption, request by: " + request.getRemoteUser(), f);
		}
	}

}
