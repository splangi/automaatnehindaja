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


@WebServlet("/viewfile")
public class ViewFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


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
			String statement = "SELECT source_code FROM attempt WHERE attempt.id = ?";
			stmt = c.prepareStatement(statement);
			stmt.setInt(1, attemptId);
			rs = stmt.executeQuery();
			if (rs.next()){
				String code = new String(rs.getBytes(1), "UTF-8");
				response.setContentType("text/html");
				
				PrintWriter out = response.getWriter();
				out.append("<link rel='stylesheet' href='http://yandex.st/highlightjs/7.3/styles/default.min.css'>");
				out.append("<script src='http://yandex.st/highlightjs/7.3/highlight.min.js'></script>");
				out.append("<script>hljs.initHighlightingOnLoad();</script>");
				out.append("<a href = 'download?id="+attemptId + "'>Laadi see fail alla</a>");
				out.append("<pre>");
				out.append("<code>");
				out.append(code);
				out.append("</code>");
				out.append("</pre>");
			}
			
		}
		catch (ClassNotFoundException | SQLException e){
			e.printStackTrace();
			
		}
	}

}
