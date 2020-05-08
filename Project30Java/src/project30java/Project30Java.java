
package project30java;

import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;
import com.pi4j.io.spi.*;
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
	    		int opcion=JOptionPane.showConfirmDialog(null, "Do you want to close the program?", "CONFIRMATION", JOptionPane.OK_CANCEL_OPTION);
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
	private JMenu menu = new JMenu(" Settings ");; 
	private JMenu submenu = new JMenu("Safe working interva");
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
	private String samplesUnit;
	private int samplesValor;
	private PopupWindows file;
	private String fileUnit;
	private int fileValor;
	private PopupWindows alert;
	private String alertUnit;
	private int alertValor;
	private boolean flagONOFF=true;
	private boolean flagReviewSettings=false;	
	private boolean r[]=new boolean[8];
	private Host host;
	private Executor executor=null;
	private MakeFile makeFile;
	private List <String> dataUsersLoad = new ArrayList <String> ();
	private List <String> dataUsersSave = new ArrayList <String> ();
	
	public Panel() throws IOException, URISyntaxException {
		
		host=new Host(port);
		makeFile=new MakeFile();
		
		loadDataUsers();
		
		jTextArea.setEditable(false);
		
		int[] bounds100={40, 40, 600, 80};		//hago los objetos con los valores leidos en loadDataUsers()
		samples = new PopupWindows(bounds100, "SAMPLES", samplesUnit, samplesValor);		
		int[] bounds200={60, 60, 600, 80};	
		file = new PopupWindows(bounds200, "FILE", fileUnit , fileValor);		
		int[] bounds300={80, 80, 600, 80};
		alert= new PopupWindows(bounds300, "ALERTS", alertUnit, alertValor);
		
		setLayout(null);
		
		menuBar.add(menu);
		menuItem = new JMenuItem("Send e-mail from:"); menuItem.addActionListener(this); menu.add(menuItem);									//1	
		menuItem = new JMenuItem("E-mail password (sending account):"); menuItem.addActionListener(this); menu.add(menuItem);					//2
		menu.addSeparator();
		menuItem = new JMenuItem("Send e-mail to:"); menuItem.addActionListener(this); menu.add(menuItem);										//3
		menuItem = new JMenuItem("Delete send the email to:"); menuItem.addActionListener(this); menu.add(menuItem);							//4
		menu.addSeparator();	
			menuItem = new JMenuItem("Min. Value"); menuItem.addActionListener(this); menu.add(menuItem);										//5
			submenu.add(menuItem);
			menuItem = new JMenuItem("Max. Value"); menuItem.addActionListener(this); menu.add(menuItem);										//6
			submenu.add(menuItem);
		menu.add(submenu);	
		menu.addSeparator();	
		menuItem = new JMenuItem("Set acquisition timing:"); menuItem.addActionListener(this); menu.add(menuItem);								//7
		menuItem = new JMenuItem("Set time interval to send records:"); menuItem.addActionListener(this); menu.add(menuItem);					//8	
		menuItem = new JMenuItem("E-mail alert frequency:"); menuItem.addActionListener(this); menu.add(menuItem);								//9
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
	public void loadDataUsers() throws IOException {	
		dataUsersLoad=makeFile.readDataUsersFile();
		Object[] objDataUsers=dataUsersLoad.toArray();		
		int lengthObjDataUsers=objDataUsers.length;
		
		if(lengthObjDataUsers>5) {
			Sender=objDataUsers[0].toString();
			Password=objDataUsers[1].toString();	
			ToList.add(objDataUsers[2].toString());
			
			for(int i=3; i<lengthObjDataUsers-9; i++) {
				ToList.add(objDataUsers[i].toString());
			}
			
			MinValue=Integer.parseInt(objDataUsers[lengthObjDataUsers-9].toString());
			MaxValue=Integer.parseInt(objDataUsers[lengthObjDataUsers-8].toString());		
			samplesValor=Integer.parseInt(objDataUsers[lengthObjDataUsers-7].toString());
			samplesUnit=objDataUsers[lengthObjDataUsers-6].toString();		
			fileValor=Integer.parseInt(objDataUsers[lengthObjDataUsers-5].toString());
			fileUnit=objDataUsers[lengthObjDataUsers-4].toString();
			alertValor=Integer.parseInt(objDataUsers[lengthObjDataUsers-3].toString());
			alertUnit=objDataUsers[lengthObjDataUsers-2].toString();
		}
	}
	public void saveDataUsers() throws IOException {
		dataUsersSave.add(Sender);
		dataUsersSave.add(Password);
		for (Iterator<String> iterator = ToList.iterator(); iterator.hasNext(); ) {		
			String StringIterator = iterator.next();			
			dataUsersSave.add(StringIterator);
		}
		dataUsersSave.add(Integer.toString(MinValue));
		dataUsersSave.add(Integer.toString(MaxValue));		
		dataUsersSave.add(Integer.toString(samples.getValor()));
		dataUsersSave.add(samples.getStringTimeUnit());
		dataUsersSave.add(Integer.toString(file.getValor()));
		dataUsersSave.add(file.getStringTimeUnit());
		dataUsersSave.add(Integer.toString(alert.getValor()));
		dataUsersSave.add(alert.getStringTimeUnit());	
		makeFile.writeDataUsersFile(dataUsersSave);
	}
	public void actionPerformed(ActionEvent e) {

		JMenuItem source = (JMenuItem)(e.getSource());
		StringItem=source.getText();
		
		//-----
		if(StringItem.equals("Send e-mail from:")){			
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
		if(StringItem.equals("E-mail password (sending account):")){			
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
		if(StringItem.equals("Send e-mail to:")){					
			String email = JOptionPane.showInputDialog("Address to receive emails");
			if(email != null && !email.equals("")) {
				ToList.add(email);
				jTextAreaRepaint();
			}
		}
		//------- 
		
		//---------
		if(StringItem.equals("Delete send the email to:")){		
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
					JOptionPane.showMessageDialog(null, "Enter an integer value", "ERROR", JOptionPane.ERROR_MESSAGE);
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
		if(StringItem.equals("Set acquisition timing:")){				
			samples.setVisible(true);
		}
		//---------------
		
		//-----------------
		if(StringItem.equals("Set time interval to send records:")){
			file.setVisible(true);
		}
		//-----------------
		
		//-----------------
		if(StringItem.equals("E-mail alert frequency:")){
			alert.setVisible(true);
		}
		//-----------------		
	}	
	
	//*****************************************
	public void jTextAreaRepaint() {		
		jTextArea.selectAll();
		jTextArea.replaceSelection("");
		jTextArea.append("--------------Settings--------------" + "\n");		
		jTextArea.append("Your IP address is: " + host.getIPadress()  + ":" + port +"\n");		
		if(Sender != null) {
			jTextArea.append("Send e-mail from: " + Sender + "\n");	
		}		
		if(Password != null) {
			jTextArea.append("E-mail password (sending account): " + Password + "\n");	
		}
		for (Object anObject: ToList) {
			String theMyObject = (String) anObject;	
			jTextArea.append("Send e-mail to: " + theMyObject + "\n");
		}	
		if(MinValue != -9999999) {
			jTextArea.append("Safe working interva. MinValue: " + MinValue + "°C" + "\n");
		}	
		
		if(MaxValue != -9999999) {
			jTextArea.append("Safe working interva MaxValue: " + MaxValue + "°C" + "\n");
		}		
		if(samples.getStringTimeUnit() != null && samples.getValor() !=0) {
			jTextArea.append("Set acquisition timing: " + samples.getValor() + "    " + samples.getStringTimeUnit()  +"\n");
		}		
		if(file.getStringTimeUnit() != null && file.getValor() !=0) {
			jTextArea.append("Set time interval to send records: " + file.getValor() + "    " + file.getStringTimeUnit() +"\n");
		}
		if(alert.getStringTimeUnit() != null && alert.getValor() !=0) {
			jTextArea.append("E-mail alert frequency: " + alert.getValor() + "    " + alert.getStringTimeUnit() +"\n");
		}		
		jTextArea.append("--------------Settings--------------" + "\n");
		jTextArea.append("" + "\n");	
		reviewSettings();
	}	
	//*****************************************
	
	////////////////////////////////////////////////////////
	public void reviewSettings() {
		jTextArea.append("--------------Missing--------------" +"\n");		
		if(Sender==null || Sender=="") {
		jTextArea.append("Send e-mail from: ?" +"\n");
			r[0]=false;
		}else {
			r[0]=true;
		}
		if(Password==null || Password=="") {
		jTextArea.append("E-mail password (sending account): ?" +"\n");
			r[1]=false;
		}else {
			r[1]=true;
		}
		if(ToList.isEmpty()) {
			jTextArea.append("Send e-mail to: ?" +"\n");
			r[2]=false;
		}else {
			r[2]=true;
		}	
		if( MinValue==-9999999) {
			jTextArea.append("Safe working interva. MinValue: ?" +"\n");
			r[4]=false;
		}else {
			r[4]=true;
		}		
		if( MaxValue==-9999999) {
			jTextArea.append("Safe working interva MaxValue: ?" +"\n");
			r[3]=false;
		}else {
			r[3]=true;
		}
		if(samples.getStringTimeUnit() == null  || samples.getValor()==0) {
			jTextArea.append("Set acquisition timing: ?" +"\n");
			r[5]=false;
		}else {
			r[5]=true;
		}
		if(file.getStringTimeUnit() == null  || file.getValor()==0) {
			jTextArea.append("Set time interval to send records: ?" +"\n");
			r[6]=false;
		}else {
			r[6]=true;
		}
		
		if(alert.getStringTimeUnit() == null  || alert.getValor()==0) {
			jTextArea.append("E-mail alert frequency: ?" +"\n");
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
		private JRadioButton jRadioButtonSeconds; 
		private JRadioButton jRadioButtonMinutes;
		private JRadioButton jRadioButtonHours;
		private JRadioButton jRadioButtonDays;
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
			jtextfieldNumberOfUnits.setText(Integer.toString(valor));
			if("SECONDS".equals(uniTime)) {
				jRadioButtonSeconds=new JRadioButton("seconds", true);
			}else {
				jRadioButtonSeconds=new JRadioButton("seconds", false);
			}
			if("MINUTES".equals(uniTime)) {
				jRadioButtonMinutes=new JRadioButton("minutes", true);
			}else {
				jRadioButtonMinutes=new JRadioButton("minutes", false);
			}
			if("HOURS".equals(uniTime)) {
				jRadioButtonHours=new JRadioButton("hours", true);
			}else {
				jRadioButtonHours=new JRadioButton("hours", false);
			}
			if("DAYS".equals(uniTime)) {
				jRadioButtonDays=new JRadioButton("days", true);
			}else {
				jRadioButtonDays=new JRadioButton("days", false);
			}		
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
					JOptionPane.showMessageDialog(null, "Enter a positive integer", "ERROR", JOptionPane.ERROR_MESSAGE);						
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
						
						saveDataUsers();////////////////////////////////////////////////////////////////////////
						
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
	private String Sender;
	
	public Executor(String Sender, String Password, List <String> ToList, int MaxValue, int MinValue, String sampleString, int sampleValue, 
			String fileString, int fileValue, String alertString, int alertValue, JTextArea jTextArea, MakeFile makeFile, JLabel ValueLabel) throws IOException{
		
		if(System.getProperty("os.name").equals("Linux")) {					
			readThermocouple= new ReadThermocouple();
		}
		
		mail=new Mail(Sender, Password);
		this.Sender=Sender;
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
			String res=mail.sendMail(receiver, "Test", "This is a e-mail sent from: " + Sender + " No action is required.");
			if(res.equals("doneSendMail")) {
				jTextArea.append(receiver + "\t" + "Test done" + "\n");
			}else {
				jTextArea.append(receiver + "\t" + "Test failed" + "\n");
			}				
		}	
	}
	public void sendAlert() throws IOException {	
		if(cont>=1) {
			for(Object ee: ToList) {
				String receiver=ee.toString();
				String res=mail.sendMail(receiver, "Temperature ALERT!", "ALERT!! " + Sender + " has detected a temperature value out of the set working range. Current temperature is: " + getReadThermocoupleValue() + "°C");
				if(res.equals("doneSendMail")) {
					jTextArea.append(receiver + "\t" + "Alert done" + "\n");
				}else {
					jTextArea.append(receiver + "\t" + "Alert failed" + "\n");
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
			double value=100;
			ValueLabel.setText("Value: " + value);
			return 100;
		}
	}
	
	//INTERCLASS.............................................
	private class Samples implements Runnable{
		public void run() {
			
			try {
				makeFile.samplesWriteFile(getReadThermocoupleValue());
			} catch (IOException e) {
				e.printStackTrace();
			}						
			try {
				if( ((int)(getReadThermocoupleValue())) <= MinValue || ((int)(getReadThermocoupleValue())) >= MaxValue) {					
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
			try {
				sendAlert();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
						jTextArea.append(receiver + "\t" + "Data File failed" + "\n");
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
						jTextArea.append(receiver + "\t" + "Data File failed" + "\n");
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
	
	private BufferedWriter SamplesbufferedWriter;
	private FileWriter SamplesfileWriter;
	private File Samplesfile;
	private String Samplespath;
	private String SamplesnameFile;
	private BufferedReader DatabufferedReader;
	private FileReader DatafileReader;
	private List <String> dataFileReader = new ArrayList <String> ();
	private BufferedWriter DatabufferedWriter;
	private FileWriter DatafileWriter;
	private Iterator<String> iterator;

	public MakeFile() throws IOException {		
		if(!System.getProperty("os.name").equals("Linux")) {	//estas en Win			
			Samplespath="C:/Mail/";
		}else {
			Samplespath="/home/pi/Desktop/Mail/";
		}
	
		SamplesnameFile=JOptionPane.showInputDialog(null, null, "File Name", JOptionPane.CLOSED_OPTION);			
		Samplesfile= new File(Samplespath + SamplesnameFile + ".txt");
	
		Samplesfile.setWritable(true);		
		Samplesfile.setExecutable(true);				
		SamplesfileWriter=new FileWriter(Samplesfile,true);
		SamplesbufferedWriter=new BufferedWriter(SamplesfileWriter);
		SamplesbufferedWriter.write(new Time().getDate() + "\n" + "\n");
		SamplesbufferedWriter.newLine();
		SamplesbufferedWriter.close();	
		SamplesfileWriter.close();		
	}
	public void samplesWriteFile(double value) throws IOException {
		SamplesfileWriter=new FileWriter(Samplesfile,true);
		SamplesbufferedWriter=new BufferedWriter(SamplesfileWriter);
		SamplesbufferedWriter.write(new Time().getTime() + "\t" + "\t" + value );
		SamplesbufferedWriter.newLine();
		SamplesbufferedWriter.close();	
		SamplesfileWriter.close();
	}
	public List <String> readDataUsersFile() throws IOException {
		DatafileReader=new FileReader(new File(Samplespath + "dataUsers" + ".txt"));
		DatabufferedReader= new BufferedReader(DatafileReader);
		String string;
		do {
			string=DatabufferedReader.readLine();
			dataFileReader.add(string);
		}while (string != null);
		DatabufferedReader.close();
		DatafileReader.close();	
		return dataFileReader;
	}	
	public void writeDataUsersFile(List <String> data) throws IOException {		
		DatafileWriter=new FileWriter(new File(Samplespath + "dataUsers" + ".txt"),false);
		DatabufferedWriter=new BufferedWriter(DatafileWriter);		
		String StringItratore;		
		for (iterator =data.iterator(); iterator.hasNext(); ) {		
			StringItratore = iterator.next();
			if(StringItratore != null) {
				DatabufferedWriter.write(StringItratore) ;
				DatabufferedWriter.newLine();
			}
			else {
				DatabufferedWriter.write("null") ;
				DatabufferedWriter.newLine();
			}
			iterator.remove();		///salve Cesar
		}
		DatabufferedWriter.close();	
		DatafileWriter.close();
	}
	public String getMakeFile() {
		return SamplesnameFile;
	}
}
//uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu

//*******************************************************************************************
class MakeFolder{

	public MakeFolder() throws IOException {
		if(!System.getProperty("os.name").equals("Linux")) {
			File directorio = new File("C:/Mail");
			if(!directorio.exists()) {
				directorio.mkdirs();				
				File file=new File("C:/Mail/" + "dataUsers" + ".txt");						
				file.setWritable(true);		
				file.setExecutable(true);				
				FileWriter fileWriter=new FileWriter(file,false);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				bufferedWriter.write("Data Users");
				bufferedWriter.close();	
				fileWriter.close();
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
				File file=new File("/home/pi/Desktop/Mail/" + "dataUsers" + ".txt");						
				file.setWritable(true);		
				file.setExecutable(true);				
				FileWriter fileWriter=new FileWriter(file,false);
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
				bufferedWriter.write("Data Users");
				bufferedWriter.close();	
				fileWriter.close();
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

