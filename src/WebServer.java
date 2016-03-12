/**
 * This is the template file for Assignment 1 Problem 2 for ECSE 414 Fall 2014.
 * 
 * This class implements a multi-threaded HTTP 1.0-compliant web server. The
 * root directory from which files are served is the same directory from which
 * this application is executed. When the server encounters an error, it sends a
 * response message with the appropriate HTML code so that the error information
 * is displayed.
 * 
 * @author michaelrabbat,syedhaq
 *
 */
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * This is the main class which runs the loop that listens for incoming requests
 * and spawns new threads to handle each request.
 * 
 * 
 * 
 */
public final class WebServer {
	public static void main(String argx[]) throws Exception {
		// Step 1: Set the port number (may not work with 80)
		int port = 6789;
		
		// Create the socket to listen for incoming connections
		
		try{
		ServerSocket welcomeSocket=new ServerSocket(port);
		
		// Enter an infinite loop and process incoming connections
		// Use Ctrl-C to quit the application
		
		
		while (true) {
			
			
			// Listen for a new TCP connection request
			Socket connectionSocket=welcomeSocket.accept();
			
			// Construct an HttpRequest object to process the request message
			HttpRequest request=new HttpRequest(connectionSocket);
			
			// Create a new thread to process the request
			// Start the thread
			
			new Thread(request).start();
			
		}
	}
		catch(Exception e){
			e.getMessage();
		}
	}
	
	
}

/**
 * This is the helper class that processes individual HTTP requests
 * 
 * 
 * 
 */
final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;
	
	/**
	 * Constructor takes the socket for this request
	 */
	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}
	
	/**
	 * Implement the run() method of the Runnable interface. 
	 */
	@Override
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * This is where the action occurs
	 * @throws Exception
	 */
	private void processRequest() throws Exception
	{
		// STEP 2a: Parse the HTTP Request message
		// Get a reference to the socket's input and output streams
		// Set up input stream filters
		BufferedReader inFromClient=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DataOutputStream outToClient=new DataOutputStream(socket.getOutputStream());
		
		
		
		
		// Get the request line of the HTTP request message
		String requestLine;
		requestLine=inFromClient.readLine();
		// Display the request line
		System.out.println();
		System.out.println(requestLine);

		// Get and display the header lines
		boolean endOfMessage=false;
		while(!endOfMessage){
			String nextLine=inFromClient.readLine();
			
			if (!nextLine.isEmpty()){
			System.out.println(nextLine);
			}
			
			else{endOfMessage=true;}
			
			
		}
			
		
		


		// (The last part of STEP 2 is at the end of this method)
		// (Close the socket)
			
		// STEP 3a: Prepare and Send the HTTP Response message
		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which we'll assume is "GET"
		String fileName = tokens.nextToken();
				
		// Prepend a "." to the file name so that the file request is in the
		// current directory
		fileName = "." + fileName;


		// Open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		
		
		// Construct the response message header
		String statusLine = null;
		String contentTypeLine = null;
		String errorMessage = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not Found</BODY></HTML>";

		// Fill in the values of statusLine and contentTypeLine based on whether
		// or not the requested file was found
		
		if(fileExists){
			statusLine="HTTP/1.0 200 OK"+CRLF;
			contentTypeLine="Content-type:"+ contentType(fileName) +CRLF;
			
			
		}
		else{
			statusLine="HTTP/1.0 404 Not Found"+CRLF;
			contentTypeLine="Content-type:text/html" +CRLF ;
		}
		
		
		// Send a HTTP response header containing the status line and
		// content-type line. Don't forget to include a blank line after the
		// content-type to signal the end of the header.
		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(contentTypeLine);
		outToClient.writeBytes(CRLF);
	
		// Send the body of the message (the web object)
		// You may use the sendBytes helper method provided
		if(fileExists){
			sendBytes(fis,outToClient);
			
		}
		else{
			outToClient.writeBytes(errorMessage);
		}

		// STEP 2b: Close the input/output streams and socket before returning
		
		inFromClient.close();
		outToClient.close();
		socket.close();
		
	}
	
	/**
	 * Private method that returns the appropriate MIME-type string based on the
	 * suffix of the appended file
	 * @param fileName
	 * @return
	 */
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		// STEP 3b: Add code here to deal with GIFs and JPEGs
		if (fileName.endsWith(".jpg")){
			return "image/jpg";
		}
		if (fileName.endsWith(".gif")){
			return "image/gif";
		}
		if (fileName.endsWith(".css")){
			return "text/css";
		}
		return "application/octet-stream";
	}

	/**
	 * Private helper method to read the file and send it to the socket
	 * @param fis
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, DataOutputStream os) 
		throws Exception 
	{
		
		// Allocate a 1k buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		
		
		}
	}
}

