package automaatnehindaja;

import java.io.IOException;
import java.security.SecureRandom;
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

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

@WebServlet(urlPatterns = { "/addusermanually" })
public class AddUserManually extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SecureRandom random = new SecureRandom();
       
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
		String course = request.getParameter("course");
		if (autogenerate.equals("true")){
			newPassword = generatePassword();
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
				}
				if (autogenerate.equals("true")){
					emailPassword(newUsername, newPassword);
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
	
	protected String generatePassword(){
		char[] allowedCharacters = {'a','b','c','d','f','e','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','z','1','2','3','4','5','6','7','8','9'};
		StringBuffer password = new StringBuffer();
		for (int i = 0; i<8; i++){
			password.append(allowedCharacters[random.nextInt(allowedCharacters.length)]);
		}
		return password.toString();
	}


	protected void emailPassword(String to, String passwordToSend){
		final String username = "automaatkontroll@gmail.com";
		final String password = "k1rven2gu";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("automaatkontroll@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject("Genereeritud parool");
			message.setText("Tere, \n" 
			+"Antud e-mail on automaatselt genereeritud ning ärge vastake sellele \n" 
			+"Sinu parool automaatsehindaja rakenduses on: " 
			+ passwordToSend + "\n\n"
			+ "Ärge unustage oma parool esimesel sisselogimisel vahetada");
 
			Transport.send(message);
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	}
