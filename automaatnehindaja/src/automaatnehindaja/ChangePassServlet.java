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

/**
 * Servlet implementation class ChangePassServlet
 */
@WebServlet("/ChangePassServlet")
public class ChangePassServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangePassServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = request.getRemoteUser();
		
		String oldPass = request.getParameter("oldPass");
		String newPass1 = request.getParameter("newPass1");
		String newPass2 = request.getParameter("newPass2");
		
		Connection c = null;
		PreparedStatement stmt = null;
		String statement;
		ResultSet rs;
		
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
					stmt.setString(1, newPass1);
					stmt.setString(2, username);
					
					stmt.executeUpdate();
					response.getWriter().write("success");
				 }
				 else {
					 response.getWriter().write("wrongPass");
				 }
			}

		} catch (SQLException e) {
			response.sendRedirect("/automaatnehindaja/error.html");
		} catch (ClassNotFoundException f) {
			response.sendRedirect("/automaatnehindaja/error.html");
		}
	}

}
