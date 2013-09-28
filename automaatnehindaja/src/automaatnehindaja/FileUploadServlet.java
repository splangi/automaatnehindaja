package automaatnehindaja;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

//TODO Õppejõu conf
@MultipartConfig(location = "/tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024, maxRequestSize = 1024 * 1024 * 2)
@WebServlet("/upload")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FileUploadServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	// References:
	// http://www.avajava.com/tutorials/lessons/how-do-i-upload-a-file-to-a-servlet.html?page=1
	// References: http://docs.oracle.com/javaee/6/tutorial/doc/glrbb.html
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(request);
		if (!isMultipartContent) {
			// TODO tell the client that there was a error in the upload
			return;
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> fields = upload.parseRequest(request);
			Iterator<FileItem> it = fields.iterator();
			System.out.println(fields.size());
			if (!it.hasNext()) {
				// TODO tell the client that there was no file in the
				// uploadstream
				return;
			}
			while (it.hasNext()) {
				Connection c = null;
				PreparedStatement stmt = null;
				FileItem fileitem = it.next();
				// TODO check if file is python file! 
				// TODO does not contain SQL Injection
				// TODO Nice to have, email to henri if suspection of SQL Injection
				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/automaatnehindaja",
						"root", "t6urott");
				String statement = "select username from attempt where username = ?";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, request.getRemoteUser());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()){
					stmt.close();
					System.out.println("update action");
					statement = "UPDATE attempt SET time = ?, source_code= ?, language = ? WHERE username = ?;";
					stmt = c.prepareStatement(statement);
					stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					stmt.setBinaryStream(2, fileitem.getInputStream());
					stmt.setString(3, "python"); // TODO support for other languages
					stmt.setString(4, request.getRemoteUser());
					stmt.executeUpdate();
				}
				else {
					stmt.close();
					System.out.println(c.isClosed());
					System.out.println("insert action");
					statement = "INSERT INTO attempt (username, task, time, source_code, language) VALUES (?, ?, ?, ?, ?);";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, request.getRemoteUser());
					stmt.setInt(2, 1); // TODO get taskID
					stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					stmt.setBinaryStream(4, fileitem.getInputStream());
					stmt.setString(5, "python"); // TODO support for other languages
					stmt.executeUpdate();
				}				
				response.sendRedirect("/automaatnehindaja/taskview.html?id=1");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Exception");
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		catch (FileUploadException f){
			f.printStackTrace();
			System.out.println("FileUploadException");
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		catch (ClassNotFoundException g){
			g.printStackTrace();
			System.out.println("ClassNotFoundException");
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		  

	}

}
