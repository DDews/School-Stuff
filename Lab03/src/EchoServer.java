import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class EchoServer {
	public static void main(String[] arstring) {
		try {
			System.setProperty("javax.net.ssl.keyStore", "./keystore");
		    System.setProperty("javax.net.ssl.keyStorePassword", "moondied");
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(5030);
			while (true) {
				new SSLThread((SSLSocket) sslserversocket.accept()).start();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static class SSLThread extends Thread {
		SSLSocket sslsocket;

		public SSLThread(SSLSocket socket) {
			sslsocket = socket;
		}

		public void run() {
			try {
				InputStream inputstream = sslsocket.getInputStream();
				PrintWriter out = new PrintWriter(sslsocket.getOutputStream(), true);;
				InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
				BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

				String string = null;
				while ((string = bufferedreader.readLine()) != null) {
					System.out.println(string);
					out.println(string.toUpperCase());
					out.flush();
					if (string.toUpperCase().equals("BYE")) break;
					System.out.flush();
				}
				bufferedreader.close();
				out.close();
				sslsocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
