
import java.awt.ComponentOrientation;
import java.awt.Dimension;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

public class SLUpdater extends JFrame implements DropTargetListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String NULL_KEY = "00000000-0000-0000-0000-000000000000";
	String filepath = "";
	String itemsearch = "";
	ArrayList<String> items;
	private JTextField filename;
	private JScrollPane scroll;
	private JProgressBar loading;
	private JButton go;
	private JButton copy;
	private JButton file;
	private JTextArea textarea;
	private JTextField item;
	private JComboBox<String> itemname;
	private ArrayList<File> foundFiles;
	boolean working = false;

	public SLUpdater() throws IOException {
		super("SLUpdater");

		/*
		 * Process p = Runtime.getRuntime().exec("jps"); BufferedReader
		 * processes = new BufferedReader(new
		 * InputStreamReader(p.getInputStream())); String process; int running =
		 * 0; while ((process = processes.readLine()) != null) { if
		 * (process.endsWith("SLUpdater")) running++; } if (running > 1) {
		 * showError("SLUpdater already running!"); System.exit(1); }
		 */
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		items = new ArrayList<String>();
		foundFiles = new ArrayList<File>();
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		JPanel veryTopPanel = new JPanel();
		JPanel middlePanel = new JPanel();
		JPanel veryBottomPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
		veryTopPanel.setLayout(new BoxLayout(veryTopPanel, BoxLayout.X_AXIS));
		loading = new JProgressBar();
		go = new JButton("Go");
		go.setEnabled(true);
		go.setMinimumSize(new Dimension(80, 30));
		loading.setMinimum(0);
		loading.setMaximum(100);
		copy = new JButton("Copy");
		file = new JButton("Load:");
		item = new JTextField("Update: ");
		item.setEditable(false);
		item.setMinimumSize(new Dimension(80, 30));
		item.setBorder(null);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		itemname = new JComboBox<String>();
		itemname.setMaximumSize(new Dimension(300, 30));
		textarea = new JTextArea("", 15, 30);
		textarea.setLineWrap(true);
		textarea.setEditable(true);
		@SuppressWarnings("unused")
		DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
				this, true);
		@SuppressWarnings("unused")
		DropTarget dt2 = new DropTarget(textarea,
				DnDConstants.ACTION_COPY_OR_MOVE, this, true);
		scroll = new JScrollPane(textarea);
		scroll.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		topPanel.add(item);
		topPanel.add(itemname);
		topPanel.add(go);
		topPanel.setMinimumSize(new Dimension(80 * 2 + 400, 30));
		veryTopPanel.add(file);
		filepath = findFile();
		filename = new JTextField(filepath);
		filename.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				textarea.getHighlighter().removeAllHighlights();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				textarea.getHighlighter().removeAllHighlights();

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		// filename.setTransferHandler(handler);
		filename.setEditable(false);

		veryTopPanel.add(filename);
		middlePanel.add(loading);
		middlePanel.add(scroll);
		veryBottomPanel.add(copy);
		go.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					updateTextArea(itemsearch);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		});
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textarea.getHighlighter().addHighlight(0,
							textarea.getText().length(),
							DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				StringSelection stringSelection = new StringSelection(
						textarea.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
				showMessage("Copied to clipboard.");
			}
		});
		itemname.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				itemsearch = (String) itemname.getSelectedItem();
			}

		});
		file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File(
						FileSystemView.getFileSystemView().getHomeDirectory()
								.getAbsolutePath() + "\\..\\Downloads"));
				int value = jfc.showOpenDialog(null);
				if (value == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					if (selectedFile.getName().endsWith(".csv"))
						filepath = selectedFile.getAbsolutePath();
					else {
						showError("Invalid file. Needs to be a .csv file.");
						return;
					}
					try {
						updateTextArea(itemsearch);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					filename.setText(filepath);
				}
			}
		});

		this.getContentPane().add(veryTopPanel);
		this.getContentPane().add(topPanel);
		this.getContentPane().add(middlePanel);
		this.getContentPane().add(bottomPanel);
		this.getContentPane().add(veryBottomPanel);
		updateComboBox();
		this.pack();
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private String trimName(String name) {
		return name.toLowerCase().replaceAll(" ", ".").replaceAll(".resident",
				"");
	}

	private String getName(String name) {
		if (!name.contains(" "))
			return name.toLowerCase() + ".resident";
		else
			return name.toLowerCase().replaceAll(" ", ".");
	}

	public void updateComboBox() {
		if (working) {
			showError("Still processing from previous request.");
			return;
		}
		working = true;
		itemname.removeAllItems();
		itemname.setEditable(false);
		itemname.setEnabled(true);
		items = new ArrayList<String>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				File f = new File(filepath);
				@SuppressWarnings("unused")
				ArrayList<String> names = new ArrayList<String>();
				@SuppressWarnings("unused")
				ArrayList<String> keys = new ArrayList<String>();
				try {
					// System.out.println(f.toString());
					Scanner in = new Scanner(f);
					String line = in.nextLine();
					ArrayList<String> params = new ArrayList<String>();
					String[] parameters = line.split(",");
					for (int i = 0; i < parameters.length; i++) {
						params.add(parameters[i]);
					}
					HashMap<String, String> info;
					while (in.hasNextLine()) {
						info = new HashMap<String, String>();
						line = in.nextLine();
						String[] lines = line.split(",");
						for (int i = 0; i < lines.length; i++) {
							String string = lines[i];
							info.put(params.get(i), string);
						}
						if (!items.contains(
								info.get("Item").replaceAll("\"", ""))) {
							items.add(info.get("Item").replaceAll("\"", ""));
						}
					}
					items.sort(new Comparator<String>() {

						@Override
						public int compare(String arg0, String arg1) {
							return arg0.compareTo(arg1);
						}
					});
					for (String item : items) {
						itemname.addItem(item);
					}
					itemname.setSelectedItem(items.get(0));
					itemsearch = items.get(0);
					working = false;
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}).start();
	}

	public void updateTextArea(String item) throws IOException {
		if (working) {
			showError("Still processing from previous request. Pleas wait");
			return;
		}
		textarea.setText(
				"//  This should be generated by the SLUpdater.jar\n//  DO NOT EDIT THIS YOURSELF.\n");
		textarea.setEditable(false);
		textarea.setSize(400, 300);
		textarea.setAutoscrolls(true);
		loading.setValue(0);
		loading.setString("Getting keys...");
		loading.setStringPainted(true);
		working = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				File f = new File(filepath);
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<String> errors = new ArrayList<String>();
				try {
					// System.out.println(f.toString());
					Scanner in = new Scanner(f);
					String line = in.nextLine();
					ArrayList<String> params = new ArrayList<String>();
					for (String string : line.split(",")) {
						params.add(string);
					}
					HashMap<String, String> info;
					while (in.hasNextLine()) {
						info = new HashMap<String, String>();
						line = in.nextLine();
						int i = 0;
						for (String string : line.split(",")) {
							info.put(params.get(i++), string);
						}
						if (info.get("Item").contains(item)
								&& info.get("State").equals("Delivered")) {
							String name = info.get("Recipient").replaceAll("\"",
									"");
							if (!names.contains(name))
								names.add(name);
						}
					}
					int nameindex = 0;
					for (String name : names) {
						BufferedReader rd = null;
						try {
							URL url = new URL(
									"http://vwrsearch.secondlife.com/client_search.php?session="
											+ NULL_KEY + "&q="
											+ name.replace(" ", "%20"));
							HttpURLConnection connection = (HttpURLConnection) url
									.openConnection();
							connection.setRequestMethod("GET");

							// Get Response
							InputStream is = connection.getInputStream();
							rd = new BufferedReader(new InputStreamReader(is));
							String text;
							StringBuilder h3 = new StringBuilder();
							while ((text = rd.readLine()) != null) {
								// System.out.println(text);
								if (text.contains("result_title")) {
									String key = "";
									while ((text = rd.readLine()) != null) {
										h3.append(text);
										if (text.contains("</h3>"))
											break;
									}
									Matcher m = Pattern
											.compile(
													"resident\\/([a-zA-Z0-9\\-]*)\\\"")
											.matcher(h3.toString()
													.toLowerCase());
									System.out.println("matching for "
											+ trimName(name).replaceAll("\\.",
													"[\\\\.\\\\s]+")
											+ ": " + h3.toString());
									if (m.find()) {
										key = m.group(1);
									}
									Matcher k = Pattern
											.compile(trimName(name)
													.replaceAll("\\.",
															"[\\\\.\\\\s]+"))
											.matcher(h3.toString()
													.toLowerCase());
									if (k.find()) {
										if (!keys.contains(getName(name)
												+ ", " + key)) {
											keys.add(getName(name) + ", "
													+ key);
											loading.setValue(Math
													.round(((float) nameindex
															/ names.size())
															* 100));
										}
									} else {
										Matcher groupUrl = Pattern
												.compile(
														"\\\"(http://world\\.secondlife\\.com/group/[a-zA-Z\\-0-9]*)\\\"")
												.matcher(h3.toString());
										if (groupUrl.find()) {
											String groupkey = getKeyFromURL(name,
													groupUrl.group(1));
											key = groupkey;
											if (!keys.contains(getName(name) + ", "
													+ groupkey)) {
												keys.add(
														getName(name) + ", " + groupkey);
												loading.setValue(Math
														.round(((float) nameindex
																/ names.size())
																* 100));
											}
										}
									}
									if (key.isEmpty()) {
										errors.add(name);
										System.err.println("couldn't find key for " + name);
									}
								}
							}
							if (!errors.isEmpty()) {
								showError("Warning: Could not find keys for these residents: \n" + errors);
							}
							rd.close();
						} catch (MalformedURLException e) {
							showError(e.getMessage());
							e.printStackTrace();
							if (rd != null)
								try {
									rd.close();
								} catch (IOException e1) {
									showError(e.getMessage());
									e1.printStackTrace();
									return;
								}
						} catch (UnknownHostException e2) {
							showError(
									"You must be connected to the internet to get keys.");
							e2.printStackTrace();
							return;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							showError(e.getMessage());
							e.printStackTrace();
							return;
						}
						nameindex++;
					}
					in.close();
				} catch (Exception e) {
					showError(e.getMessage());
					e.printStackTrace();
				}
				for (String key : keys) {
					textarea.append(key + "\n");
				}
				loading.setValue(100);
				loading.setStringPainted(false);
				working = false;
			}

		}).start();
	}

	protected String getKeyFromURL(String name, String url) throws Exception {
		URL urlpath = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlpath
				.openConnection();
		connection.setRequestMethod("GET");

		// Get Response
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String text;
		while ((text = rd.readLine()) != null) {
			// System.out.println(text);
			// System.out.println(trimName(name).replaceAll("\\.","\\[\\\\.\\s\\]+"));
			Matcher m = Pattern
					.compile("\\/resident\\/([a-zA-Z\\-0-9]*)[\\'\\\"].*"
							+ trimName(name).replaceAll("\\.",
									"\\[\\\\.\\s\\]+")
							+ "\\)")
					.matcher(text);
			if (m.find()) {
				return m.group(1);
			}
		}
		rd.close();
		return "";
	}

	public String findFile() {
		findFile("orders(\\(.*\\))*\\.csv",
				new File(FileSystemView.getFileSystemView().getHomeDirectory()
						.getAbsolutePath() + "\\..\\Downloads"));
		long modified = 0;
		File found = new File("");
		for (File f : foundFiles) {
			if (f.lastModified() > modified) {
				modified = f.lastModified();
				found = f;
			}
		}
		return found.getAbsolutePath();
	}

	private String findFile(String name, File file) {
		File[] files = file.listFiles();
		// System.out.println(Arrays.toString(files));
		if (files == null)
			return "";
		for (File f : files) {
			if (f.isDirectory())
				continue;
			Matcher m = Pattern.compile(name).matcher(f.getName());
			if (m.find()) {
				foundFiles.add(f);
			}
		}
		return "";
	}

	private void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Clipboard",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showError(String error) {
		JOptionPane.showMessageDialog(this, error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		SLUpdater updater = new SLUpdater();
	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("...");
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent arg0) {
		arg0.acceptDrop(arg0.getDropAction());
		try {
			filepath = "";
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>) arg0.getTransferable()
					.getTransferData(DataFlavor.javaFileListFlavor);
			long modified = 0;
			for (File f : files) {
				System.out.println(f);
				if (f.isDirectory() && files.size() == 1) {
					findFile("orders(\\(.*\\))*\\.csv", f);
					for (File file : foundFiles) {
						if (file.getName().endsWith(".csv") && file.isFile()
								&& file.lastModified() > modified) {
							modified = file.lastModified();
							filepath = file.getAbsolutePath();
						}
					}
					filename.setText(filepath);
				}
				if (f.isFile() && f.getName().endsWith(".csv")
						&& f.lastModified() > modified) {
					filepath = f.getAbsolutePath();
					filename.setText(filepath);
					modified = f.lastModified();
				}
			}
			if (!filepath.isEmpty())
				updateTextArea(itemsearch);
		} catch (Exception e) {
			showError(e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub

	}

}