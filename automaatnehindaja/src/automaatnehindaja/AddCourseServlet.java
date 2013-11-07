package automaatnehindaja;

import java.io.IOException;
import java.io.PrintWriter;
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


@WebServlet("/addCourse")
public class AddCourseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AddTaskServlet.class);
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String coursename = request.getParameter("coursename");
		PrintWriter pw = response.getWriter();
		response.setContentType("text/plain");
		if (request.isUserInRole("admin")){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection c = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/automaatnehindaja",
						"ahindaja", "k1rven2gu");
				logger.info("Inserting a new course by: " + request.getRemoteUser());
				String statement = "INSERT INTO courses (coursename) VALUES (?);";
				PreparedStatement stmt = c.prepareStatement(statement);
				stmt.setString(1, coursename);
				stmt.executeUpdate();
				stmt.close();
				statement = "SELECT username FROM users_roles WHERE rolename = 'admin';";
				stmt = c.prepareStatement(statement);
				ResultSet rs = stmt.executeQuery();
				statement = "INSERT INTO users_courses VALUES (?,?);";
				PreparedStatement stmt2 = c.prepareStatement(statement);
				while(rs.next()){
					stmt2.setString(1, rs.getString(1));
					stmt2.setString(2, coursename);
					stmt2.addBatch();
				}
				stmt2.executeBatch();
				stmt.close();
				stmt2.close();
				c.close();
				logger.info("Success! added course " + coursename + ".");
				pw.write("Kursuse lisamine õnnestus!");
			} catch (ClassNotFoundException e) {
				logger.error("ClassNotFoundException", e);
				e.printStackTrace();
			} catch (SQLException e) {
				logger.error("ClassNotFoundException", e);
				pw.write("Kursuse lisamine ebaõnnestus, tõenäoliselt on antud nimega kursus juba olemas!");
				e.printStackTrace();
			}
			
		}
		else {
			pw.write("not authorized!");
		}
	}

}
