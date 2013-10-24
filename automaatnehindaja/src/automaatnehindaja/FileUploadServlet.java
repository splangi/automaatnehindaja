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
import org.apache.log4j.Logger;

//TODO õppejõu conf
@MultipartConfig(location = "tmp", fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024, maxRequestSize = 1024 * 1024 * 2)
@WebServlet("/upload")
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(FileUploadServlet.class);

	public FileUploadServlet() {
		super();
	}

	// References:
	// http://www.avajava.com/tutorials/lessons/how-do-i-upload-a-file-to-a-servlet.html?page=1
	// References: http://docs.oracle.com/javaee/6/tutorial/doc/glrbb.html
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int taskid = Integer.parseInt(request.getParameter("id"));
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(request);
		if (!isMultipartContent) {
			response.sendRedirect("/automaatnehindaja/taskview.html?id=" + taskid +"&result=incorrect");
			return;
		}
		logger.info("Starting to upload a students solution, by: " + request.getRemoteUser());
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> fields = upload.parseRequest(request);
			Iterator<FileItem> it = fields.iterator();
			if (fields.size()!=2) {
				response.sendRedirect("/automaatnehindaja/mainpage.html#taskview?id=" + taskid +"&result=incorrect");
				return;
			}
			if (it.hasNext()) {
				Connection c = null;
				PreparedStatement stmt = null;
				FileItem fileitem = it.next();
				FileItem languageitem = it.next();
				String language = languageitem.getString();
				if (!(fileitem.getName().endsWith(".py") || fileitem.getName().endsWith(".java"))){
					logger.info("Upload failed!, notcorrect file end");
					response.sendRedirect("/automaatnehindaja/mainpage.html#taskview?id=" + taskid +"&result=incorrect");
					return;
				}
				if (fileitem.getSize()>1024*1024){
					logger.info("Upload failed!, too large");
					response.sendRedirect("/automaatnehindaja/mainpage.html#taskview?id=" + taskid +"&result=toolarge");
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/automaatnehindaja",
						"ahindaja", "k1rven2gu");
				String statement = "select username from attempt where username = ? and task = ?";
				stmt = c.prepareStatement(statement);
				stmt.setString(1, request.getRemoteUser());
				stmt.setInt(2, taskid);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()){
					stmt.close();
					statement = "UPDATE attempt SET time = ?, source_code= ?, language = ?, result = ? WHERE username = ? and task = ?;";
					stmt = c.prepareStatement(statement);
					stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
					stmt.setBinaryStream(2, fileitem.getInputStream());
					stmt.setString(3, language); // TODO support for other languages
					stmt.setString(4, "kontrollimata");
					stmt.setString(5, request.getRemoteUser());
					stmt.setInt(6, taskid);
					stmt.executeUpdate();
				}
				else {
					stmt.close();
					statement = "INSERT INTO attempt (username, task, time, source_code, language, result) VALUES (?, ?, ?, ?, ?, ?);";
					stmt = c.prepareStatement(statement);
					stmt.setString(1, request.getRemoteUser());
					stmt.setInt(2, taskid);
					stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					stmt.setBinaryStream(4, fileitem.getInputStream());
					stmt.setString(5, language); // TODO support for other languages
					stmt.setString(6, "kontrollimata");
					stmt.executeUpdate();
				}				
				response.sendRedirect("/automaatnehindaja/mainpage.html#taskview?id=" + taskid + "&result=ok");
				c.close();
			}
			logger.info("Upload succeeded!");
			
		} 
		catch (SQLException e) {
			logger.error("SQL Exception. Request by: " + request.getRemoteUser(), e);
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		catch (FileUploadException f){
			logger.error("FileUploadException. Request by: " + request.getRemoteUser(), f);
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		catch (ClassNotFoundException g){
			logger.error("ClassNotFoundEception. Request by: " + request.getRemoteUser(), g);
			response.sendRedirect("/automaatnehindaja/error.html");
		}
		  

	}

}
