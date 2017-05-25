import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.Timer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

public class main extends JWindow {
	final Point offset = new Point();
	int size = 8;
	JTextArea temp;
	public main() {
		temp = new JTextArea("80°F");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.add(temp);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.getContentPane().add(panel);
		this.setLocationRelativeTo(null);
		temp.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mousePressed(final MouseEvent e) {
	            offset.setLocation(e.getPoint());
	        }
	    });
	    temp.addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(final MouseEvent e) {
	            setLocation(e.getXOnScreen()-offset.x, e.getYOnScreen()-offset.y);
	        }
	    });
	    temp.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				temp.setFont(new Font("Arial", Font.PLAIN, (size += arg0.getWheelRotation())));
				main.this.pack();
				main.this.setVisible(true);
			}
	    	
	    });
		/*this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Already there
	    this.setExtendedState();
	    this.setUndecorated(true);*/
	    Timer timer = new Timer(60000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				 updateTemp();
			}
	    	
	    });
	    updateTemp();
	    timer.start();
		this.pack();
		this.setVisible(true);
		this.setAlwaysOnTop( true );
		this.setFocusableWindowState(true);
		this.setFocusable(true);
	}
	public void updateTemp() {
		String sURL = "http://api.openweathermap.org/data/2.5/weather?APPID=c00e12e043f225652ee574d7f05e39bb&units=imperial&q=Denver,us"; //just a string

		 try {
	    // Connect to the URL using java's native library
	    URL url = new URL(sURL);
	    HttpURLConnection request = (HttpURLConnection) url.openConnection();
	    request.connect();

	    // Convert to a JSON object to print data
	    JsonParser jp = new JsonParser(); //from gson
	    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
	    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
	    temp.setText(rootobj.get("main").getAsJsonObject().get("temp").getAsString() + "°F"); //just grab the zipcode
		 } catch (Exception err) {
			 err.printStackTrace();
		 }
	}
	public static void main(String[] args) {
		main window = new main();
	}
}
