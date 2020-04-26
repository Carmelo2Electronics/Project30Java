
package project30java;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import javax.activation.*;

//kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
public class Project30Java  {
    public static void main(String[] args) throws IOException, URISyntaxException {

    	new MakeFolder();
    	
		Frame frame=new Frame();
		frame.setVisible(true);
    }
}
//kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk

//ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp
class Frame extends JFrame{
	
	private static final long serialVersionUID = 1L;

	public Frame() throws IOException, URISyntaxException {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		setBounds(20, 20, 700, 400);
		setTitle("Carmelo2Elecronics");
		setResizable(false);				
		addWindowListener(new WindowAdapter() {							
	        public void windowClosing(WindowEvent event) {
	    		int opcion=JOptionPane.showConfirmDialog(null, "You want to close the entire program", "CONFIRMATION", JOptionPane.OK_CANCEL_OPTION);
	    		if(opcion==0) {
	    			System.exit(0);	    			
	    		}
	        }
	    });			
		add(new Panel());		
	}
}
//ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class Panel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private int port=9999;
	private JMenuBar menuBar = new JMenuBar();;
	private JMenu menu = new JMenu(" Setting ");; 
	private JMenu submenu = new JMenu("Thresholds value for sending email alerts");
	private JMenuItem menuItem;
	private String StringItem;	
	private JTextArea jTextArea=new JTextArea(15,100);
	private JScrollPane jScrollPane =new JScrollPane(jTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JButton ONOFF=new JButton("OFF");
	private JButton TEST=new JButton("TEST");
	private JLabel ValueLabel=new JLabel("Value: ");
	private String Sender;
	private String Password;
	private List <String> ToList = new ArrayList <String> ();	
	private int MaxValue=-9999999;
	private int MinValue=-9999999;
	private PopupWindows samples;			
	private PopupWindows file;
	private PopupWindows alert;
	private boolean flagONOFF=true;
	private boolean flagReviewSettings=false;	
	private boolean r[]=new boolean[8];
	private Host host;
	private Executor executor=null;
	private MakeFile makeFile;

	public Panel() throws IOException, URISyntaxException {
		
		host=new Host(port);
		makeFile=new MakeFile();
		
		jTextArea.setEditable(false);
			
		String string100 = null;
		int inter100 = 0;		
		int[] bounds100={40, 40, 600, 80};
		samples = new PopupWindows(bounds100, "SAMPLES", string100 ,inter100);		
		int[] bounds200={60, 60, 600, 80};	
		file = new PopupWindows(bounds200, "FILE", string100 ,inter100);		
		int[] bounds300={80, 80, 600, 80};
		alert= new PopupWindows(bounds300, "ALERTS", string100 ,inter100);
		
		setLayout(null);
		
		menuBar.add(menu);
		menuItem = new JMenuItem("Sending mail"); menuItem.addActionListener(this); menu.add(menuItem);									//1	
		menuItem = new JMenuItem("Password sending email"); menuItem.addActionListener(this); menu.add(menuItem);						//2
		menu.addSeparator();
		menuItem = new JMenuItem("Add email addresses to receive emails"); menuItem.addActionListener(this); menu.add(menuItem);		//3
		menuItem = new JMenuItem("Delete email addresses to receive emails"); menuItem.addActionListener(this); menu.add(menuItem);		//4
		menu.addSeparator();
			menuItem = new JMenuItem("Min. Value"); menuItem.addActionListener(this); menu.add(menuItem);								//5
			submenu.add(menuItem);
			menuItem = new JMenuItem("Max. Value"); menuItem.addActionListener(this); menu.add(menuItem);								//6
			submenu.add(menuItem);
		menu.add(submenu);	
		menu.addSeparator();	
		menuItem = new JMenuItem("Every time a sample was taken"); menuItem.addActionListener(this); menu.add(menuItem);				//7
		menuItem = new JMenuItem("Every time a sample file is sent"); menuItem.addActionListener(this); menu.add(menuItem);				//8	
		menuItem = new JMenuItem("Every time a alert is sent"); menuItem.addActionListener(this); menu.add(menuItem);					//9
		menuBar.setBounds(40, 10, 70, 20);
		add(menuBar);
							
		jScrollPane.setBounds(40, 40, 600, 250);
		add(jScrollPane);
		
		ONOFF.setBounds(40, 310, 100, 20);
		add(ONOFF);
		ONOFF.setBackground(Color.RED);
		ONOFF.setEnabled(false);
		ONOFF.addActionListener(new EventsButton());
		
		TEST.setBounds(200, 310, 100, 20);
		add(TEST);
		TEST.setEnabled(false);
		TEST.addActionListener(new EventsButton());
		
		ValueLabel.setBounds(350, 310, 250, 20);
		add(ValueLabel);

	    jTextAreaRepaint();
	}
	public void actionPerformed(ActionEvent e) {

		JMenuItem source = (JMenuItem)(e.getSource());
		StringItem=source.getText();
		
		//-----
		if(StringItem.equals("Sending mail")){			
			Sender = JOptionPane.showInputDialog("Sending mail");		
			if(Sender != null && !Sender.equals("")) {
				jTextAreaRepaint();
			}else {
				Sender=null;
				jTextAreaRepaint();
			}
		}
		//-----
		
		//-----
		if(StringItem.equals("Password sending email")){			
			Password = JOptionPane.showInputDialog("Password sending email");		
			if(Password != null && !Password.equals("")) {
				jTextAreaRepaint();
			}else {
				Password=null;
				jTextAreaRepaint();
			}
		}
		//-----
		
		//------- 
		if(StringItem.equals("Add email addresses to receive emails")){					
			String email = JOptionPane.showInputDialog("Address to receive emails");
			if(email != null && !email.equals("")) {
				ToList.add(email);
				jTextAreaRepaint();
			}
		}
		//------- 
		
		//---------
		if(StringItem.equals("Delete email addresses to receive emails")){		
			Object [] objetos = ToList.toArray ();			
			Object obj10 = JOptionPane.showInputDialog(null,"Delete email addresses to receive emails", "CHOOSE", JOptionPane.QUESTION_MESSAGE, null, objetos,null);			
			if(obj10!=null && !obj10.equals("")) {								
				for (Iterator<String> iterator = ToList.iterator(); iterator.hasNext(); ) {		
					String StringIterator = iterator.next();
					if (obj10.toString().equals(StringIterator)) {
						iterator.remove();
					}
				}
				jTextAreaRepaint();
			}		
		}
		//---------
		
		//-------------
		if(StringItem.equals("Min. Value")){			
			String MinValueString = JOptionPane.showInputDialog("Min. Value");			
			if(MinValueString != null && !MinValueString.equals("")) {
				try {
					MinValue= Integer.valueOf(MinValueString);
					jTextAreaRepaint();
				}catch(Exception q) {
					JOptionPane.showMessageDialog(null, "Put a negative or positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				MinValue=-9999999;
				jTextAreaRepaint();
			}				
		}
		//-------------
		
		//-----------
		if(StringItem.equals("Max. Value")){			
			String MaxValueString = JOptionPane.showInputDialog("Max. Value");			
			if(MaxValueString != null && !MaxValueString.equals("")) {
				try {
					MaxValue= Integer.valueOf(MaxValueString);
					jTextAreaRepaint();
				}catch(Exception q) {
					JOptionPane.showMessageDialog(null, "Put a negative or positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}else {
				MaxValue=-9999999;
				jTextAreaRepaint();
			}
		}
		//-----------
		
		//---------------
		if(StringItem.equals("Every time a sample was taken")){				
			samples.setVisible(true);
		}
		//---------------
		
		//-----------------
		if(StringItem.equals("Every time a sample file is sent")){
			file.setVisible(true);
		}
		//-----------------
		
		//-----------------
		if(StringItem.equals("Every time a alert is sent")){
			alert.setVisible(true);
		}
		//-----------------		
	}	
	
	//*****************************************
	public void jTextAreaRepaint() {		
		jTextArea.selectAll();
		jTextArea.replaceSelection("");
		jTextArea.append("--------------Setting--------------" + "\n");		
		jTextArea.append("Your IP address is: " + host.getIPadress()  + ":" + port +"\n");		
		if(Sender != null) {
			jTextArea.append("Sending mail: " + Sender + "\n");	
		}		
		if(Password != null) {
			jTextArea.append("Password mail: " + Password + "\n");	
		}
		for (Object anObject: ToList) {
			String theMyObject = (String) anObject;	
			jTextArea.append("Address to receive emails: " + theMyObject + "\n");
		}	
		if(MinValue != -9999999) {
			jTextArea.append("Thresholds value for sending email alerts. MinValue: " + MinValue +"\n");
		}	
		
		if(MaxValue != -9999999) {
			jTextArea.append("Thresholds value for sending email alerts. MaxValue: " + MaxValue +"\n");
		}		
		if(samples.getStringTimeUnit() != null && samples.getValor() !=0) {
			jTextArea.append("Take a sample each : " + samples.getValor() + "    " + samples.getStringTimeUnit()  +"\n");
		}		
		if(file.getStringTimeUnit() != null && file.getValor() !=0) {
			jTextArea.append("Send one file each : " + file.getValor() + "    " + file.getStringTimeUnit() +"\n");
		}
		if(alert.getStringTimeUnit() != null && alert.getValor() !=0) {
			jTextArea.append("Send one alert each : " + alert.getValor() + "    " + alert.getStringTimeUnit() +"\n");
		}		
		jTextArea.append("--------------Setting--------------" + "\n");
		jTextArea.append("" + "\n");	
		reviewSettings();
	}	
	//*****************************************
	
	////////////////////////////////////////////////////////
	public void reviewSettings() {
		jTextArea.append("--------------Missing--------------" +"\n");		
		if(Sender==null || Sender=="") {
		jTextArea.append("Sending Mail" +"\n");
			r[0]=false;
		}else {
			r[0]=true;
		}
		if(Password==null || Password=="") {
		jTextArea.append("Password Mail" +"\n");
			r[1]=false;
		}else {
			r[1]=true;
		}
		if(ToList.isEmpty()) {
			jTextArea.append("Addresses to receive emails" +"\n");
			r[2]=false;
		}else {
			r[2]=true;
		}	
		if( MinValue==-9999999) {
			jTextArea.append("Thresholds min value" +"\n");
			r[4]=false;
		}else {
			r[4]=true;
		}		
		if( MaxValue==-9999999) {
			jTextArea.append("Thresholds max value" +"\n");
			r[3]=false;
		}else {
			r[3]=true;
		}
		if(samples.getStringTimeUnit() == null  || samples.getValor()==0) {
			jTextArea.append("Time a sample was taken" +"\n");
			r[5]=false;
		}else {
			r[5]=true;
		}
		if(file.getStringTimeUnit() == null  || file.getValor()==0) {
			jTextArea.append("Send a file every time" +"\n");
			r[6]=false;
		}else {
			r[6]=true;
		}
		
		if(alert.getStringTimeUnit() == null  || alert.getValor()==0) {
			jTextArea.append("Send a alert every time" +"\n");
			r[7]=false;
		}else {
			r[7]=true;
		}
		jTextArea.append("--------------Missing--------------" +"\n");
			
		flagReviewSettings=r[0] & r[1] & r[2] & r[3] & r[4] & r[5] & r[6] & r[7];
		
		if (flagReviewSettings) {			
			ONOFF.setEnabled(true);			
		}else {	
			ONOFF.setText("OFF");
			ONOFF.setBackground(Color.RED);
			ONOFF.setEnabled(false);
		}	
	}
	////////////////////////////////////////////////////////
	
	//INTER CLASS PopupWindows******************************************************************************
	private class PopupWindows extends JFrame implements ActionListener, DocumentListener{

		private static final long serialVersionUID = 1L;
		private JRadioButton jRadioButtonSeconds=new JRadioButton("seconds", false); 
		private JRadioButton jRadioButtonMinutes=new JRadioButton("minutes", false);
		private JRadioButton jRadioButtonHours=new JRadioButton("hours", false);
		private JRadioButton jRadioButtonDays=new JRadioButton("days", false);
		private ButtonGroup buttonGroup=new ButtonGroup();
		private JPanel panel=new JPanel();;
		private JLabel labelUnitis=new JLabel("    Put the number of units");
		private JTextField jtextfieldNumberOfUnits=new JTextField(5);	
		private String uniTime;
		private int valor;
	
		public PopupWindows(int bounds[], String title, String uniTime, int valor) {
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);	
			setType(JFrame.Type.UTILITY);		//I remove decoration to the frame only in windows
			setBounds(bounds[0], bounds[1], bounds[2],bounds[3]);
			setResizable(false);
			setTitle(title);
			this.uniTime=uniTime;
			this.valor=valor;		
			buttonGroup.add(jRadioButtonSeconds);
			buttonGroup.add(jRadioButtonMinutes);
			buttonGroup.add(jRadioButtonHours);
			buttonGroup.add(jRadioButtonDays);
			panel.add(jRadioButtonSeconds);
			panel.add(jRadioButtonMinutes);
			panel.add(jRadioButtonHours);
			panel.add(jRadioButtonDays);
			panel.add(labelUnitis);
			panel.add(jtextfieldNumberOfUnits);
			jRadioButtonSeconds.addActionListener(this);
			jRadioButtonMinutes.addActionListener(this);
			jRadioButtonHours.addActionListener(this);
			jRadioButtonDays.addActionListener(this);		
			jtextfieldNumberOfUnits.addActionListener(this);			
			jtextfieldNumberOfUnits.getDocument().addDocumentListener(this);		
			add(panel);
		}	
		public void actionPerformed(ActionEvent e) {		
			if (e.getSource()==jRadioButtonSeconds) {
				uniTime="SECONDS";
				jTextAreaRepaint();
			}
			if(e.getSource()==jRadioButtonMinutes) {
				uniTime="MINUTES";
				jTextAreaRepaint();
			}
			if(e.getSource()==jRadioButtonHours) {
				uniTime="HOURS";
				jTextAreaRepaint();
			}
			if(e.getSource()==jRadioButtonDays) {
				uniTime="DAYS";
				jTextAreaRepaint();
			}
		}
		public void changedUpdate(DocumentEvent arg0) {}
		public void insertUpdate(DocumentEvent arg0) {
			getChart();
		}
		public void removeUpdate(DocumentEvent arg0) {
			getChart();
		}	
		public void getChart() {			
			String string1=jtextfieldNumberOfUnits.getText();		
			if(string1 != null && !string1.equals("")) {
				try {
					valor= Integer.valueOf(string1);
					jTextAreaRepaint();
				}catch(Exception q) {
					JOptionPane.showMessageDialog(null, "Put a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);						
					SwingUtilities.invokeLater(new Runnable() {///////// Very good
			            public void run() {
			            	jtextfieldNumberOfUnits.setText("");
			            }
			        });
				}
			}			
		}		
		public String getStringTimeUnit() {
			return uniTime;
		}
		public int getValor() {
			return valor;
		}
	}
	//INTER CLASS PopupWindows******************************************************************************
	
	//INTERCLASS EventsButton iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
	private class EventsButton implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==ONOFF) {			
				if(flagReviewSettings==true && flagONOFF) {				
					ONOFF.setBackground(Color.GREEN);
					ONOFF.setText("ON");			
					flagONOFF=!flagONOFF;
					try {
						executor=new Executor(Sender, Password, ToList, MaxValue, MinValue, samples.getStringTimeUnit(), 
								samples.getValor(), file.getStringTimeUnit(), file.getValor(), alert.getStringTimeUnit(), alert.getValor(), jTextArea, makeFile, ValueLabel);
						TEST.setEnabled(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					menu.setEnabled(false);								
				}
				else{					
					jTextAreaRepaint();					
					ONOFF.setBackground(Color.RED);
					ONOFF.setText("OFF");		
					flagONOFF=!flagONOFF;
					executor.shutdownExecutor();
					executor=null;
					menu.setEnabled(true);
					TEST.setEnabled(false);
				}		
			}	
			if(e.getSource()==TEST) {
				if(executor!=null) {
					executor.sendTestEmail();
				}
			}
		}
	}
	//INTERCLASS EventsButton iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
}	
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
	
//ghghghgggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg
class Executor {
	
	private List <String> ToList = new ArrayList <String> ();	
	private int MaxValue;
	private int MinValue;
	private Mail mail;
	private JTextArea jTextArea;
	private ScheduledExecutorService schd;
	private MakeFile makeFile;
	private ReadThermocouple readThermocouple;
	private int cont=0;
	private JLabel ValueLabel;
	
	public Executor(String Sender, String Password, List <String> ToList, int MaxValue, int MinValue, String sampleString, int sampleValue, 
			String fileString, int fileValue, String alertString, int alertValue, JTextArea jTextArea, MakeFile makeFile, JLabel ValueLabel) throws IOException{
		
		if(System.getProperty("os.name").equals("Linux")) {					
			readThermocouple= new ReadThermocouple();
		}
		
		mail=new Mail(Sender, Password);
		
		this.ToList = ToList;	
		this.MaxValue=MaxValue;
		this.MinValue=MinValue;
		this.jTextArea=jTextArea;
		this.makeFile=makeFile;
		this.ValueLabel=ValueLabel;
		
		schd = Executors.newScheduledThreadPool(3);
		schd.scheduleAtFixedRate(new Samples(), 0, sampleValue, TimeUnit.valueOf(sampleString));		//Sample
		schd.scheduleAtFixedRate(new Files(), fileValue, fileValue, TimeUnit.valueOf(fileString));		//Files
		schd.scheduleAtFixedRate(new AlertMail(), 0, alertValue, TimeUnit.valueOf(alertString));		//Alert				
	}
	public void shutdownExecutor() {
		schd.shutdown();
	}	
	public void sendTestEmail(){
		for(Object ee: ToList) {
			String receiver=ee.toString();
			String res=mail.sendMail(receiver, "Test", "Hello");
			if(res.equals("doneSendMail")) {
				jTextArea.append(receiver + "\t" + "Test done" + "\n");
			}else {
				jTextArea.append(receiver + "\t" + "Test fail" + "\n");
			}				
		}	
	}
	public void sendAlert() {	
		if(cont>=1) {
			for(Object ee: ToList) {
				String receiver=ee.toString();
				String res=mail.sendMail(receiver, "ALERT", "-ALERT-ALERT-ALERT-ALERT-");
				if(res.equals("doneSendMail")) {
					jTextArea.append(receiver + "\t" + "ALERT done" + "\n");
				}else {
					jTextArea.append(receiver + "\t" + "ALERT fail" + "\n");
				}				
			}
		}		
	}
	private double getReadThermocoupleValue() throws IOException {
		if(System.getProperty("os.name").equals("Linux")) {	
			double value=readThermocouple.getReadTermocouple();
			ValueLabel.setText("Value: " + value);			
			return value;		
		}else {
			double value=100000;
			ValueLabel.setText("Value: " + value);
			return 100;
		}
	}
	
	//INTERCLASS.............................................
	private class Samples implements Runnable{
		public void run() {
			
			try {
				makeFile.writeFile(getReadThermocoupleValue());
			} catch (IOException e) {
				e.printStackTrace();
			}						
			try {
				if( ((int)(getReadThermocoupleValue())) > MinValue && ((int)(getReadThermocoupleValue())) < MaxValue) {					
					cont++;
					if(cont==1) {
						sendAlert();
					}
				}else {
					cont=0;			
				}
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}	
	//INTERCLASS.............................................
	
	//INTERCLASS+++++++++++++++++++++++++++++++++++++++++++++++++++++
	private class AlertMail implements Runnable{
		public void run() {
			sendAlert();
		}	
	}	
	//INTERCLASS+++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	//INTERCLASS----------------------------------------------------------------
	private class Files implements Runnable{
		public void run() {
			if(!System.getProperty("os.name").equals("Linux")) {			// Estas en Win 			
				String addressFileText="C:\\Mail\\" + makeFile.getMakeFile() + ".txt";				
				for(Object ee: ToList) {
					String receiver=ee.toString();
					String res=mail.sendFile(receiver, "Data File", "", addressFileText);					
					if(res.equals("doneSendFile")) {
						jTextArea.append(receiver + "\t" + "Data File done" + "\n");
					}else {
						jTextArea.append(receiver + "\t" + "Data File fail" + "\n");
					}
				}
			}else {			
				String addressFileText="/home/pi/Desktop/Mail/" + makeFile.getMakeFile() + ".txt";											
				for(Object ee: ToList) {
					String receiver=ee.toString();
					String res=mail.sendFile(receiver, "Data File", "", addressFileText);					
					if(res.equals("doneSendFile")) {
						jTextArea.append(receiver + "\t" +  "Data File done" + "\n");
					}else {
						jTextArea.append(receiver + "\t" + "Data File fail" + "\n");
					}
				}
			}
		}		
	}
	//INTERCLASS----------------------------------------------------------------
}
//ghghghgggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg

//uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu
class MakeFile{
	
	private BufferedWriter bufferedWriter;
	private FileWriter fileWriter;
	private File file;
	private String path;
	private String nameFile;
	
	public MakeFile() throws IOException {
		
		if(!System.getProperty("os.name").equals("Linux")) {	//estas en Win			
			path="C:/Mail/";
		}else {
			path="/home/pi/Desktop/Mail/";
		}
	
		nameFile=JOptionPane.showInputDialog(null, null, "File Name", JOptionPane.CLOSED_OPTION);			
		file= new File(path + nameFile + ".txt");
	
		file.setWritable(true);		
		file.setExecutable(true);				
		fileWriter=new FileWriter(file,true);
		bufferedWriter=new BufferedWriter(fileWriter);
		bufferedWriter.write(new Time().getDate() + "\n" + "\n");
		bufferedWriter.newLine();
		bufferedWriter.close();	
		fileWriter.close();
	}
	public void writeFile(double value) throws IOException {
		fileWriter=new FileWriter(file,true);
		bufferedWriter=new BufferedWriter(fileWriter);
		bufferedWriter.write(new Time().getTime() + "\t" + "\t" + value );
		bufferedWriter.newLine();
		bufferedWriter.close();	
		fileWriter.close();
	}	
	public String getMakeFile() {
		return nameFile;
	}
}
//uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu

//*******************************************************************************************
class MakeFolder{						
	public MakeFolder() {
		if(!System.getProperty("os.name").equals("Linux")) {
			File directorio = new File("C:/Mail");
			if(!directorio.exists()) {
				directorio.mkdirs();
				JOptionPane.showMessageDialog(null, "A folder has been made in: C:/Mail",
						"INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "There is a folder in: C:/Mail",
						"INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}	
		}else {									//estas en Linux
			File directorio = new File("/home/pi/Desktop/Mail");
			if(!directorio.exists()) {
				directorio.mkdirs();
				JOptionPane.showMessageDialog(null, "A folder has been made in: /home/pi/Desktop/Mail",
						"INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "There is a folder in: /home/pi/Desktop/Mail",
						"INFORMATION MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}	
		}
	}
}
//*******************************************************************************************

//------------------------------------------------------------------------------------------------------------
class Mail{

	private Properties Fileprops;
	private Properties Textrops;
	private Session sessionFile;
	private Session sessionText;
	private MimeMessage mimeMessageFile;
	private MimeMessage mimeMessageText;	
	private Transport transport;
	private MimeBodyPart mimeBodyPart;
	private String Sender;
	private String Password;
	
	public Mail(String Sender, String Password) {	
		this.Sender=Sender;
		this.Password=Password;
	}
	
	//99999999999999999	  
	public String sendMail(String To, String Subject, String Text) {
		
		Textrops = System.getProperties();
		Textrops.put("mail.smtp.host", "smtp.gmail.com");  		
		Textrops.put("mail.smtp.user", Sender);
		Textrops.put("mail.smtp.clave", Password);    			
		Textrops.put("mail.smtp.auth", "true");    		
		Textrops.put("mail.smtp.starttls.enable", "true"); 		
		Textrops.put("mail.smtp.port", "587"); 						

	    sessionText = Session.getDefaultInstance(Textrops);
	    mimeMessageText = new MimeMessage(sessionText);

	    try {
	    	mimeMessageText.setFrom(new InternetAddress(Sender));
	    	mimeMessageText.addRecipients(Message.RecipientType.TO, To);	
	    	mimeMessageText.setSubject(Subject);
	    	mimeMessageText.setText(Text);
	        transport = sessionText.getTransport("smtp");
	        transport.connect("smtp.gmail.com", Sender, Password);
	        transport.sendMessage( mimeMessageText,  mimeMessageText.getAllRecipients());
	        transport.close();
	        
	        return "doneSendMail";
	    }
	    catch (MessagingException me) {
	        return "failSendMail";
	    }	    
	}
	//99999999999999999	 
	
	//ppppppppppppppppp
	public String sendFile(String To, String Subject, String Text, String fileName) {
			      
		Fileprops = System.getProperties();
		Fileprops.put("mail.smtp.host", "smtp.gmail.com");  				//Server Google SMTP 
		Fileprops.put("mail.smtp.user", Sender);
		Fileprops.put("mail.smtp.clave", Password);
		Fileprops.put("mail.smtp.auth", "true");
		Fileprops.put("mail.smtp.starttls.enable", "true"); 
		Fileprops.put("mail.smtp.port", "587"); 
		
		sessionFile = Session.getInstance(Fileprops, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Sender, Password);			
				}
		});

		try {
			mimeMessageFile = new MimeMessage(sessionFile);				// Create a default MimeMessage object.	         
			mimeMessageFile.setFrom(new InternetAddress(Sender));		// Set From: header field of the header.
			mimeMessageFile.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To));
			mimeMessageFile.setSubject(Subject);						// Set Subject: header field
			
			mimeBodyPart = new MimeBodyPart();							// Create the message part
			mimeBodyPart.setText(Text);									// Now set the actual message
			
			MimeMultipart mimeMultipart = new MimeMultipart();			// Create a multipar message
			mimeMultipart.addBodyPart(mimeBodyPart);					// Set text message part
			
			
			mimeBodyPart = new MimeBodyPart();							// Part two is attachment
			DataSource source = new FileDataSource(fileName);
						
			mimeBodyPart.setDataHandler(new DataHandler(source));
			mimeBodyPart.setFileName(fileName);
			mimeMultipart.addBodyPart(mimeBodyPart);
			
			mimeMessageFile.setContent(mimeMultipart);					// Send the complete message parts         
			Transport.send(mimeMessageFile);							// Send message
			
			return "doneSendFile";			
		}catch (MessagingException e){
			return "failSendFile";
		}			
	}
	//ppppppppppppppppp
}
//------------------------------------------------------------------------------------------------------------

//11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
class ReadThermocouple{
	
    private SpiDevice spiDevice = null;
    
    public ReadThermocouple() throws IOException {   	
    	//System.out.println("Starting Thermocouple Application.");
    	spiDevice = SpiFactory.getInstance(SpiChannel.CS0,SpiDevice.DEFAULT_SPI_SPEED,SpiDevice.DEFAULT_SPI_MODE);
    }     
    public double getReadTermocouple() throws IOException {
    	 byte data[] = new byte[] {0,0, 0, 0};								// Dummy payloads. It's not responsible for anything.        
         byte[] result = spiDevice.write(data);				 				//Request data from MAX31855 via SPI with dummy pay-load        
         if((result[0] & 128)==0 && (result[1] & 1)==1 ) {					//Sign bit is 0 and D16 is high corresponds to Thermocouple not connected.
             System.out.println("Thermocouple is not connected");
             return 0;
         }
         String stringResult=String.format("%32s",Integer.toString(ByteBuffer.wrap(result).getInt(), 2)).replace(' ', '0');
         double valInt=0.0;       
         if(stringResult.charAt(0)=='1' ){  								//Checking for signed bit. If need to convert to 2's Complement.
          	StringBuilder onesComplementBuilder = new StringBuilder();        	
         	for(char bit : stringResult.substring(0, 12).toCharArray()) {
         	    onesComplementBuilder.append((bit == '0') ? 1 : 0);  		// if bit is '0', append a 1. if bit is '1', append a 0.
         	}
         	String onesComplement = onesComplementBuilder.toString();
         	valInt = -1*( Integer.valueOf(onesComplement, 2) + 1); // two's complement = one's complement + 1. This is the positive value of our original binary string, so make it negative again.
         }else{
         	valInt=Integer.parseInt(stringResult.substring(0, 12),2); 		//+ve no convert to double value
         }
         if(stringResult.charAt(12)=='1') 									//Check for D18 and D19 for fractional values
         	valInt+=0.5;	
         if(stringResult.charAt(13)=='1')
         	valInt+=0.25;
         return valInt;
    }
}
//11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

//wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww
class Time{
	 
private Calendar now;
private String formatTime="%02d:%02d:%02d";
private String formatDate="%02d/%02d/%04d";
private String StringTime;
private String StringDate;

	public Time() {				
		now = Calendar.getInstance();
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		int month=now.get(Calendar.MONTH)+1;
		int day= now.get(Calendar.DAY_OF_MONTH);
		int year=now.get(Calendar.YEAR);
		StringTime=String.format(formatTime, hour, minute, second);
		StringDate=String.format(formatDate, month, day, year);		
	}	
	public String getTime() {
		return StringTime;
	}
	public String getDate(){
		return StringDate;
	}
}
//wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
class Host{
	
	private String SOName;
	private String stringIPadress;		
	private StringBuilder stringBuilder;
	private String s;
	private String stringText;
	private String[] splits;
	private InetAddress IP=null;
	
	public Host(int port) throws IOException, URISyntaxException{

		SOName=System.getProperty("os.name");
		
		if(SOName.equals("Linux")) {		//You are in Linux (Raspberry Pi)						
			stringBuilder = new StringBuilder();				//Get the IP address in linux
			Process p = Runtime.getRuntime().exec("ifconfig wlan0");                                                                                                                                               
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdInput.readLine()) != null) {
				stringBuilder.append(s);				
			}	
			stringText=stringBuilder.toString();
			splits = stringText.split("inet|netmask");	
			stringIPadress=splits[1].toString();
			stringIPadress=stringIPadress.replaceAll("\\s", "");				
		}else {								//You're not on Linux	
			IP = InetAddress.getLocalHost();
			stringIPadress=IP.getHostAddress();
		}		
	}
	public String getIPadress() {
		return stringIPadress;
	}
}
//hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh

