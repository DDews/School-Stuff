import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class InfoCollectionClient {
    public static int port;
    public static String server;
	public static void main(String[] arstring) {
		System.setProperty("javax.net.ssl.trustStore", "./truststore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "moondied");
    	if (arstring.length  == 2) {
    		try {
    			server = arstring[0];
    			port = Integer.parseInt(arstring[1]);
    			if (port < 0) {
    				System.out.println("Please use an integer greater than 0 for a port number.");
    				System.exit(0);
    			}
    		} catch (NumberFormatException e) {
    			System.out.println("Usage: java InfoCollectionClient <ip> <port number>");
    			System.exit(0);
    		}
    	} else {
    		System.out.println("Usage: java InfoCollectionClient <ip> <port number>");
    		System.exit(0);
    	}
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(server, port);

            InputStream inputstream = System.in;
            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

            OutputStream outputstream = sslsocket.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
            BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);

            BufferedReader socketIn = new BufferedReader(new InputStreamReader(sslsocket.getInputStream()));
            boolean cont = true;
            String from = "";
            String to = "";
            do {
            	from = socketIn.readLine();
            	System.out.print(from);
            	to = bufferedreader.readLine();
            	if (from.equals("Add more users? (yes or any for no) ")) cont = to.toLowerCase().equals("yes");
            	bufferedwriter.write(to + "\n");
            	bufferedwriter.flush();
            } while(cont);
            outputstream.close();
            bufferedwriter.close();
            bufferedreader.close();
            sslsocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
