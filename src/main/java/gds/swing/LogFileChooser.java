package gds.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class LogFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7738377264618480953L;


	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			LogFileChooser dialog = new LogFileChooser();
//			dialog.createDialog(null).setVisible(true);
////			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
////			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
	JCheckBox chckbxAppend = new JCheckBox("Append to File");

	@Override
	protected JDialog createDialog(Component parent) throws HeadlessException {
		
		JDialog dialog=super.createDialog(parent);
		dialog.getContentPane().add(chckbxAppend,BorderLayout.SOUTH);
		return dialog;
	}

	public boolean isAppend(){
		return this.chckbxAppend.isSelected();
	}
	
	
//	JFileChooser fileChooser;
//	boolean confirmed=false;
//	
//	public boolean isAppendToFile(){
//		return this.chckbxAppend.isSelected();
//	}
//	
//	public File getSelectedFile(){
//		return this.fileChooser.getSelectedFile();
//	}
//	
//	public boolean isConfirmed(){
//		return confirmed;
//	}
//
//	/**
//	 * Create the dialog.
//	 */
//	public LogFileChooser() {
//		setModal(true);
//		setModalityType(ModalityType.APPLICATION_MODAL);
//		setTitle("Select Log File");
//		setBounds(100, 100, 551, 341);
//		getContentPane().setLayout(new BorderLayout());
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		getContentPane().add(contentPanel, BorderLayout.CENTER);
//		contentPanel.setLayout(new BorderLayout(0, 0));
//		{
//			fileChooser = new JFileChooser();
//			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
////			fileChooser.setControlButtonsAreShown(false);
//
//			contentPanel.add(fileChooser);
//		}
//		{
//			JPanel buttonPane = new JPanel();
//			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//			getContentPane().add(buttonPane, BorderLayout.SOUTH);
//			{
//				chckbxAppend = new JCheckBox("Append to File");
//				buttonPane.add(chckbxAppend);
//			}
//			{
//				JButton okButton = new JButton("OK");
//				okButton.setActionCommand("OK");
//				buttonPane.add(okButton);
//				getRootPane().setDefaultButton(okButton);
//				okButton.addActionListener(new ActionListener() {
//					
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						confirmed=true;
//						fileChooser.approveSelection();
//						LogFileChooser.this.setVisible(false);
//					}
//				});
//			}
//			{
//				JButton cancelButton = new JButton("Cancel");
//				cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//				cancelButton.addActionListener(new ActionListener() {
//					
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						confirmed=false;
//						LogFileChooser.this.setVisible(false);
//						
//					}
//				});
//			}
//		}
//	}

}
