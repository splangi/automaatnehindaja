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

import org.json.JSONException;
import org.json.JSONObject;


@WebServlet("/TaskstableServlet")
public class TaskstableServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public TaskstableServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;  
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			  c =DriverManager.getConnection 
			  ("jdbc:mysql://localhost:3306/automaatnehindaja","root","t6urott");
			  
			String statement = "select id, name, deadline from tasks;";
			stmt = c.prepareStatement(statement);
			
			rs = stmt.executeQuery();
			
			response.setContentType("application/json");
			
			JSONObject json = new JSONObject();
			
			while (rs.next()){
				try {
					json.append("id", rs.getString(1));
					json.append("name", rs.getString(2));
					json.append("deadline", rs.getDate(3).toString());
				} catch (JSONException e) {
					response.sendRedirect("/automaatnehindaja/error.html");
				}				
			}
			
			response.getWriter().write(json.toString());
		}
		catch (SQLException e){
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		catch (ClassNotFoundException f){
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		
	}
	
}
