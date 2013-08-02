package com.sci.idex;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import jsyntaxpane.DefaultSyntaxKit;

public final class SciIDEX implements WindowListener
{
	public static void main(String[] args)
	{
		getInstance().initUI();
	}

	//

	private static SciIDEX instance;

	private JFrame frame;

	private JMenuBar menuBar;

	private JTabbedPane tabs;

	private JMenu file;
	private JMenu edit;
	private JMenu view;

	private JMenuItem new_;
	private JMenuItem open;
	private JMenuItem save;
	private JMenuItem saveAs;
	private JMenuItem close;
	private JMenuItem exit;

	private JMenuItem preferences;

	private JMenu highlightMode;
	private JMenuItem plain;
	private JMenuItem xpath;
	private JMenuItem xml;
	private JMenuItem xhtml;
	private JMenuItem tal;
	private JMenuItem sql;
	private JMenuItem scala;
	private JMenuItem ruby;
	private JMenuItem python;
	private JMenuItem properties;
	private JMenuItem lua;
	private JMenuItem java;
	private JMenuItem javascript;
	private JMenuItem groovy;
	private JMenuItem dosBatch;
	private JMenuItem clojure;
	private JMenuItem bash;

	private PreferencesFrame prefs;
	private Font font;

	private Properties props;

	private static String cachedUserHome;

	static
	{
		cachedUserHome = System.getProperty("user.home");
	}

	public static String getDefInstallPath()
	{
		try
		{
			CodeSource codeSource = SciIDEX.class.getProtectionDomain().getCodeSource();
			File jarFile;
			jarFile = new File(codeSource.getLocation().toURI().getPath());
			return jarFile.getParentFile().getPath();
		}
		catch(URISyntaxException e)
		{
		}
		return System.getProperty("user.dir") + "//SciIDEX";
	}

	public static String getDynamicStorageLocation()
	{
		switch (getCurrentOS())
		{
		case WINDOWS:
			return System.getenv("APPDATA") + "/SciIDEX/";
		case MACOSX:
			return cachedUserHome + "/Library/Application Support/SciIDEX/";
		case UNIX:
			return cachedUserHome + "/.SciIDEX/";
		default:
			return getDefInstallPath() + "/temp/";
		}
	}

	public static OS getCurrentOS()
	{
		String osString = System.getProperty("os.name").toLowerCase();
		if(osString.contains("win"))
		{
			return OS.WINDOWS;
		}
		else if(osString.contains("nix") || osString.contains("nux"))
		{
			return OS.UNIX;
		}
		else if(osString.contains("mac"))
		{
			return OS.MACOSX;
		}
		else
		{
			return OS.OTHER;
		}
	}

	public static enum OS
	{
		WINDOWS, UNIX, MACOSX, OTHER,
	}

	private SciIDEX()
	{
		props = new Properties();

		try
		{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			catch(Exception e1)
			{
			}
		}
	}

	public void save()
	{
		if(!new File(getDynamicStorageLocation()).exists())
			new File(getDynamicStorageLocation()).mkdirs();
		File propsFile = new File(getDynamicStorageLocation(), "preferences.cfg");
		if(!propsFile.exists())
			try
			{
				propsFile.createNewFile();
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while creating the preferences file!");
			}

		if(propsFile.exists())
		{
			props.setProperty("fontName", font.getName());
			props.setProperty("fontSize", String.valueOf(font.getSize()));

			try
			{
				props.store(new FileOutputStream(propsFile), "SciIDEX Properties File");
			}
			catch(FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while saving the preferences file! The properties file cannot be found!");
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while saving the preferences file!");
			}
		}
	}

	public void load()
	{
		File propsFile = new File(getDynamicStorageLocation(), "preferences.cfg");

		if(propsFile.exists())
		{
			try
			{
				props.load(new FileInputStream(propsFile));
			}
			catch(FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while saving the preferences file! The properties file cannot be found!");
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(frame, "An error occured while saving the preferences file!");
			}
		}

		prefs.setFontName(props.getProperty("fontName"));
		prefs.setFontSize(Integer.valueOf(props.getProperty("fontSize")));
	}

	public Font getFont()
	{
		return font;
	}

	public void setFont(Font f)
	{
		this.font = f;
	}

	public void updateUI()
	{
		for(Component c : tabs.getComponents())
		{
			if(c instanceof FileTab)
			{
				FileTab tab = (FileTab) c;
				tab.setFont(font);
			}
		}
	}

	private void initUI()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Font defaultFont = new JLabel().getFont();
				font = new Font(defaultFont.getName(), Font.PLAIN, 12);

				frame = new JFrame();
				prefs = new PreferencesFrame();

				tabs = new JTabbedPane();

				frame.add(tabs);

				DefaultSyntaxKit.initKit();

				menuBar = new JMenuBar();

				file = new JMenu("File");
				edit = new JMenu("Edit");
				view = new JMenu("View");

				new_ = new JMenuItem("New");
				new_.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = new FileTab();
						tabs.addTab("", tab);
						tab.init("Untitled");
					}
				});
				open = new JMenuItem("Open");
				open.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						final JFileChooser fileChooser = new JFileChooser();
						int result = fileChooser.showOpenDialog(frame);
						if(result == JFileChooser.APPROVE_OPTION)
						{
							FileTab tab = new FileTab();
							tabs.addTab("", tab);
							tab.init(fileChooser.getSelectedFile().getName());
							tab.setFile(fileChooser.getSelectedFile());
							try
							{
								tab.load();
							}
							catch(IOException e)
							{
								JOptionPane.showMessageDialog(frame, "An error occured while loading the file!");
							}
						}
					}
				});
				save = new JMenuItem("Save");
				save.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							final JFileChooser fileChooser = new JFileChooser();
							int result = -1337;
							if(tab.getFile() == null)
							{
								result = fileChooser.showSaveDialog(frame);
								if(result == JFileChooser.APPROVE_OPTION)
								{
									tab.setFile(fileChooser.getSelectedFile());
									tab.setTitle(fileChooser.getSelectedFile().getName());
								}
							}

							if(result != JFileChooser.CANCEL_OPTION)
							{
								try
								{
									tab.save();
								}
								catch(IOException e)
								{
									JOptionPane.showMessageDialog(frame, "An error occured while saving the file!");
								}
							}
						}
					}
				});
				saveAs = new JMenuItem("Save As...");
				saveAs.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							final JFileChooser fileChooser = new JFileChooser();
							int result = fileChooser.showSaveDialog(frame);
							if(result == JFileChooser.APPROVE_OPTION)
							{
								tab.setFile(fileChooser.getSelectedFile());
								tab.setTitle(fileChooser.getSelectedFile().getName());

								try
								{
									tab.save();
								}
								catch(IOException e)
								{
									JOptionPane.showMessageDialog(frame, "An error occured while saving the file!");
								}
							}
						}
					}
				});
				close = new JMenuItem("Close");
				close.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							if(tab.isDirty())
							{
								int result = JOptionPane.showConfirmDialog(frame, "Do you wish to save the file before closing?", "Save file?", JOptionPane.YES_NO_CANCEL_OPTION);
								if(result == JOptionPane.CANCEL_OPTION)
									return;
								else if(result == JOptionPane.YES_OPTION)
								{
									if(tab.getFile() == null)
									{
										final JFileChooser fileChooser = new JFileChooser();
										result = fileChooser.showSaveDialog(frame);
										if(result == JFileChooser.APPROVE_OPTION)
										{
											tab.setFile(fileChooser.getSelectedFile());
											tab.setTitle(fileChooser.getSelectedFile().getName());
										}
										else if(result == JFileChooser.CANCEL_OPTION)
											return;
									}

									try
									{
										tab.save();
									}
									catch(IOException e)
									{
										JOptionPane.showMessageDialog(frame, "An error occured while saving the file!");
									}
								}
								tabs.remove(tab);
							}
							else
							{
								tabs.remove(tab);
							}
						}
					}
				});
				exit = new JMenuItem("Exit");
				exit.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						getInstance().windowClosing(null);
					}
				});

				file.add(new_);
				file.add(open);
				file.add(save);
				file.add(saveAs);
				file.add(close);
				file.add(exit);

				new_.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
				save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
				open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
				close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
				exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));

				preferences = new JMenuItem("Preferences");
				preferences.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						prefs.setVisible(true);
					}
				});

				edit.add(preferences);

				highlightMode = new JMenu("Highlight Mode");

				view.add(highlightMode);

				plain = new JMenuItem("Plain");
				plain.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("plain");
						}
					}
				});
				xpath = new JMenuItem("XPath");
				xpath.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("xpath");
						}
					}
				});
				xml = new JMenuItem("XML");
				xml.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("xml");
						}
					}
				});
				xhtml = new JMenuItem("XHTML");
				xhtml.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("xhtml");
						}
					}
				});
				tal = new JMenuItem("TAL");
				tal.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("tal");
						}
					}
				});
				sql = new JMenuItem("SQL");
				sql.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("sql");
						}
					}
				});
				scala = new JMenuItem("Scala");
				scala.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("scala");
						}
					}
				});
				ruby = new JMenuItem("Ruby");
				ruby.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("ruby");
						}
					}
				});
				python = new JMenuItem("Python");
				python.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("python");
						}
					}
				});
				properties = new JMenuItem("Properties");
				properties.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("properties");
						}
					}
				});
				lua = new JMenuItem("LUA");
				lua.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("lua");
						}
					}
				});
				java = new JMenuItem("Java");
				java.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("java");
						}
					}
				});
				javascript = new JMenuItem("JavaScript");
				javascript.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("javascript");
						}
					}
				});
				groovy = new JMenuItem("Groovy");
				groovy.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("groovy");
						}
					}
				});
				dosBatch = new JMenuItem("DOS Batch");
				dosBatch.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("dosbatch");
						}
					}
				});
				clojure = new JMenuItem("Clojure");
				clojure.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("clojure");
						}
					}
				});
				bash = new JMenuItem("Bash");
				bash.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						FileTab tab = (FileTab) tabs.getSelectedComponent();
						if(tab != null)
						{
							tab.setHighlightMode("bash");
						}
					}
				});

				highlightMode.add(plain);
				highlightMode.add(xpath);
				highlightMode.add(xml);
				highlightMode.add(xhtml);
				highlightMode.add(tal);
				highlightMode.add(sql);
				highlightMode.add(scala);
				highlightMode.add(ruby);
				highlightMode.add(python);
				highlightMode.add(properties);
				highlightMode.add(lua);
				highlightMode.add(java);
				highlightMode.add(javascript);
				highlightMode.add(groovy);
				highlightMode.add(dosBatch);
				highlightMode.add(clojure);
				highlightMode.add(bash);

				menuBar.add(file);
				menuBar.add(edit);
				menuBar.add(view);

				frame.setJMenuBar(menuBar);

				frame.setTitle("SciIDE X");
				frame.setSize(600, 600);
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.addWindowListener(SciIDEX.getInstance());

				load();
				prefs.loadFromProps();
				
				frame.setVisible(true);
			}
		});
	}

	public static SciIDEX getInstance()
	{
		if(instance == null)
			instance = new SciIDEX();
		return instance;
	}

	public JTabbedPane getTabs()
	{
		return tabs;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	@Override
	public void windowActivated(WindowEvent arg0)
	{
	}

	@Override
	public void windowClosed(WindowEvent arg0)
	{
	}

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		for(Component c : tabs.getComponents())
		{
			if(c instanceof FileTab)
			{
				int result = JOptionPane.showConfirmDialog(frame, "Do you wish to save \"" + ((FileTab)c).getTitle() +  "\" before closing?", "Save file?", JOptionPane.YES_NO_CANCEL_OPTION);
				if(result == JOptionPane.CANCEL_OPTION)
					return;
				else if(result == JOptionPane.YES_OPTION)
				{
					final JFileChooser fileChooser = new JFileChooser();
					result = -1337;
					if(((FileTab)c).getFile() == null)
					{
						result = fileChooser.showSaveDialog(frame);
						if(result == JFileChooser.APPROVE_OPTION)
						{
							((FileTab)c).setFile(fileChooser.getSelectedFile());
							((FileTab)c).setTitle(fileChooser.getSelectedFile().getName());
						}
						else if(result == JFileChooser.CANCEL_OPTION)
							return;
					}

					if(result != JFileChooser.CANCEL_OPTION)
					{
						try
						{
							((FileTab)c).save();
						}
						catch(IOException e)
						{
							JOptionPane.showMessageDialog(frame, "An error occured while saving the file!");
						}
					}
				}
			}
		}
		
		save();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent arg0)
	{
	}

	@Override
	public void windowIconified(WindowEvent arg0)
	{
	}

	@Override
	public void windowOpened(WindowEvent arg0)
	{
	}
}