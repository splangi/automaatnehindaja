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

@WebServlet("/task")
public class TaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;  
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String id = request.getParameter("id");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			  c =DriverManager.getConnection 
			  ("jdbc:mysql://localhost:3306/automaatnehindaja","ahindaja","k1rven2gu");
			  
			String statement = "select name, description, deadline, active from tasks where id = ?;";
			stmt = c.prepareStatement(statement);
			stmt.setInt(1, Integer.parseInt(id));
			
			rs = stmt.executeQuery();
			
			response.setContentType("application/json");
			
			JSONObject json = new JSONObject();
			
			try{
				if (rs.next()){
					json.put("name", rs.getString(1));
					json.put("description", rs.getString(2));
					json.put("deadline", rs.getTimestamp(3).toString());
					json.put("active", rs.getString(4));
				}
			}
			
			catch (JSONException e) {
			}
			
			c.close();
			response.getWriter().write(json.toString());
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (ClassNotFoundException f){
		}	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
