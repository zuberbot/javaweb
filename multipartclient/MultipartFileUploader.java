/**
 * MultipartFileUploader
 */
package http;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author chuck
 * 
 */
public class MultipartFileUploader {

	static final String CHAR_SET = "UTF-8";
	
	static final String URL_POST = "http://localhost:8080/JavaWeb/HandleFileUpload/StreamBytes";
	
	static final String FILE_TO_UPLOAD = File.listRoots()[0].getAbsolutePath() + File.separator + "Temp"+ File.separator + "bible_verse.zip";
	
	static final String FILE_PART_NAME = "file";
	
	/**
	 * @param args
	 */
	public static void main(String... args) {
		File uploadFile = new File(FILE_TO_UPLOAD);		

		try {
			MultipartUtility multipart = new MultipartUtility(URL_POST, CHAR_SET);

			// Server request calls are actually made here
			multipart.addFilePart(FILE_PART_NAME, uploadFile, 0);

			// Receives response from the server
			List<String> response = multipart.finish();

			System.out.print("=> SERVER REPLIED: ");
			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
