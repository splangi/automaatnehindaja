package automaatnehindaja;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

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
			c = new SqlConnectionService().getConnection();
			if (request.isUserInRole("admin") || request.isUserInRole("responsible")){
				String statement = "SELECT GROUP_CONCAT"
						+ "(tasks_input.input SEPARATOR ' ') "
						+ "from attempt LEFT JOIN tasks_input on tasks_input.task_id = attempt.task "
						+ "WHERE attempt.id = ? "
						+ "group by outer_seq;";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
				rs = stmt.executeQuery();
				ArrayList<String> TasksInput = new ArrayList<>();
				while (rs.next()){
					TasksInput.add(rs.getString(1));
				}
				rs.close();
				statement = "SELECT GROUP_CONCAT"
						+ "(tasks_output.output SEPARATOR ' ') "
						+ "from attempt LEFT JOIN tasks_output on tasks_output.task_id = attempt.task "
						+ "WHERE attempt.id = ? "
						+ "GROUP BY outer_seq;";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
				rs = stmt.executeQuery();
				ArrayList<String> TasksOutput = new ArrayList<>();
				while (rs.next()){
					TasksOutput.add(rs.getString(1));
				}
				rs.close();
				statement = "SELECT GROUP_CONCAT(output SEPARATOR ' ') FROM attempt_output WHERE attempt_id = ? GROUP BY seq;";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
				rs = stmt.executeQuery();
				ArrayList<String> AttemptOutput = new ArrayList<>();
				while (rs.next()){
					AttemptOutput.add(rs.getString(1));
				}
				rs.close();
				
				Iterator<String> AttemptOutputIT = AttemptOutput.iterator();
				Iterator<String> TasksInputIT = TasksInput.iterator();
				Iterator<String> TasksOutputIT = TasksOutput.iterator();
				
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.append("<table border = 1 style = 'width:100%; border-collapse:collapse; font-family: Arial;'>");
				out.append("<tr>");
				out.append("<th>Sisend</th>");
				out.append("<th>Eeldatav väljund</th>");
				out.append("<th>Õpilase väljund</th>");
				out.append("</tr>");
				while (TasksInputIT.hasNext()){
					out.append("<tr>");
					out.append("<td>" + TasksInputIT.next() + "</td>");
					out.append("<td>" + TasksOutputIT.next() + "</td>");
					if (AttemptOutputIT.hasNext()){
						out.append("<td>" + AttemptOutputIT.next() + "</td>");
					}
					else{
						out.append("<td>" + "NULL" + "</td>");
					}
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
