/**
 * MultipartUtility
 */
package http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 
 * 
 */
public class MultipartUtility {

	static final String LINE_FEED = "\r\n";

	private final String boundary;
	
	private HttpURLConnection httpConn;

	private OutputStream outputStream;
	
	private PrintWriter writer;

	/**
	 * This constructor initializes a new HTTP POST request with content type is set to multipart/form-data
	 * 
	 * @param requestURL
	 * @param charset
	 * @throws IOException
	 */
	public MultipartUtility(String requestURL, String charset) throws IOException {
		// Creates a unique boundary based on time stamp
		boundary = "===" + System.currentTimeMillis() + "===";

		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("User-Agent", "CodeJava Agent");

		outputStream = httpConn.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(outputStream, charset);
		writer = new PrintWriter(osw, true);
	}

	/**
	 * Adds an upload file section to the request in incremental byte chunks
	 * 
	 * @param fieldName
	 * @param uploadFile
	 * @throws IOException
	 */
	public void addFilePart(String fieldName, File uploadFile, long startingPoint) throws IOException {
		String fileName = uploadFile.getName();
		buildContentDispositions(fieldName, fileName);

		FileInputStream inputStream = new FileInputStream(uploadFile);
		
		int bytesRead = -1;
		
		while ((bytesRead = inputStream.read()) != -1) {			
			outputStream.write(bytesRead);
		}
		
		outputStream.flush();
		inputStream.close();

		writer.append(LINE_FEED);
		writer.flush();
	}

	/**
	 * 
	 * @param fieldName
	 * @param fileName
	 */
	protected void buildContentDispositions(String fieldName, String fileName) {
		/*
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"data1\"").append(LINE_FEED).append(LINE_FEED).append(LINE_FEED);
		writer.append("7&+#3kS@e38").append(LINE_FEED); //.append(LINE_FEED);
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"data2\"").append(LINE_FEED).append(LINE_FEED).append(LINE_FEED);
		writer.append("2234PPP1$$y").append(LINE_FEED); //.append(LINE_FEED);
		*/

		// Actual file

		String fileHeader = "--" + boundary;
		fileHeader += LINE_FEED;
		fileHeader += "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"";
		fileHeader += LINE_FEED;
		fileHeader += LINE_FEED;

		System.out.println(fileHeader);

		writer.append(fileHeader);

		writer.flush();
	}
	
	/**
	 * Completes the request and receives response from the server.
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<String> finish() throws IOException {
		List<String> response = new ArrayList<String>();

		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			
			reader.close();
			httpConn.disconnect();
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}

		return response;
	}
}
