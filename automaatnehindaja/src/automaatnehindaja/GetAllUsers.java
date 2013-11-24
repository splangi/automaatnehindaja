package automaatnehindaja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/getusers")
public class GetAllUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(GetAllUsers.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String statement = null;

		try {
			if (request.isUserInRole("admin")
					|| request.isUserInRole("responsible")) {
				Class.forName("com.mysql.jdbc.Driver");
				c = new SqlConnectionService().getConnection();
				
				statement = "SELECT users.username from users "
							+"LEFT JOIN users_roles "
							+"ON users.username=users_roles.username "
							+"WHERE users_roles.rolename != ?;";
				
				stmt = c.prepareStatement(statement);
				stmt.setString(1, "admin");

				rs = stmt.executeQuery();

				JSONObject json = new JSONObject();

				while (rs.next()) {
					json.append("usernames", rs.getString(1));
				}

				c.close();

				response.setContentType("application/json");
				response.getWriter().write(json.toString());
			}
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			logger.error("Error while getting all users", e);
		}

	}

}
