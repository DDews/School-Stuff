import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class EchoClient {
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
    			System.out.println("Usage: EchoClient <ip> <port number>");
    			System.exit(0);
    		}
    	} else {
    		System.out.println("Usage: EchoClient <ip> <port number>");
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
            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                bufferedwriter.write(string + '\n');
                bufferedwriter.flush();
                String reply;
                System.out.println();
                if ((reply = socketIn.readLine()) != null) {
                	System.out.println(reply);
                } else {
                	System.out.println("[server replied with nothing.]");
                }
                System.out.println();
                if (string.toUpperCase().equals("BYE")) break;
            }
            outputstream.close();
            bufferedwriter.close();
            bufferedreader.close();
            sslsocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
