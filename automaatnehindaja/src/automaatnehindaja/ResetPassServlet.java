package automaatnehindaja;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.UUID;


/**
 * Servlet implementation class ChangePassServlet
 */
@WebServlet("/resetPass")
public class ResetPassServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PasswordResetMailer mailer = new PasswordResetMailer();
	private static PasswordGeneratorAndMailer generator = new PasswordGeneratorAndMailer();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetPassServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String guid = request.getParameter("id");
		
		if (guid == null) {
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		
		Date now = new Date();
		Connection c = null;
		PreparedStatement stmt = null;
		String statement;
		ResultSet rs;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			
			statement = "SELECT time, username FROM passreset WHERE guid=?;";
			stmt = c.prepareStatement(statement);
			stmt.setString(1, guid);
			rs = stmt.executeQuery();
			Timestamp t;
			
			while (rs.next()) {
				t = rs.getTimestamp(1);
				String username = rs.getString(2);
				long diff = (now.getTime() - t.getTime()) / 60000;
				
				if (diff < 60) {
					String newPassword = generator.generatePassword();
					String newPasswordHash = PasswordGeneratorAndMailer.sha1(newPassword);
					
					statement = "UPDATE users SET password = ? WHERE username = ?;";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, newPasswordHash);
					stmt.setString(2, username);
					
					stmt.executeUpdate();
					
					String messageText = "Tere, \n" 
							+"Antud e-mail on automaatselt genereeritud ning ärge vastake sellele \n" 
							+"Sinu uus parool automaatsehindaja rakenduses on: " 
							+ newPassword + "\n\n"
							+ "Ärge unustage oma parool sisselogimisel vahetada";
					
					mailer.emailMessage(username, messageText, "Uus parool");
					response.getWriter().println("Uus parool saadetud.");
				}
				else {
					response.getWriter().println("Link on aegunud.");
				}
			}
			
			statement = "DELETE FROM passreset WHERE guid=?;";
			stmt = c.prepareStatement(statement);
			stmt.setString(1, guid);
			stmt.executeUpdate();
			
			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (ClassNotFoundException f) {
			f.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String mailAddress = request.getParameter("mail");
		
		if (mailAddress == null) {
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		
		UUID guid = UUID.randomUUID();
		Timestamp time = new Timestamp(new Date().getTime());
		
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs;
		String statement;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			
			statement = "SELECT EXISTS(SELECT 1 FROM users WHERE username=?);";
			
			stmt = c.prepareStatement(statement);
			stmt.setString(1, mailAddress);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				int exists = rs.getInt(1);
				if (exists == 0) {
					throw new NoUserException();
				}
			}
			
			statement = "INSERT INTO passreset(guid, username, time) VALUES (?,?,?);";
			
			stmt = c.prepareStatement(statement);
			stmt.setString(1, guid.toString());
			stmt.setString(2, mailAddress);
			stmt.setTimestamp(3, time);
			stmt.executeUpdate();
			
			c.close();
			
			String confirmLink = "https://ec2-54-237-98-146.compute-1.amazonaws.com/automaatnehindaja/resetPass?id=" + guid.toString();
					
			String messageText = "Tere, \n" 
					+"Antud e-mail on automaatselt genereeritud ning ärge vastake sellele \n" 
					+"Uue parooli saamise kinnitamiseks klikkige allolevale lingile: \n\n"
					+ confirmLink;
			
			mailer.emailMessage(mailAddress, messageText, "Parooli taastamise kinnitus");
			response.getWriter().write("success");

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (ClassNotFoundException f) {
			f.printStackTrace();
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (NoUserException e) {
			response.getWriter().write("no user");
		}
	}

}

class NoUserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoUserException() {}
}
