package com.unknownsilicon.gpfs.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.lingala.zip4j.exception.ZipException;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JList;

import org.unknownsilicon.gpfs.GooglePhotosFileStorage;
import org.unknownsilicon.gpfs.photosAPI.DuplicateNameException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class GPSFSGUI {

	private JFrame frame;
	private GooglePhotosFileStorage gpfs;
	private JFileChooser fileChooser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GPSFSGUI window = new GPSFSGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GPSFSGUI() {
		gpfs = new GooglePhotosFileStorage();
		fileChooser = new JFileChooser();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 784, 598);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		DefaultListModel<String> fileListModel = new DefaultListModel<>(); // Populate the list
		
		updateList(fileListModel);
		
		JList<String> fileList = new JList<String>(fileListModel);
		frame.getContentPane().add(fileList, "2, 2, 8, 1, fill, fill");
		
		JButton buttonUpload = new JButton("Upload");
		buttonUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Show file dialog screen
				File f = getFile();
				
				if (f == null) {
					return;
				}
				
				try {
					gpfs.upload(f);
					
					updateList(fileListModel);
				} catch (NoSuchAlgorithmException | IOException | ZipException | InterruptedException e) { // TODO: Actually make this show something useful
					e.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(buttonUpload, "2, 4");
		
		JButton buttonDownload = new JButton("Download");
		buttonDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String name = fileList.getSelectedValue();
				String id = getId(name);
				try {
					// Open file dialog
					File outputDir = saveFile();
					gpfs.download(id, outputDir);
				} catch (NoSuchAlgorithmException | IOException | ZipException | InterruptedException
						| DuplicateNameException e) {
					e.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(buttonDownload, "4, 4");
		
		JButton buttonDelete = new JButton("Delete");
		buttonDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedFile = fileList.getSelectedValue();
				
				String id = getId(selectedFile);
				
				try {
					gpfs.deleteFile(id);
				} catch (DuplicateNameException e1) {
					e1.printStackTrace();
				}
				
				updateList(fileListModel);
			}
		});
		frame.getContentPane().add(buttonDelete, "6, 4");
		
		JButton buttonList = new JButton("Update List");
		buttonList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateList(fileListModel);
			}
		});
		
		frame.getContentPane().add(buttonList, "8, 4");
	}
	
	private void updateList(DefaultListModel<String> model) {
		ArrayList<String> files = gpfs.listFiles();
		
		model.clear();
		
		for (String f : files) {
			model.addElement(f);
		}
	}
	
	private String getId(String fullName) { // ab*c
		String id = fullName.substring(fullName.lastIndexOf("*"));
		return id;
	}
	
	private File getFile() {
		int status = fileChooser.showOpenDialog(frame);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			return file;
		} else {
			return null;
		}
	}
	
	private File saveFile() {
		int status = fileChooser.showSaveDialog(frame);
		
		if (status == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getCurrentDirectory();
			
			return file;
		} else {
			return null;
		}
	}

}
