package automaatnehindaja;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// Reference from : http://www.codejava.net/java-ee/servlet/java-servlet-to-download-file-from-database
@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_SIZE = 4096; 

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
			if (request.isUserInRole("tudeng")){
				String statement = "SELECT username, language, tasks.name, source_code FROM attempt INNER JOIN tasks ON tasks.id = attempt.task WHERE attempt.id = ? AND attempt.username = ?";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
				stmt.setString(2, request.getRemoteUser());
			}
			else if (request.isUserInRole("admin")){
				String statement = "SELECT username, language, tasks.name, source_code FROM attempt INNER JOIN tasks ON tasks.id = attempt.task WHERE attempt.id = ?";
				stmt = c.prepareStatement(statement);
				stmt.setInt(1, attemptId);
			}
			rs = stmt.executeQuery();
			if (rs.next()){
				String username = rs.getString(1);
				String fileExtension = ".py";
				// TODO Other language support
				String taskname = rs.getString(3);
				Blob file = rs.getBlob(4);
				InputStream inputstream = file.getBinaryStream();
				int filelength = inputstream.available();
				ServletContext context = getServletContext();
				taskname.replaceAll(" ", "_");
				
				String filename = username + "_" + taskname + fileExtension;
				String mimeType = context.getMimeType(filename);
				if (mimeType == null) {         
                    mimeType = "application/octet-stream";
                } 
				response.setContentType(mimeType);
				response.setContentLength(filelength);
				String headerKey = "Content-Disposition";
                String headerValue = String.format("attachment; filename=\"%s\"", filename);
                response.setHeader(headerKey, headerValue);
                OutputStream outStream = response.getOutputStream();
                
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                 
                while ((bytesRead = inputstream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                 
                inputstream.close();
                outStream.close();  				
			}
			else{
				response.getWriter().print("File not found for the id: " + attemptId); 
			}
			
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
