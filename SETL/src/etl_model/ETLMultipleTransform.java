package etl_model;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import core.LevelEntryNew;
import core.MultipleTransformation;
import helper.Methods;
import model.ETLOperation;
import net.miginfocom.swing.MigLayout;

public class ETLMultipleTransform implements ETLOperation {
	private String firstSourcePath, secondSourcePath, mapPath, targetType, targetPath;
	
	private JTextField textFieldFirstSource;
	private JTextField textFieldSecondSource;
	private JTextField textFieldMap;
	private JTextField textFieldTarget;
	
	private Methods methods;

	@Override
	public boolean execute(JTextPane textPane) {
		// TODO Auto-generated method stub
		final JDialog dialog = new JDialog();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String result = "";
					result += Calendar.getInstance().getTime().toString() + "\n";
					
					MultipleTransformation multipleTransformation = new MultipleTransformation();
					result += multipleTransformation.transformMultipleLiteral(firstSourcePath, secondSourcePath, mapPath, targetPath);
					
					result += "\n" + Calendar.getInstance().getTime();

					textPane.setText(textPane.getText().toString() + "\n" + result);
					dialog.dispose();
					dialog.setVisible(false);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow]", "[]"));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		panel.add(progressBar, "cell 0 0,grow");

		final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		dialog.setTitle("Progress");
		dialog.setModal(true);

		dialog.setContentPane(optionPane);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
		
		return true;
	}

	@Override
	public boolean getInput(JPanel panel, HashMap<String, LinkedHashSet<String>> inputParamsMap) {
		// TODO Auto-generated method stub
		methods = new Methods();
		
		JPanel panelMultupleTransform = new JPanel();
		panelMultupleTransform.setBackground(Color.WHITE);
		panelMultupleTransform.setLayout(new MigLayout("", "[][800px,grow][]", "[][][][][][grow]"));
		
		JLabel lblFirstSourceAbox = new JLabel("First Source ABox File:");
		lblFirstSourceAbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(lblFirstSourceAbox, "cell 0 0,alignx trailing");
		
		textFieldFirstSource = new JTextField();
		textFieldFirstSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(textFieldFirstSource, "cell 1 0,growx");
		textFieldFirstSource.setColumns(10);
		
		if (getFirstSourcePath() != null) {
			textFieldFirstSource.setText(getFirstSourcePath());
		}
		
		JButton btnOpenFirstSource = new JButton("Open");
		btnOpenFirstSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Source ABox File");
				if (filePath != null) {
					setFirstSourcePath(filePath);
					textFieldFirstSource.setText(filePath);
				}
			}
		});
		btnOpenFirstSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(btnOpenFirstSource, "cell 2 0");
		
		JLabel lblSecondSourceAbox = new JLabel("Second Source ABox File:");
		lblSecondSourceAbox.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(lblSecondSourceAbox, "cell 0 1,alignx trailing");
		
		textFieldSecondSource = new JTextField();
		textFieldSecondSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(textFieldSecondSource, "cell 1 1,growx");
		textFieldSecondSource.setColumns(10);
		
		if (getSecondSourcePath() != null) {
			textFieldSecondSource.setText(getSecondSourcePath());
		}
		
		JButton btnOpenSecondSource = new JButton("Open");
		btnOpenSecondSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Source ABox File");
				if (filePath != null) {
					setSecondSourcePath(filePath);
					textFieldSecondSource.setText(filePath);
				}
			}
		});
		btnOpenSecondSource.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(btnOpenSecondSource, "cell 2 1");
		
		JLabel lblMappingFile = new JLabel("Mapping File:");
		lblMappingFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(lblMappingFile, "cell 0 2,alignx trailing");
		
		textFieldMap = new JTextField();
		textFieldMap.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(textFieldMap, "cell 1 2,growx");
		textFieldMap.setColumns(10);
		
		if (getMapPath() != null) {
			textFieldMap.setText(getMapPath());
		}
		
		JButton btnOpenMap = new JButton("Open");
		btnOpenMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filePath = methods.chooseFile("Select Map File");
				if ((filePath != null)) {
					setMapPath(filePath);
					textFieldMap.setText(filePath);
				}
			}
		});
		btnOpenMap.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(btnOpenMap, "cell 2 2");
		
		JLabel lblTargetAboxType = new JLabel("Target ABox Type:");
		lblTargetAboxType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(lblTargetAboxType, "cell 0 3,alignx trailing");
		
		JComboBox comboBoxTargetType = new JComboBox(methods.getAllFileTypes().keySet().toArray());
		comboBoxTargetType.setBackground(Color.WHITE);
		comboBoxTargetType.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(comboBoxTargetType, "cell 1 3 2 1,growx");
		
		if (getTargetType() != null) {
			comboBoxTargetType.setSelectedItem(getTargetType());
		}
		
		JLabel lblTargetAboxFile = new JLabel("Target ABox File:");
		lblTargetAboxFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(lblTargetAboxFile, "cell 0 4,alignx trailing");
		
		textFieldTarget = new JTextField();
		textFieldTarget.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(textFieldTarget, "cell 1 4,growx");
		textFieldTarget.setColumns(10);
		
		if (getTargetPath() != null) {
			textFieldTarget.setText(getTargetPath());
		}
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = (String) comboBoxTargetType.getSelectedItem();
				String extension = methods.getAllFileTypes().get(key);
				String defaultName = methods.getDateTime() + "_TargetABox" + extension;

				String filePath = methods.chooseSaveFile("", defaultName,
						"Select Directory to save target ABox File");

				setTargetPath(filePath);
				setTargetType(key);
				
				textFieldTarget.setText(filePath);
				
			}
		});
		btnNew.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelMultupleTransform.add(btnNew, "cell 2 4,growx");
		
		int confirmation = JOptionPane.showConfirmDialog(null, panelMultupleTransform,
				"Please Input Values for Multiple Transform.", JOptionPane.OK_CANCEL_OPTION);
		
		if (confirmation == JOptionPane.OK_OPTION) {
			String firstSourcePath = textFieldFirstSource.getText().toString();
			String secondSourcePath = textFieldSecondSource.getText().toString();
			String mapFile = textFieldMap.getText().toString();
			String targetType = comboBoxTargetType.getSelectedItem().toString();
			String targetFile = textFieldTarget.getText().toString();
			
			if (methods.checkStrings(firstSourcePath, secondSourcePath, mapFile, targetFile)) {
				setFirstSourcePath(firstSourcePath);
				setSecondSourcePath(secondSourcePath);
				setMapPath(mapFile);
				setTargetType(targetType);
				setTargetPath(targetFile);
				
				ArrayList<String> inputList = new ArrayList<>();
				inputList.add(getFirstSourcePath());
				inputList.add(getSecondSourcePath());
				inputList.add(getMapPath());
				inputList.add(getTargetPath());
				inputList.add(getTargetType());
				
				return true;
			} else {
				methods.showDialog("Please provide value to all input.");
				return false;
			}
		}
		
		return false;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getFirstSourcePath() {
		return firstSourcePath;
	}

	public void setFirstSourcePath(String firstSourcePath) {
		this.firstSourcePath = firstSourcePath;
	}

	public String getSecondSourcePath() {
		return secondSourcePath;
	}

	public void setSecondSourcePath(String secondSourcePath) {
		this.secondSourcePath = secondSourcePath;
	}

	public String getMapPath() {
		return mapPath;
	}

	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
}
