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

@WebServlet(urlPatterns = { "/addusermanually" })
public class AddUserManually extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PasswordGeneratorAndMailer generator = new PasswordGeneratorAndMailer();
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String newUsername = request.getParameter("username");
		String newPassword = request.getParameter("password");
		String fullname = request.getParameter("fullname");
		String studentid = request.getParameter("studentid");
		String autogenerate = request.getParameter("autogenerate");
		String role = request.getParameter("role");
		//TODO course
		@SuppressWarnings("unused")
		String course = request.getParameter("course");
		if (autogenerate.equals("true")){
			newPassword = generator.generatePassword();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			String statement;
			if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
				statement = "SELECT 1 FROM users WHERE username = ?";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, newUsername);
				rs = stmt.executeQuery();
				if (rs.next()){
					response.setHeader("exists", "true");
					System.out.println("tere");
				}
				else{
					response.setHeader("exists", "false");
					stmt.close();
					statement = "INSERT INTO users VALUES (?,?,?,?);";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, newUsername);
					stmt.setString(2, newPassword);
					stmt.setString(3, fullname);
					if (studentid.length() != 6){
						stmt.setNull(4, java.sql.Types.VARCHAR);
					}
					else{
						stmt.setString(4, studentid);
					}
					stmt.executeUpdate();
					stmt.close();
					statement = "INSERT INTO users_roles VALUES (?,?);";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, newUsername);
					stmt.setString(2, role);
					stmt.executeUpdate();
					if (autogenerate.equals("true")){
						generator.emailPassword(newUsername, newPassword);
					}
				}				
				response.setHeader("error", "false");
			}
			else{
				response.setHeader("error", "true");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setHeader("error", "true");
		} catch (ClassNotFoundException f) {
			f.printStackTrace();
			response.setHeader("error", "true");
		}

	}

}
