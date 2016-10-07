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

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gds.console.ITerminal;
import gds.console.NetSetting;
import gds.serial.SerialPortManager;
import gds.serial.SerialPortSetting;
import gds.serial.SerialPortSetting.ExceptionSettingsParse;

public class TerminalConnectionDialog extends JDialog {


	/**
	 *
	 */
	private static final long serialVersionUID = -5251231900898693401L;
	private static final Logger logger = LoggerFactory.getLogger(TerminalConnectionDialog.class.getName());
	private final JPanel contentPanel = new JPanel();
	boolean confirmed = false;
	String connectionString;
	SerialPortSetting serialPortSettings = new SerialPortSetting("        ", 9600, SerialPortSetting.DATABITS_8,
			SerialPortSetting.STOPBITS_1, SerialPortSetting.PARITY_NONE, SerialPortSetting.FLOWCONTROL_NONE);
	NetSetting netSetting = new NetSetting();
	// public SSHSetting getSSHSetting() {
	// return SSHSetting;
	// }

	@SuppressWarnings("rawtypes")
	JComboBox baudrateComboBox;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	JRadioButton rdbtnSerial;
	JComboBox netHostComboBox;
	JTextField netPortTextField;

	public JComboBox getSSHLoginComboBox() {
		return netHostComboBox;
	}

//	public JRadioButton getRdbtnSerial() {
//		return rdbtnSerial;
//	}
//
//	public JRadioButton getRdbtnSshShell() {
//		return rdbtnSSH;
//	}

	JRadioButton rdbtnSSH;
	JRadioButton rdbtnTELNET;
	// public SerialPortSetting getSerialPortSettings() {
	// return serialPortSettings;
	// }

	public boolean isConfirmed() {
		return confirmed;
	}

	@SuppressWarnings("rawtypes")
	JComboBox deviceComboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			TerminalConnectionDialog dialog = new TerminalConnectionDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			logger.error("ERROR!!", e);
		}
	}

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TerminalConnectionDialog(JFrame frame) {
		super(frame);
		setTitle("Open New Terminal Connection");
		setBounds(100, 100, 480, 305);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 69, 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			rdbtnSSH = new JRadioButton("SSH");
			buttonGroup.add(rdbtnSSH);
			GridBagConstraints gbc_rdbtnSSH = new GridBagConstraints();
			gbc_rdbtnSSH.anchor = GridBagConstraints.WEST;
			gbc_rdbtnSSH.insets = new Insets(0, 0, 5, 5);
			gbc_rdbtnSSH.gridx = 0;
			gbc_rdbtnSSH.gridy = 0;
			contentPanel.add(rdbtnSSH, gbc_rdbtnSSH);
		}
		{
			JLabel lblLoginname = new JLabel("Host :");
			GridBagConstraints gbc_lblLoginname = new GridBagConstraints();
			gbc_lblLoginname.insets = new Insets(0, 0, 5, 5);
			gbc_lblLoginname.anchor = GridBagConstraints.EAST;
			gbc_lblLoginname.gridx = 2;
			gbc_lblLoginname.gridy = 0;
			contentPanel.add(lblLoginname, gbc_lblLoginname);
		}
		{
			netHostComboBox = new JComboBox();
			netHostComboBox.setModel(new DefaultComboBoxModel(new String[] { "localhost" }));
			netHostComboBox.setEditable(true);
			GridBagConstraints gbc_netHostComboBox = new GridBagConstraints();
			gbc_netHostComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_netHostComboBox.gridwidth = 2;
			gbc_netHostComboBox.insets = new Insets(0, 0, 5, 10);
			gbc_netHostComboBox.gridx = 3;
			gbc_netHostComboBox.gridy = 0;
			contentPanel.add(netHostComboBox, gbc_netHostComboBox);
		}
		{
			rdbtnTELNET = new JRadioButton("TELNET");
			buttonGroup.add(rdbtnTELNET);
			GridBagConstraints gbc_rdbtnTELNET = new GridBagConstraints();
			gbc_rdbtnTELNET.anchor = GridBagConstraints.WEST;
			gbc_rdbtnTELNET.insets = new Insets(0, 0, 5, 5);
			gbc_rdbtnTELNET.gridx = 0;
			gbc_rdbtnTELNET.gridy = 1;
			contentPanel.add(rdbtnTELNET, gbc_rdbtnTELNET);
		}
		{
			JLabel lblPort = new JLabel("Port :");
			GridBagConstraints gbc_lblPort = new GridBagConstraints();
			gbc_lblPort.anchor = GridBagConstraints.EAST;
			gbc_lblPort.insets = new Insets(0, 0, 5, 5);
			gbc_lblPort.gridx = 2;
			gbc_lblPort.gridy = 1;
			contentPanel.add(lblPort, gbc_lblPort);
		}
		{
			netPortTextField = new JTextField();
			netPortTextField.setColumns(10);
			netPortTextField.setText("22");
			GridBagConstraints gbc_netPortTextField = new GridBagConstraints();
			gbc_netPortTextField.fill = GridBagConstraints.HORIZONTAL;
			gbc_netPortTextField.insets = new Insets(0, 0, 5, 5);
			gbc_netPortTextField.gridx = 3;
			gbc_netPortTextField.gridy = 1;
			contentPanel.add(netPortTextField, gbc_netPortTextField);

		}
		{
			JSeparator separator = new JSeparator();
			GridBagConstraints gbc_separator = new GridBagConstraints();
			gbc_separator.gridwidth = 5;
			gbc_separator.fill = GridBagConstraints.HORIZONTAL;
			gbc_separator.insets = new Insets(0, 0, 5, 0);
			gbc_separator.gridx = 0;
			gbc_separator.gridy = 2;
			contentPanel.add(separator, gbc_separator);
		}
		{
			rdbtnSerial = new JRadioButton("Serial");
			// rdbtnSerial.addActionListener(this);
			rdbtnSerial.setSelected(true);
			buttonGroup.add(rdbtnSerial);
			GridBagConstraints gbc_rdbtnSerial = new GridBagConstraints();
			gbc_rdbtnSerial.anchor = GridBagConstraints.WEST;
			gbc_rdbtnSerial.insets = new Insets(0, 0, 5, 5);
			gbc_rdbtnSerial.gridx = 0;
			gbc_rdbtnSerial.gridy = 3;
			contentPanel.add(rdbtnSerial, gbc_rdbtnSerial);
		}
		{
			JLabel lblDevice = new JLabel("Device :");
			GridBagConstraints gbc_lblDevice = new GridBagConstraints();
			gbc_lblDevice.anchor = GridBagConstraints.EAST;
			gbc_lblDevice.insets = new Insets(0, 0, 5, 5);
			gbc_lblDevice.gridx = 2;
			gbc_lblDevice.gridy = 3;
			contentPanel.add(lblDevice, gbc_lblDevice);
		}
		{
			deviceComboBox = new JComboBox();
			deviceComboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {
							serialPortSettings.parseName(item.toString());
						} catch (ExceptionSettingsParse e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});
			GridBagConstraints gbc_deviceComboBox = new GridBagConstraints();
			gbc_deviceComboBox.gridwidth = 2;
			gbc_deviceComboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_deviceComboBox.anchor = GridBagConstraints.WEST;
			gbc_deviceComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_deviceComboBox.gridx = 3;
			gbc_deviceComboBox.gridy = 3;
			contentPanel.add(deviceComboBox, gbc_deviceComboBox);
		}
		{
			JLabel lblBaudRate = new JLabel("Baud Rate :");
			GridBagConstraints gbc_lblBaudRate = new GridBagConstraints();
			gbc_lblBaudRate.anchor = GridBagConstraints.EAST;
			gbc_lblBaudRate.insets = new Insets(0, 0, 5, 5);
			gbc_lblBaudRate.gridx = 2;
			gbc_lblBaudRate.gridy = 4;
			contentPanel.add(lblBaudRate, gbc_lblBaudRate);
		}
		{
			baudrateComboBox = new JComboBox();
			baudrateComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {

							serialPortSettings.parseBaudRate(item.toString());
						} catch (ExceptionSettingsParse e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});
			String[] bd = new String[] { "115200", "38400", "19200", "9600", };
			baudrateComboBox.setModel(new DefaultComboBoxModel(bd));
			baudrateComboBox.setSelectedIndex(0);
			try {
				serialPortSettings.parseBaudRate(bd[0]);
			} catch (ExceptionSettingsParse e1) {
				logger.error("ERROR!!", e1);
			}
			GridBagConstraints gbc_baudrateComboBox = new GridBagConstraints();
			gbc_baudrateComboBox.gridwidth = 2;
			gbc_baudrateComboBox.anchor = GridBagConstraints.WEST;
			gbc_baudrateComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_baudrateComboBox.gridx = 3;
			gbc_baudrateComboBox.gridy = 4;
			contentPanel.add(baudrateComboBox, gbc_baudrateComboBox);
		}
		{
			JLabel lblBit = new JLabel("Parity :");
			GridBagConstraints gbc_lblBit = new GridBagConstraints();
			gbc_lblBit.anchor = GridBagConstraints.EAST;
			gbc_lblBit.insets = new Insets(0, 0, 5, 5);
			gbc_lblBit.gridx = 2;
			gbc_lblBit.gridy = 5;
			contentPanel.add(lblBit, gbc_lblBit);
		}
		{
			JComboBox parityComboBox = new JComboBox();
			parityComboBox.setEnabled(false);
			parityComboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {
							serialPortSettings.parseParity(item.toString());
						} catch (ExceptionSettingsParse e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});
			parityComboBox.setModel(new DefaultComboBoxModel(new String[] { "NONE", "EVEN", "ODD" }));
			parityComboBox.setSelectedIndex(0);
			GridBagConstraints gbc_parityComboBox = new GridBagConstraints();
			gbc_parityComboBox.gridwidth = 2;
			gbc_parityComboBox.anchor = GridBagConstraints.WEST;
			gbc_parityComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_parityComboBox.gridx = 3;
			gbc_parityComboBox.gridy = 5;
			contentPanel.add(parityComboBox, gbc_parityComboBox);
		}
		{
			JLabel lblDataL = new JLabel("Data Bits :");
			GridBagConstraints gbc_lblDataL = new GridBagConstraints();
			gbc_lblDataL.anchor = GridBagConstraints.EAST;
			gbc_lblDataL.insets = new Insets(0, 0, 5, 5);
			gbc_lblDataL.gridx = 2;
			gbc_lblDataL.gridy = 6;
			contentPanel.add(lblDataL, gbc_lblDataL);
		}
		{
			JComboBox databitComboBox = new JComboBox();
			databitComboBox.setEnabled(false);
			databitComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {
							serialPortSettings.parseDataBits(item.toString());
						} catch (Exception e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});
			databitComboBox.setModel(new DefaultComboBoxModel(new String[] { "8", "7" }));
			GridBagConstraints gbc_databitComboBox = new GridBagConstraints();
			gbc_databitComboBox.gridwidth = 2;
			gbc_databitComboBox.anchor = GridBagConstraints.WEST;
			gbc_databitComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_databitComboBox.gridx = 3;
			gbc_databitComboBox.gridy = 6;
			contentPanel.add(databitComboBox, gbc_databitComboBox);
		}
		{
			JLabel lblFlowControl = new JLabel("Stop Bits :");
			GridBagConstraints gbc_lblFlowControl = new GridBagConstraints();
			gbc_lblFlowControl.anchor = GridBagConstraints.EAST;
			gbc_lblFlowControl.insets = new Insets(0, 0, 5, 5);
			gbc_lblFlowControl.gridx = 2;
			gbc_lblFlowControl.gridy = 7;
			contentPanel.add(lblFlowControl, gbc_lblFlowControl);
		}
		{
			JComboBox stopbitComboBox = new JComboBox();
			stopbitComboBox.setEnabled(false);
			stopbitComboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {
							serialPortSettings.parseStopBits(item.toString());
						} catch (ExceptionSettingsParse e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});
			stopbitComboBox.setModel(new DefaultComboBoxModel(new String[] { "1", "2" }));
			stopbitComboBox.setSelectedIndex(0);
			GridBagConstraints gbc_stopbitComboBox = new GridBagConstraints();
			gbc_stopbitComboBox.gridwidth = 2;
			gbc_stopbitComboBox.anchor = GridBagConstraints.WEST;
			gbc_stopbitComboBox.insets = new Insets(0, 0, 5, 0);
			gbc_stopbitComboBox.gridx = 3;
			gbc_stopbitComboBox.gridy = 7;
			contentPanel.add(stopbitComboBox, gbc_stopbitComboBox);
		}
		{
			JLabel lblFlowControl_1 = new JLabel("Flow Control :");
			GridBagConstraints gbc_lblFlowControl_1 = new GridBagConstraints();
			gbc_lblFlowControl_1.anchor = GridBagConstraints.EAST;
			gbc_lblFlowControl_1.insets = new Insets(0, 0, 0, 5);
			gbc_lblFlowControl_1.gridx = 2;
			gbc_lblFlowControl_1.gridy = 8;
			contentPanel.add(lblFlowControl_1, gbc_lblFlowControl_1);
		}
		{
			JComboBox<?> flowControlComboBox = new JComboBox<Object>();
			flowControlComboBox.setEnabled(false);
			flowControlComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						Object item = e.getItem();
						try {
							serialPortSettings.parseFlowControl(item.toString());
						} catch (ExceptionSettingsParse e1) {
							logger.error("ERROR!!", e1);
						}
					}
				}
			});

			flowControlComboBox.setModel(new DefaultComboBoxModel(new String[] { "NONE" }));
			flowControlComboBox.setSelectedIndex(0);

			GridBagConstraints gbc_flowControlComboBox = new GridBagConstraints();
			gbc_flowControlComboBox.gridwidth = 2;
			gbc_flowControlComboBox.anchor = GridBagConstraints.WEST;
			gbc_flowControlComboBox.gridx = 3;
			gbc_flowControlComboBox.gridy = 8;
			contentPanel.add(flowControlComboBox, gbc_flowControlComboBox);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TerminalConnectionDialog.this.confirmed = true;
						if (rdbtnSSH.isSelected() || rdbtnTELNET.isSelected()) {
							String host = netHostComboBox.getSelectedItem().toString();
							int port = 22;
							try {
								port = Integer.parseInt(netPortTextField.getText());
							} catch (Exception ex) {
							}

							// port=
							netSetting.setHost(host);
							netSetting.setPort(port);

							if (rdbtnSSH.isSelected()){
								connectionString = ITerminal.PREFIX_SSH	+ netSetting.toString();
							}else{
								connectionString = ITerminal.PREFIX_TELNET + netSetting.toString();
							}

						} else {
							connectionString = serialPortSettings.toString();
						}

						TerminalConnectionDialog.this.setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						TerminalConnectionDialog.this.confirmed = false;
						TerminalConnectionDialog.this.setVisible(false);
					}

				});
				buttonPane.add(cancelButton);
			}
		}

		UIPostInit();
	}

	private void UIPostInit() {

		this.addWindowListener(new WindowAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void windowOpened(WindowEvent e) {
				deviceComboBox.removeAllItems();
				deviceComboBox.addItem("        ");

				for (String str : SerialPortManager.listAvailable(true)) {
					deviceComboBox.addItem(str);
				}

			}




		});

	}

	public String getConnectionString() {
		return this.connectionString;
	}

}
