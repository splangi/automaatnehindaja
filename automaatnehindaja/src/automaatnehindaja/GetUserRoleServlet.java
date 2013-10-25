package automaatnehindaja;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/getRole")
public class GetUserRoleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		
		if (request.isUserInRole("admin")){
			response.getWriter().write("admin");
			System.out.println("admin");
		}
		else if (request.isUserInRole("tudeng")){
			response.getWriter().write("tudeng");
			System.out.println("tudeng");
		}
		else if (request.isUserInRole("responsible")){
			response.getWriter().write("responsible");
			System.out.println("responsible");
		}
	}

}
