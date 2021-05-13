/*
 * Implementation of a simple Http client in java
 * By Srihari Nelakuditi for CSCE 416 - Edited by Victoria Condos
 */

// Package for I/O related stuff
import java.io.*;

// Package for socket related stuff
import java.net.*;

	//TODO Print HTTP Header Info to a fIle HttpClientOutput (code should handle http redirects (i.e. if a website redirects to another location, should GET from new location)(i.e. http://cse.sc.edu redirects to https:// cse.sc.edu) 
	//TODO Use Wireshark to capture HTTP, GET packets, HTTP response packets, and measure delay between GET and response (while using HttpClient)
		//TODO Run Wireshark to capture packets from an Ethernet or WiFi interface
		//TODO Apply the filter http in wireshark to filter only HTTP packets
		//TODO run an instance of your HttpClient program and see the output of wireshark (TAKE .jpg SNAPSHOT OF THE OUTPUT
		//TODO GET large files and measure how long it takes between HTTP GET and response packets. run $ java HttpClient http://xcal1.vodafone.co.uk/5MB.zip, $ java HttpClient http://xcal1.vodafone.co.uk/10MB.zip, $ java HttpClient http://xcal1.vodafone.co.uk/20MB.zip and calculate delay (storing delats in file HttpClientTime 
		

/*
 * This class does all the client's job
 * It opens a connection given the URL,
 * gets the file and prints headers and data.
 * In case of redirect, gets from the new location.
 */
public class HttpClient
{

	// The client program starts from here
	public static void main(String args[])
	{
		// Client needs a URL to get
		if (args.length != 1) {
			System.out.println("usage: java HttpClient <URL>");
			System.exit(1);
		}

		// Start with given location (first argument)
		String location = args[0];

		try {
			// Initialize BufferedWriter
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter("HttpClientOutput.txt"));
			// Need a handle for the URL connection
			HttpURLConnection httpConn = null;
			
			/* Keep following redirects */
			while (true) {
				/* Create a url */
				URL url = new URL(location);

				/* Make a http connection */
				httpConn = (HttpURLConnection) url.openConnection();

				// Display the location
				System.out.println("==== URL: " + location);
				
				// Display the status (first header field)
				System.out.println("==== Status: " + httpConn.getHeaderField(0));

				
				// Done if response code is not a redirect
				int code = httpConn.getResponseCode();
				if (!(code == HttpURLConnection.HTTP_MOVED_PERM ||
						code == HttpURLConnection.HTTP_MOVED_TEMP ||
						code == HttpURLConnection.HTTP_SEE_OTHER))
					break;

				// Get hold of the new Location
				location = httpConn.getHeaderField("Location");
				location = URLDecoder.decode(location, "UTF-8");

				// Disconnect the current url connection
				httpConn.disconnect();
			}

			// Iterate and print each header field and value
			for (int j = 1; ; j++) {
				String headerKey = httpConn.getHeaderFieldKey(j);
				String headerVal = httpConn.getHeaderField(j);
				if (headerKey == null && headerVal == null) break;
				System.out.println(headerKey + ": " + headerVal);
				//write header info to the output file
				outputWriter.write(headerKey + ": " + headerVal + "\n");
			
			}
			System.out.println();
			
			// Prepare to read the response
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(httpConn.getInputStream()));

			outputWriter.write("Content: ");			
			// Read and display the response
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				//write content to the output file
				outputWriter.write(line);
			}

			// All done, close everything
			reader.close();
			outputWriter.close();
			httpConn.disconnect();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}
