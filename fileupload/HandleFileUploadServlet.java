package com.java.web.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class HandleFileUploadServlet
 */
@MultipartConfig
@WebServlet(description = "HandleFileUploadServlet", urlPatterns = { "/HandleFileUpload/StreamBytes" })
public class HandleFileUploadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final String PATH = File.separator + "Temp" + File.separator + "Upload";
	
	private String root;
	
	private String path;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HandleFileUploadServlet() {
        super();
    	// 1: HandleFileUploadServlet - only happens once
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
    	// 2: INIT - only happens once
		root = File.listRoots()[0].getAbsolutePath();
		path = root + PATH;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Ensure that it is a multipart request first
		if (ServletFileUpload.isMultipartContent(request)) {
			System.out.println("[Is Multipart Request]");

			// Get the file part HTTP header
			Part filePart = request.getPart("file");
		    String fileName = filePart.getSubmittedFileName();
		    System.out.println("[FILENAME] " + fileName);

		    // Set up the final file destination
		    String filePath = path + File.separator + fileName;
		    System.out.println("[PATH] " + filePath);		    
		    File file = new File(filePath);
		    file.getParentFile().mkdirs();

	        if (!file.exists()){
	            file.createNewFile();
	        }
	
		    OutputStream out = new FileOutputStream(file);
		    InputStream fileContent = filePart.getInputStream();
		    
		    // Do the actual write
		    IOUtils.copy(fileContent, out);
		    
		    out.flush();
		    out.close();
		    fileContent.close();
		    
		    response.setStatus(HttpServletResponse.SC_OK);
			doGet(request, response);
		}
		else {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head><title>Servlet upload</title></head>");
			out.println("<body><p>No file uploaded</p></body>");
			out.println("</html>");
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		}		
	}
}
