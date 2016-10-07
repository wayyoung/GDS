package gds.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.console.AbstractControlConsole;

public class FunctionScriptDialog extends JDialog implements ActionListener, ItemListener {

	/**
	 *

	 */
	private static final Logger logger = LoggerFactory.getLogger(FunctionScriptDialog.class.getName());
	private static final long serialVersionUID = 6612002107423239264L;
	private final JPanel contentPanel = new JPanel();
	private JTextField fs_0;
	private JTextField fs_1;
	private JTextField fs_2;
	private JTextField fs_3;
	private JTextField fs_4;
	private JTextField fs_5;
	private JTextField fs_6;
	private JTextField fs_7;
	private TCheckBox checkbox_0;
	private TCheckBox checkbox_1;
	private TCheckBox checkbox_2;
	private TCheckBox checkbox_3;
	private TCheckBox checkbox_4;
	private TCheckBox checkbox_5;
	private TCheckBox checkbox_6;
	private TCheckBox checkbox_7;
	JButton btnBrowse_0;
	JButton btnBrowse_1;
	JButton btnBrowse_2;
	JButton btnBrowse_3;
	JButton btnBrowse_4;
	JButton btnBrowse_5;
	JButton btnBrowse_6;
	JButton btnBrowse_7;

	final JFileChooser fc = new JFileChooser();
	AbstractControlConsole console;


	@Override
	public void itemStateChanged(ItemEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnBrowse_0 || e.getSource() == btnBrowse_1
				|| e.getSource() == btnBrowse_2 || e.getSource() == btnBrowse_3
				|| e.getSource() == btnBrowse_4 || e.getSource() == btnBrowse_5
				|| e.getSource() == btnBrowse_6 || e.getSource() == btnBrowse_7) {

			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					String filePath = fc.getSelectedFile().getCanonicalPath();

					if (e.getActionCommand().equals("F1")) {
						fs_0.setText(filePath);
						console.fsScripts.setProperty("F1", filePath);
					} else if (e.getActionCommand().equals("F2")) {
						fs_1.setText(filePath);
						console.fsScripts.setProperty("F2", filePath);
					} else if (e.getActionCommand().equals("F3")) {
						fs_2.setText(filePath);
						console.fsScripts.setProperty("F3", filePath);
					} else if (e.getActionCommand().equals("F4")) {
						fs_3.setText(filePath);
						console.fsScripts.setProperty("F4", filePath);
						// fs.set(0,filePath);
					} else if (e.getActionCommand().equals("F5")) {
						fs_4.setText(filePath);
						console.fsScripts.setProperty("F5", filePath);
						// fsFiles[4]=f;
					} else if (e.getActionCommand().equals("F6")) {
						fs_5.setText(filePath);
						console.fsScripts.setProperty("F6", filePath);
						// fsFiles[5]=f;
					} else if (e.getActionCommand().equals("F7")) {
						fs_6.setText(filePath);
						console.fsScripts.setProperty("F7", filePath);
						// fsFiles[6]=f;
					} else if (e.getActionCommand().equals("F8")) {
						fs_7.setText(filePath);
						console.fsScripts.setProperty("F8", filePath);
						// fsFiles[7]=f;
					}
				} catch (IOException e1) {
					logger.error( "ERROR!!", e1);
				}
				this.console.saveFunctionScriptProperties();
				// This is where a real application would open the file.
				// log.append("Opening: " + file.getName() + "." + newline);
			}
		}else  if (e.getSource() == checkbox_0 || e.getSource() == checkbox_1
				|| e.getSource() == checkbox_2 || e.getSource() == checkbox_3
				|| e.getSource() == checkbox_4 || e.getSource() == checkbox_5
				|| e.getSource() == checkbox_6 || e.getSource() == checkbox_7) {
			int idx=Integer.parseInt(((JCheckBox)e.getSource()).getActionCommand());
			if(!((TCheckBox)e.getSource()).isSelected())
			{
				console.fsFlags[idx]=2;
				((TCheckBox)e.getSource()).setEnabled(false);

				if(console.scriptWorkers[idx]!=null){
					console.scriptWorkers[idx].cancel(true);

					try {
						console.ready();
					} catch (RemoteException e1) {
						logger.error("FScript Cancel!!",e1);
					}

				}

			}

		}

	}

	public void syncExecutingStatus(){
		if(console.fsFlags[0]==1)checkbox_0.setSelected(true);
		else if(console.fsFlags[0]==0){checkbox_0.setSelected(false);checkbox_0.setSelectionState(0);}
		if(console.fsFlags[1]==1)checkbox_1.setSelected(true);
		else if(console.fsFlags[1]==0){checkbox_1.setSelected(false);checkbox_1.setSelectionState(0);}
		if(console.fsFlags[2]==1)checkbox_2.setSelected(true);
		else if(console.fsFlags[2]==0){checkbox_2.setSelected(false);checkbox_2.setSelectionState(0);}
		if(console.fsFlags[3]==1)checkbox_3.setSelected(true);
		else if(console.fsFlags[3]==0){checkbox_3.setSelected(false);checkbox_3.setSelectionState(0);}
		if(console.fsFlags[4]==1)checkbox_4.setSelected(true);
		else if(console.fsFlags[4]==0){checkbox_4.setSelected(false);checkbox_4.setSelectionState(0);}
		if(console.fsFlags[5]==1)checkbox_5.setSelected(true);
		else if(console.fsFlags[5]==0){checkbox_5.setSelected(false);checkbox_5.setSelectionState(0);}
		if(console.fsFlags[6]==1)checkbox_6.setSelected(true);
		else if(console.fsFlags[6]==0){checkbox_6.setSelected(false);checkbox_6.setSelectionState(0);}
		if(console.fsFlags[7]==1)checkbox_7.setSelected(true);
		else if(console.fsFlags[7]==0){checkbox_7.setSelected(false);checkbox_7.setSelectionState(0);}

		if(console.fsFlags[0]==1){
			checkbox_0.setEnabled(true);
		}else{
			checkbox_0.setEnabled(false);
		}
		if(console.fsFlags[1]==1){
			checkbox_1.setEnabled(true);
		}else{
			checkbox_1.setEnabled(false);
		}
		if(console.fsFlags[2]==1){
			checkbox_2.setEnabled(true);
		}else{
			checkbox_2.setEnabled(false);
		}
		if(console.fsFlags[3]==1){
			checkbox_3.setEnabled(true);
		}else{
			checkbox_3.setEnabled(false);
		}
		if(console.fsFlags[4]==1){
			checkbox_4.setEnabled(true);
		}else{
			checkbox_4.setEnabled(false);
		}
		if(console.fsFlags[5]==1){
			checkbox_5.setEnabled(true);
		}else{
			checkbox_5.setEnabled(false);
		}
		if(console.fsFlags[6]==1){
			checkbox_6.setEnabled(true);
		}else{
			checkbox_6.setEnabled(false);
		}
		if(console.fsFlags[7]==1){
			checkbox_7.setEnabled(true);
		}else{
			checkbox_7.setEnabled(false);
		}

	}



	Thread syncThread;

	/**
	 * Create the dialog.
	 */
	public FunctionScriptDialog(AbstractControlConsole console) {
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

		syncThread=new Thread("Function Script Sync Thread"){
			@Override
			public void run() {
					while(true){
						try{
							synchronized(this){
								if(isVisible()){
									wait(100);
								}else{

									wait();
								}
								SwingUtilities.invokeLater(new Runnable(){
									@Override
									public void run(){
										syncExecutingStatus();
									}
								});
							}
						}catch(Exception ee){
							logger.error( "ERROR!!", ee);
						}
					}
			}
		};
		syncThread.start();


		addWindowListener(new WindowAdapter() {

			@Override
			public void windowActivated(WindowEvent e) {
				synchronized (syncThread) {
					syncExecutingStatus();
					syncThread.notify();
				}

			}

		});
		setTitle("Function Scripts");
		setBounds(100, 100, 555, 346);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblFunctionScripts = new JLabel("Function Scripts:");
			GridBagConstraints gbc_lblFunctionScripts = new GridBagConstraints();
			gbc_lblFunctionScripts.gridwidth = 2;
			gbc_lblFunctionScripts.anchor = GridBagConstraints.WEST;
			gbc_lblFunctionScripts.insets = new Insets(0, 0, 5, 5);
			gbc_lblFunctionScripts.gridx = 1;
			gbc_lblFunctionScripts.gridy = 0;
			contentPanel.add(lblFunctionScripts, gbc_lblFunctionScripts);
		}
		{
			checkbox_0 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 1;
			contentPanel.add(checkbox_0, gbc_checkbox);
		}
		{
			JLabel lblF = new JLabel("  F1  ");
			GridBagConstraints gbc_lblF = new GridBagConstraints();
			gbc_lblF.anchor = GridBagConstraints.EAST;
			gbc_lblF.insets = new Insets(0, 0, 5, 5);
			gbc_lblF.gridx = 1;
			gbc_lblF.gridy = 1;
			contentPanel.add(lblF, gbc_lblF);
		}
		{
			fs_0 = new JTextField();
			GridBagConstraints gbc_fs_0 = new GridBagConstraints();
			gbc_fs_0.insets = new Insets(0, 0, 5, 5);
			gbc_fs_0.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_0.gridx = 2;
			gbc_fs_0.gridy = 1;
			contentPanel.add(fs_0, gbc_fs_0);
			fs_0.setColumns(10);
		}
		{
			btnBrowse_0 = new JButton("Browse");
			GridBagConstraints gbc_btnBrowse = new GridBagConstraints();
			gbc_btnBrowse.anchor = GridBagConstraints.EAST;
			gbc_btnBrowse.insets = new Insets(0, 0, 5, 0);
			gbc_btnBrowse.gridx = 3;
			gbc_btnBrowse.gridy = 1;
			btnBrowse_0.setActionCommand("F1");
			btnBrowse_0.addActionListener(this);
			contentPanel.add(btnBrowse_0, gbc_btnBrowse);
		}
		{
			checkbox_1 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 2;
			contentPanel.add(checkbox_1, gbc_checkbox);
		}
		{
			JLabel lblF_1 = new JLabel("  F2  ");
			GridBagConstraints gbc_lblF_1 = new GridBagConstraints();
			gbc_lblF_1.anchor = GridBagConstraints.EAST;
			gbc_lblF_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_1.gridx = 1;
			gbc_lblF_1.gridy = 2;
			contentPanel.add(lblF_1, gbc_lblF_1);
		}
		{
			fs_1 = new JTextField();
			fs_1.setColumns(10);
			GridBagConstraints gbc_fs_1 = new GridBagConstraints();
			gbc_fs_1.insets = new Insets(0, 0, 5, 5);
			gbc_fs_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_1.gridx = 2;
			gbc_fs_1.gridy = 2;
			contentPanel.add(fs_1, gbc_fs_1);
		}
		{
			btnBrowse_1 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 2;
			btnBrowse_1.setActionCommand("F2");
			btnBrowse_1.addActionListener(this);
			contentPanel.add(btnBrowse_1, gbc_button);
		}
		{
			checkbox_2 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 3;
			contentPanel.add(checkbox_2, gbc_checkbox);
		}
		{
			JLabel lblF_2 = new JLabel("  F3  ");
			GridBagConstraints gbc_lblF_2 = new GridBagConstraints();
			gbc_lblF_2.anchor = GridBagConstraints.EAST;
			gbc_lblF_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_2.gridx = 1;
			gbc_lblF_2.gridy = 3;
			contentPanel.add(lblF_2, gbc_lblF_2);
		}
		{
			fs_2 = new JTextField();
			fs_2.setColumns(10);
			GridBagConstraints gbc_fs_2 = new GridBagConstraints();
			gbc_fs_2.insets = new Insets(0, 0, 5, 5);
			gbc_fs_2.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_2.gridx = 2;
			gbc_fs_2.gridy = 3;
			contentPanel.add(fs_2, gbc_fs_2);
		}
		{
			btnBrowse_2 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 3;
			btnBrowse_2.setActionCommand("F3");
			btnBrowse_2.addActionListener(this);
			contentPanel.add(btnBrowse_2, gbc_button);
		}
		{
			checkbox_3 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 4;
			contentPanel.add(checkbox_3, gbc_checkbox);
		}
		{
			JLabel lblF_3 = new JLabel("  F4  ");
			GridBagConstraints gbc_lblF_3 = new GridBagConstraints();
			gbc_lblF_3.anchor = GridBagConstraints.EAST;
			gbc_lblF_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_3.gridx = 1;
			gbc_lblF_3.gridy = 4;
			contentPanel.add(lblF_3, gbc_lblF_3);
		}
		{
			fs_3 = new JTextField();
			fs_3.setColumns(10);
			GridBagConstraints gbc_fs_3 = new GridBagConstraints();
			gbc_fs_3.insets = new Insets(0, 0, 5, 5);
			gbc_fs_3.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_3.gridx = 2;
			gbc_fs_3.gridy = 4;
			contentPanel.add(fs_3, gbc_fs_3);
		}
		{
			btnBrowse_3 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 4;
			btnBrowse_3.setActionCommand("F4");
			btnBrowse_3.addActionListener(this);
			contentPanel.add(btnBrowse_3, gbc_button);
		}
		{
			checkbox_4 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 5;
			contentPanel.add(checkbox_4, gbc_checkbox);
		}
		{
			JLabel lblF_4 = new JLabel("  F5  ");
			GridBagConstraints gbc_lblF_4 = new GridBagConstraints();
			gbc_lblF_4.anchor = GridBagConstraints.EAST;
			gbc_lblF_4.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_4.gridx = 1;
			gbc_lblF_4.gridy = 5;
			contentPanel.add(lblF_4, gbc_lblF_4);
		}
		{
			fs_4 = new JTextField();
			fs_4.setColumns(10);
			GridBagConstraints gbc_fs_4 = new GridBagConstraints();
			gbc_fs_4.insets = new Insets(0, 0, 5, 5);
			gbc_fs_4.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_4.gridx = 2;
			gbc_fs_4.gridy = 5;
			contentPanel.add(fs_4, gbc_fs_4);
		}
		{
			btnBrowse_4 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 5;
			btnBrowse_4.setActionCommand("F5");
			btnBrowse_4.addActionListener(this);
			contentPanel.add(btnBrowse_4, gbc_button);
		}
		{
			checkbox_5 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 6;
			contentPanel.add(checkbox_5, gbc_checkbox);
		}
		{
			JLabel lblF_5 = new JLabel("  F6  ");
			GridBagConstraints gbc_lblF_5 = new GridBagConstraints();
			gbc_lblF_5.anchor = GridBagConstraints.EAST;
			gbc_lblF_5.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_5.gridx = 1;
			gbc_lblF_5.gridy = 6;
			contentPanel.add(lblF_5, gbc_lblF_5);
		}
		{
			fs_5 = new JTextField();
			fs_5.setColumns(10);
			GridBagConstraints gbc_fs_5 = new GridBagConstraints();
			gbc_fs_5.insets = new Insets(0, 0, 5, 5);
			gbc_fs_5.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_5.gridx = 2;
			gbc_fs_5.gridy = 6;
			contentPanel.add(fs_5, gbc_fs_5);
		}
		{
			btnBrowse_5 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 6;
			btnBrowse_5.setActionCommand("F6");
			btnBrowse_5.addActionListener(this);
			contentPanel.add(btnBrowse_5, gbc_button);
		}
		{
			checkbox_6 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 5, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 7;
			contentPanel.add(checkbox_6, gbc_checkbox);
		}
		{
			JLabel lblF_6 = new JLabel("  F7  ");
			GridBagConstraints gbc_lblF_6 = new GridBagConstraints();
			gbc_lblF_6.anchor = GridBagConstraints.EAST;
			gbc_lblF_6.insets = new Insets(0, 0, 5, 5);
			gbc_lblF_6.gridx = 1;
			gbc_lblF_6.gridy = 7;
			contentPanel.add(lblF_6, gbc_lblF_6);
		}
		{
			fs_6 = new JTextField();
			fs_6.setColumns(10);
			GridBagConstraints gbc_fs_6 = new GridBagConstraints();
			gbc_fs_6.insets = new Insets(0, 0, 5, 5);
			gbc_fs_6.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_6.gridx = 2;
			gbc_fs_6.gridy = 7;
			contentPanel.add(fs_6, gbc_fs_6);
		}
		{
			btnBrowse_6 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.gridx = 3;
			gbc_button.gridy = 7;
			btnBrowse_6.setActionCommand("F7");
			btnBrowse_6.addActionListener(this);
			contentPanel.add(btnBrowse_6, gbc_button);
		}
		{
			checkbox_7 = new TCheckBox("");
			GridBagConstraints gbc_checkbox = new GridBagConstraints();
			gbc_checkbox.insets = new Insets(0, 0, 0, 5);
			gbc_checkbox.gridx = 0;
			gbc_checkbox.gridy = 8;
			contentPanel.add(checkbox_7, gbc_checkbox);
		}
		{
			JLabel lblF_7 = new JLabel("  F8  ");
			GridBagConstraints gbc_lblF_7 = new GridBagConstraints();
			gbc_lblF_7.anchor = GridBagConstraints.EAST;
			gbc_lblF_7.insets = new Insets(0, 0, 0, 5);
			gbc_lblF_7.gridx = 1;
			gbc_lblF_7.gridy = 8;
			contentPanel.add(lblF_7, gbc_lblF_7);
		}
		{
			fs_7 = new JTextField();
			fs_7.setColumns(10);
			GridBagConstraints gbc_fs_7 = new GridBagConstraints();
			gbc_fs_7.insets = new Insets(0, 0, 0, 5);
			gbc_fs_7.fill = GridBagConstraints.HORIZONTAL;
			gbc_fs_7.gridx = 2;
			gbc_fs_7.gridy = 8;
			contentPanel.add(fs_7, gbc_fs_7);
		}
		{
			btnBrowse_7 = new JButton("Browse");
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.anchor = GridBagConstraints.EAST;
			gbc_button.gridx = 3;
			gbc_button.gridy = 8;
			btnBrowse_7.setActionCommand("F8");
			btnBrowse_7.addActionListener(this);
			contentPanel.add(btnBrowse_7, gbc_button);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						FunctionScriptDialog.this.setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
//			{
//				JButton cancelButton = new JButton("Cancel");
//				cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//			}
		}
		this.console=console;
		syncExecutingStatus();

		checkbox_0.setActionCommand("0");checkbox_0.addActionListener(this);checkbox_0.addItemListener(this);
		checkbox_1.setActionCommand("1");checkbox_1.addActionListener(this);checkbox_1.addItemListener(this);
		checkbox_2.setActionCommand("2");checkbox_2.addActionListener(this);checkbox_2.addItemListener(this);
		checkbox_3.setActionCommand("3");checkbox_3.addActionListener(this);checkbox_3.addItemListener(this);
		checkbox_4.setActionCommand("4");checkbox_4.addActionListener(this);checkbox_4.addItemListener(this);
		checkbox_5.setActionCommand("5");checkbox_5.addActionListener(this);checkbox_5.addItemListener(this);
		checkbox_6.setActionCommand("6");checkbox_6.addActionListener(this);checkbox_6.addItemListener(this);
		checkbox_7.setActionCommand("7");checkbox_7.addActionListener(this);checkbox_7.addItemListener(this);

		if(console.fsScripts.getProperty("F1")!=null)fs_0.setText(console.fsScripts.getProperty("F1"));
		if(console.fsScripts.getProperty("F2")!=null)fs_1.setText(console.fsScripts.getProperty("F2"));
		if(console.fsScripts.getProperty("F3")!=null)fs_2.setText(console.fsScripts.getProperty("F3"));
		if(console.fsScripts.getProperty("F4")!=null)fs_3.setText(console.fsScripts.getProperty("F4"));
		if(console.fsScripts.getProperty("F5")!=null)fs_4.setText(console.fsScripts.getProperty("F5"));
		if(console.fsScripts.getProperty("F6")!=null)fs_5.setText(console.fsScripts.getProperty("F6"));
		if(console.fsScripts.getProperty("F7")!=null)fs_6.setText(console.fsScripts.getProperty("F7"));
		if(console.fsScripts.getProperty("F8")!=null)fs_7.setText(console.fsScripts.getProperty("F8"));

	}

}
