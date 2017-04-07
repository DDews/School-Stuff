import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class InfoCollectionServer {
	public static void main(String[] arstring) {
		if (arstring.length != 1) {
			System.err.println("Incorrect usage. Please provide port number: java InfoCollectionServer [port number]");
			System.exit(0);
		}
		int port = 0;
		try {
			port = Integer.parseInt(arstring[0]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid port number: java InfoCollectionServer [port number]");
			System.exit(0);
		}
		try {
			System.setProperty("javax.net.ssl.keyStore", "./keystore");
			System.setProperty("javax.net.ssl.keyStorePassword", "moondied");
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
			while (true) {
				new SSLThread((SSLSocket) sslserversocket.accept()).start();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static class SSLThread extends Thread {
		SSLSocket sslsocket;
		String filename = "unknown.txt";

		public SSLThread(SSLSocket socket) {
			sslsocket = socket;
		}

		public void run() {
			try {
				InputStream inputstream = sslsocket.getInputStream();
				PrintWriter out = new PrintWriter(sslsocket.getOutputStream(), true);
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
				SSLSession session = sslsocket.getSession();
				System.out.println("Peer host is " + session.getPeerHost());
				System.out.println("Cipher suite is " + session.getCipherSuite());
				System.out.println("Protocol is " + session.getProtocol());
				System.out.println("Session ID is " + bytesToHex(session.getId()));
				System.out.println("The creation time of this session is " + new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(session.getCreationTime())));
				System.out.println("The last accessed time of this session is " + new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(session.getLastAccessedTime())));
				BufferedWriter file;
				boolean cont = true;
				String input;
				do {
					out.write("User Name: \n");
					out.flush();
					input = bufferedreader.readLine();
					filename = input + ".txt";
					System.out.println("\nCreating file " + filename + " ...");
					file = new BufferedWriter(new FileWriter(new File(filename)));
					file.write("User Name: " + input + "\n");
					out.write("Full Name: \n");
					out.flush();
					input = bufferedreader.readLine();
					file.write("Full Name: " + input + "\n");
					out.write("Address: \n");
					out.flush();
					input = bufferedreader.readLine();
					file.write("Address: " + input + "\n");
					out.write("Phone Number: \n");
					out.flush();
					input = bufferedreader.readLine();
					file.write("Phone Number: " + input + "\n");
					out.write("Email address: \n");
					out.flush();
					input = bufferedreader.readLine();
					file.write("Email address: " + input + "\n");
					out.write("Add more users? (yes or any for no) \n");
					out.flush();
					file.flush();
					file.close();
					input = bufferedreader.readLine();
					cont = input.toLowerCase().equals("yes");
				} while (cont);
				bufferedreader.close();
				out.close();
				sslsocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
		public static String bytesToHex(byte[] bytes) {
		    char[] hexChars = new char[bytes.length * 2];
		    for ( int j = 0; j < bytes.length; j++ ) {
		        int v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
	}
}