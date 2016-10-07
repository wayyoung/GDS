package gds.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ButtonDialog extends JDialog implements ActionListener{
	
	@Override
	public void actionPerformed(ActionEvent e) {

		
		String cmd=e.getActionCommand();
		if(cmd!=null)
		{
			int i=-1;
			if(cmd.equals("B0"))i=1;
			if(cmd.equals("B1"))i=2;
			if(cmd.equals("B2"))i=3;
			if(cmd.equals("B3"))i=4;
			
			if(i>0)
			{
				checkBoxes[i-1].setSelected(menuCheckBoxes[i-1].isSelected());
			}
		}
		for(int i=0;i<checkBoxes.length;i++){
			
			if(e.getSource()==checkBoxes[i])
			{
				menuCheckBoxes[i].doClick();
				return;
			}
			else if(e.getSource()==clickButtons[i])
			{
//				JButton btn=clickButtons[i];
				if(!menuCheckBoxes[i].isSelected()){
					menuCheckBoxes[i].doClick();
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
				}
				menuCheckBoxes[i].doClick();
				break;
			}
		}
		
		
	}

	public JCheckBox[] getCheckBoxes() {
		return checkBoxes;
	}

	public void setParentCheckBoxList(JCheckBoxMenuItem[] chks) {
		menuCheckBoxes[0]=chks[0];
		menuCheckBoxes[1]=chks[1];
		menuCheckBoxes[2]=chks[2];
		menuCheckBoxes[3]=chks[3];
	}

	JCheckBox[] checkBoxes=new JCheckBox[4];
	JCheckBoxMenuItem[] menuCheckBoxes=new JCheckBoxMenuItem[4];
	JButton[] clickButtons=new JButton[4];
	
	public ButtonDialog() {
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		getContentPane().add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{90, 57, 0};
		gbl_panel.rowHeights = new int[]{19, 25, 25, 25, 25, 19, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Button 0");
		chckbxNewCheckBox.addActionListener(this);
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 1;
		panel.add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("click");
		btnNewButton.addActionListener(this);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 1;
		panel.add(btnNewButton, gbc_btnNewButton);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Button 1");
		chckbxNewCheckBox_1.addActionListener(this);
		GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_1.gridx = 0;
		gbc_chckbxNewCheckBox_1.gridy = 2;
		panel.add(chckbxNewCheckBox_1, gbc_chckbxNewCheckBox_1);
		
		JButton btnNewButton_1 = new JButton("click");
		btnNewButton_1.addActionListener(this);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 2;
		panel.add(btnNewButton_1, gbc_btnNewButton_1);
		
		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Button 2");
		chckbxNewCheckBox_2.addActionListener(this);
		GridBagConstraints gbc_chckbxNewCheckBox_2 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_2.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_2.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_2.gridx = 0;
		gbc_chckbxNewCheckBox_2.gridy = 3;
		panel.add(chckbxNewCheckBox_2, gbc_chckbxNewCheckBox_2);
		
		JButton btnNewButton_2 = new JButton("click");
		btnNewButton_2.addActionListener(this);
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_2.gridx = 1;
		gbc_btnNewButton_2.gridy = 3;
		panel.add(btnNewButton_2, gbc_btnNewButton_2);
		
		JCheckBox chckbxNewCheckBox_3 = new JCheckBox("Button 3");
		chckbxNewCheckBox_3.addActionListener(this);
		GridBagConstraints gbc_chckbxNewCheckBox_3 = new GridBagConstraints();
		gbc_chckbxNewCheckBox_3.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox_3.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxNewCheckBox_3.gridx = 0;
		gbc_chckbxNewCheckBox_3.gridy = 4;
		panel.add(chckbxNewCheckBox_3, gbc_chckbxNewCheckBox_3);
		
		JButton btnNewButton_3 = new JButton("click");
		btnNewButton_3.addActionListener(this);
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_3.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnNewButton_3.gridx = 1;
		gbc_btnNewButton_3.gridy = 4;
		panel.add(btnNewButton_3, gbc_btnNewButton_3);
		
		checkBoxes[0]=chckbxNewCheckBox;
		checkBoxes[1]=chckbxNewCheckBox_1;
		checkBoxes[2]=chckbxNewCheckBox_2;
		checkBoxes[3]=chckbxNewCheckBox_3;
		
		clickButtons[0]=btnNewButton;
		clickButtons[1]=btnNewButton_1;
		clickButtons[2]=btnNewButton_2;
		clickButtons[3]=btnNewButton_3;
		
		JButton btnNewButton_4 = new JButton("Close");
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.gridx = 1;
		gbc_btnNewButton_4.gridy = 6;
		panel.add(btnNewButton_4, gbc_btnNewButton_4);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ButtonDialog.this.setVisible(false);
			}
		});
	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				for(int i=0;i<menuCheckBoxes.length;i++){
					checkBoxes[i].setSelected(menuCheckBoxes[i].isSelected());
				}
			}
		});
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
