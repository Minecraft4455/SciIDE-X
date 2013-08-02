package com.sci.idex;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PreferencesFrame extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;

	private JComboBox<String> fontName;
	private JSpinner fontSize;

	private JButton restoreDefaults;

	public PreferencesFrame()
	{
		setTitle("Preferences");
		setSize(400, 300);
		setResizable(false);
		setLocationRelativeTo(SciIDEX.getInstance().getFrame());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(null);

		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts();
		String[] fontNames = new String[fonts.length];
		for(int i = 0; i < fonts.length; i++)
			fontNames[i] = fonts[i].getName();

		fontName = new JComboBox<String>(fontNames);
		Font defaultFont = new JLabel().getFont();
		fontName.setSelectedItem(defaultFont.getName());
		fontName.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setFontName((String) fontName.getSelectedItem());
			}
		});

		fontSize = new JSpinner();
		fontSize.setModel(new SpinnerNumberModel(12, 6, 80, 1));
		fontSize.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				setFontSize((Integer) fontSize.getValue());
			}
		});

		fontName.setBounds(50, 12, 200, 24);
		add(fontName);

		fontSize.setBounds(300, 12, 88, 24);
		add(fontSize);

		JLabel font = new JLabel("Font: ");
		font.setBounds(12, 12, 100, 24);
		add(font);

		JLabel size = new JLabel("Size: ");
		size.setBounds(270, 12, 100, 24);
		add(size);

		restoreDefaults = new JButton("Restore Defaults");
		restoreDefaults.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Font defaultFont = new JLabel().getFont();
				SciIDEX.getInstance().setFont(new Font(defaultFont.getName(), Font.PLAIN, 12));
				
				fontName.setSelectedItem(defaultFont.getName());
				fontSize.setValue(12);
			}
		});
		
		restoreDefaults.setBounds(255, 240, 140, 24);
		add(restoreDefaults);

		addWindowListener(this);
	}

	public void loadFromProps()
	{
		fontName.setSelectedItem(SciIDEX.getInstance().getFont().getName());
		fontSize.setValue(SciIDEX.getInstance().getFont().getSize());
	}
	
	public void setFontName(String name)
	{
		Font f = SciIDEX.getInstance().getFont();
		SciIDEX.getInstance().setFont(new Font(name, f.getStyle(), f.getSize()));
	}

	public void setFontSize(int size)
	{
		Font f = SciIDEX.getInstance().getFont();
		SciIDEX.getInstance().setFont(new Font(f.getName(), f.getStyle(), size));
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
		setVisible(false);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SciIDEX.getInstance().updateUI();
			}
		});
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