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

/**
 * Servlet implementation class ViewOutputServlet
 */
@WebServlet("/viewoutput")
public class ViewOutputServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int attemptId = Integer.parseInt(request.getParameter("id"));
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/automaatnehindaja", "ahindaja",
					"k1rven2gu");
			if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
				String statement = "SELECT tasks_input.input, tasks_output.output, attempt_output.output "
						+ "FROM attempt  "
						+ "LEFT JOIN tasks_input on tasks_input.task_id = attempt.task "
						+ "LEFT JOIN tasks_output on tasks_output.task_id = attempt.task and tasks_output.outer_seq = tasks_input.outer_seq "
						+ "LEFT JOIN attempt_output on attempt_output.attempt_id = attempt.id and tasks_input.outer_seq = attempt_output.seq "
						+ "WHERE attempt.id = ?;";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
			
			rs = stmt.executeQuery();
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.append("<table border = 1 style = 'width:100%; border-collapse:collapse; font-family: Arial;'>");
			out.append("<tr>");
			out.append("<th>Sisend</th>");
			out.append("<th>Eeldatav väljund</th>");
			out.append("<th>Õpilase väljund</th>");
			out.append("</tr>");
			while (rs.next()){
				out.append("<tr>");
				out.append("<td>" + rs.getString(1) + "</td>");
				out.append("<td>" + rs.getString(2) + "</td>");
				out.append("<td>" + rs.getString(3) + "</td>");
				out.append("</tr>");
			}
			out.append("</table>");			
			c.close();
			}
			else{
				response.setContentType("text/plaing");
				PrintWriter out = response.getWriter();
				out.write("Teil pole õigust seda lehte vaadata");
			}
		}
		catch (ClassNotFoundException | SQLException e){
			e.printStackTrace();
			
		}
	}

}
