package com.sci.idex;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class FileTab extends JPanel
{
	private static final long serialVersionUID = 1L;

	private File f;
	private JEditorPane text;
	private JScrollPane scroll;
	private String mode;
	private boolean dirty;

	private JPanel tabPanel;
	private JLabel title;

	public FileTab()
	{
		this.setLayout(new BorderLayout());

		text = new JEditorPane();
		scroll = new JScrollPane(text);

		text.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				dirty = true;
			}

			@Override
			public void keyReleased(KeyEvent arg0)
			{
				dirty = true;
			}

			@Override
			public void keyTyped(KeyEvent arg0)
			{
				dirty = true;
			}
		});

		setHighlightMode("plain");

		setFont(SciIDEX.getInstance().getFont());

		add(scroll);
	}

	public void setFont(Font f)
	{
		super.setFont(f);
		if(text != null)
			text.setFont(f);
	}

	public void save() throws IOException
	{
		if(f != null)
		{
			Path path = f.toPath();
			try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
			{
				String[] lines = text.getText().split("\n");
				for(int i = 0; i < lines.length; i++)
				{
					writer.write(lines[i]);
					if(i != lines.length - 1)
						writer.newLine();
				}
			}
			dirty = false;
		}
	}

	public void load() throws IOException
	{
		if(f != null)
		{
			URI uri = f.toURI();
			byte[] bytes = Files.readAllBytes(Paths.get(uri));
			text.setText(new String(bytes, "UTF-8"));
		}
	}

	public void setHighlightMode(String mode)
	{
		this.mode = mode;
		String temp = text.getText();
		text.setContentType("text/" + mode);
		text.setText(temp);
	}

	public String getHighlightMode()
	{
		return mode;
	}

	public void setFile(File f)
	{
		this.f = f;
	}

	public File getFile()
	{
		return f;
	}

	public void setTitle(String string)
	{
		title.setText(string);
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void init(String name)
	{
		int i = SciIDEX.getInstance().getTabs().indexOfComponent(this);
		tabPanel = new JPanel();
		tabPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		title = new JLabel(name);
		tabPanel.add(title);
		tabPanel.add(new CloseButton(this));

		SciIDEX.getInstance().getTabs().setTabComponentAt(i, tabPanel);
	}

	private class CloseButton extends JButton implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		private FileTab tab;

		public CloseButton(FileTab tab)
		{
			this.tab = tab;
			setOpaque(false);
			setPreferredSize(new Dimension(17, 17));
			setUI(new BasicButtonUI());
			setContentAreaFilled(false);
			setFocusable(false);
			setBorder(BorderFactory.createEtchedBorder());
			setBorderPainted(false);
			addActionListener(this);
		}

		public void updateUI()
		{
		}

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			if(getModel().isPressed())
			{
				g2.translate(1, 1);
			}
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
			if(getModel().isRollover())
			{
				g2.setColor(Color.MAGENTA);
			}
			int delta = 6;
			g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
			g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			g2.dispose();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(tab.isDirty())
			{
				int result = JOptionPane.showConfirmDialog(SciIDEX.getInstance().getFrame(), "Do you wish to save the file before closing?", "Save file?", JOptionPane.YES_NO_CANCEL_OPTION);
				if(result == JOptionPane.CANCEL_OPTION)
					return;
				else if(result == JOptionPane.YES_OPTION)
				{
					if(tab.getFile() == null)
					{
						final JFileChooser fileChooser = new JFileChooser();
						result = fileChooser.showSaveDialog(SciIDEX.getInstance().getFrame());
						if(result == JFileChooser.APPROVE_OPTION)
						{
							tab.setFile(fileChooser.getSelectedFile());
							tab.setTitle(fileChooser.getSelectedFile().getName());
						}
					}

					try
					{
						tab.save();
					}
					catch(IOException ee)
					{
						JOptionPane.showMessageDialog(SciIDEX.getInstance().getFrame(), "An error occured while saving the file!");
					}
				}
				SciIDEX.getInstance().getTabs().remove(tab);
			}
			else
			{
				SciIDEX.getInstance().getTabs().remove(tab);
			}
		}
	}

	public String getTitle()
	{
		return title.getText();
	}
}