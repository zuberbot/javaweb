package com.java.web.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HandleFileDownloadServlet
 */
@WebServlet(description = "HandleFileDownloadServlet", urlPatterns = { "/HandleFileDownload/DownloadFile" })
public class HandleFileDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PATH = File.separator + "Temp" + File.separator + "Zip";
	
	private String root;
	
	private String path;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HandleFileDownloadServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		root = File.listRoots()[0].getAbsolutePath();
		path = root + PATH;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get the file to download HTTP URL parameter
		String downloadFileName = request.getParameter("file");
		String filePath = path + File.separator + downloadFileName;
		System.out.println("[DOWNLOAD FILE PATH] " + filePath);

		// Set the download filename/browser tab title
		response.setHeader("Content-Disposition", "inline; filename=\"" + downloadFileName + '\"');

		// Then read it and stream it to response
		File file = new File(filePath);
		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(file);
			OutputStream responseOutputStream = response.getOutputStream();
			int bytes;

			while ((bytes = fileInputStream.read()) != -1) {
				responseOutputStream.write(bytes);
			}

			System.out.println("[DOWNLOAD COMPLETE] " + filePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
