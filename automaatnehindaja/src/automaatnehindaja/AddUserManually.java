package automaatnehindaja;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

@WebServlet(urlPatterns = { "/addusermanually" })
public class AddUserManually extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PasswordGeneratorAndMailer generator = new PasswordGeneratorAndMailer();
	private static Logger logger = Logger.getLogger(AddUserManually.class);

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.isUserInRole("admin")
				|| request.isUserInRole("responsible")) {
						
			Connection c = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String newUsername = request.getParameter("username");
			String newPassword = request.getParameter("password");
			String fullname = request.getParameter("fullname");
			String studentid = request.getParameter("studentid");
			String autogenerate = request.getParameter("autogenerate");
			String role = request.getParameter("role");
			String course = request.getParameter("course");
			if (autogenerate.equals("true")) {
				newPassword = generator.generatePassword();
			}
			
			if (!request.isUserInRole("admin") && role == "admin"){
				logger.warn("Unathorized admin adding, request by: " + request.getRemoteUser());
				response.setHeader("error", "true");
				return;
			}
			
			try {
				String newPasswordHash = PasswordGeneratorAndMailer.sha1(newPassword);
				
				Class.forName("com.mysql.jdbc.Driver");
				c = new SqlConnectionService().getConnection();
				String statement;
					statement = "SELECT 1 FROM users WHERE username = ?";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, newUsername);
					rs = stmt.executeQuery();
					if (rs.next()) {
						logger.info("User tried to add an user ("+newUsername+") which exists. Request by: " +request.getRemoteUser());
						response.setHeader("exists", "true");
					} else {
						logger.info("Starting to add user ("+newUsername+") manually. Request by:" + request.getRemoteUser());
						response.setHeader("exists", "false");
						stmt.close();
						statement = "INSERT INTO users VALUES (?,?,?,?);";
						stmt = c.prepareStatement(statement);
						stmt.setString(1, newUsername);
						stmt.setString(2, newPasswordHash);
						stmt.setString(3, fullname);
						if (studentid.length() != 6) {
							stmt.setNull(4, java.sql.Types.VARCHAR);
						} else {
							stmt.setString(4, studentid);
						}
						stmt.executeUpdate();
						stmt.close();
						
						statement = "INSERT INTO users_roles VALUES (?,?);";
						stmt = c.prepareStatement(statement);
						stmt.setString(1, newUsername);
						stmt.setString(2, role);
						stmt.executeUpdate();
						stmt.close();
						if (role == "admin"){
							statement = "SELECT coursename FROM courses;";
							stmt = c.prepareStatement(statement);
							rs = stmt.executeQuery();
							String statement2 = "INSERT INTO users_courses VALUES (?,?)";
							PreparedStatement stmt2 = c.prepareStatement(statement2);
							while (rs.next()){
								stmt2.setString(1, newUsername);
								stmt2.setString(2, rs.getString(1));
								stmt2.addBatch();
							}
							stmt2.executeBatch();
							stmt.close();
							stmt2.close();
						}
						else{
							statement = "INSERT INTO users_courses VALUES (?,?);";
							stmt = c.prepareStatement(statement);
							stmt.setString(1, newUsername);
							stmt.setString(2, course);
							stmt.executeUpdate();
							stmt.close();
						}
						if (autogenerate.equals("true")) {
							generator.emailPassword(newUsername, newPassword);
						}
					
					response.setHeader("error", "false");
				}
				c.close();
				logger.info("User addation succeeded ("+newUsername+"). Request by: " +request.getRemoteUser());
			} catch (SQLException e) {
				logger.error("SQLException when adding user. Request By:" + request.getRemoteUser(), e);
				response.setHeader("error", "true");
			} catch (ClassNotFoundException f) {
				logger.error("ClassNotFoundException when adding user. Request By:" + request.getRemoteUser(), f);
				f.printStackTrace();
				response.setHeader("error", "true");
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			}
		} else {
			logger.warn("Unauthorized access by: " + request.getRemoteUser());
		}

	}

}
