/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 *
 * @author Patrick
 */
//The download manager 
public class Downloadmanager extends JFrame implements Observer {
//add download textfield
    private JTextField addTextField;
    //DownLoad tables data model
    private Downloadstablemodel tableModel;
    //Table listing downloads
    private JTable table;
    //These are the buttons for managing the selected download 
    private JButton pauseButton,resumeButton,cancelButton,clearButton;
    //currently selected download 
    private Download selectedDownload;
    //flag for whether or not table selection is being cleared 
    private boolean clearing;
    //constructor for Download Manager
    public Downloadmanager(){
        ///set application title
        setTitle("DownLoad Manager");
        //set Window Size
        setSize(640, 480);
        //Handle window closing events 
        addWindowListener(new WindowAdapter(){
        public void windowClosing(WindowEvent e){
            actionExit();
        }
    });
        //set up file menu
        JMenuBar menuBar=new JMenuBar();
        JMenu fileMenu=new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
       JMenuItem fileExitMenuItem=new JMenuItem("Exit",KeyEvent.VK_X);
       fileExitMenuItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
          actionExit();
      }  
    });
       fileMenu.add(fileExitMenuItem);
       menuBar.add(fileMenu);
       setJMenuBar(menuBar);
       //setUP add panel
       JPanel addPanel=new JPanel();
       addTextField=new JTextField(30);
       addPanel.add(addTextField);
       JButton addButton=new JButton("Add download");
       addButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
            actionAdd();
        }
    });
     addPanel.add(addButton);
     //set up Downloads table
     tableModel=new Downloadstablemodel();
     table=new JTable(tableModel);
     table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
       public void valueChanged(ListSelectionEvent e){
           tableSelectionChanged();
       } 
    });
     //Allow ony one row at a time to be selected 
     table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     //Set up ProgerssBar AS renderer for progress column 
     Progressrenderer renderer=new Progressrenderer(0,100);
     renderer.setStringPainted(true);//show progress text
     table.setDefaultRenderer(JProgressBar.class, renderer);
     //set Tables row height large enough to fit JProgressBar 
     table.setRowHeight((int) renderer.getPreferredSize().getHeight());
     //set up downloads panel
     JPanel downloadsPanel=new JPanel();
     downloadsPanel.setBorder(
     BorderFactory.createTitledBorder("Downloads"));
     downloadsPanel.setLayout(new BorderLayout());
     downloadsPanel.add(new JScrollPane(table),
     BorderLayout.CENTER);
     //SET up buttonspanel
     JPanel buttonsPanel=new JPanel();
     pauseButton=new JButton("Pause");
     pauseButton.addActionListener(new ActionListener()
     {
         public void actionPerformed(ActionEvent e){
        actionPause();     
         }
     });
     pauseButton.setEnabled(false);
     buttonsPanel.add(pauseButton);
     resumeButton =new JButton("Resume");
     resumeButton.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e){
             actionResume();
         }
     });
     resumeButton.setEnabled(false);
     buttonsPanel.add(resumeButton);
     cancelButton=new JButton("cancel");
     cancelButton.addActionListener(new ActionListener()
     {
        public void actionPerformed(ActionEvent e){
            actionCancel();
        } 
     });
     cancelButton.setEnabled(false);
     buttonsPanel.add(cancelButton);
      clearButton=new JButton("clear");
      clearButton.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e){
             actionClear();
         } 
      });
      clearButton.setEnabled(false);
      buttonsPanel.add(clearButton);
      //add panels to displays
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(addPanel,BorderLayout.NORTH);
      getContentPane().add(downloadsPanel,BorderLayout.CENTER);
      getContentPane().add(buttonsPanel,BorderLayout.SOUTH);
      
    }
    //exit this program 
      private void actionExit(){
       System.exit(0);
      }
    //Add a new download 
      private void actionAdd(){
          URL verifiedUrl=verifyUrl(addTextField.getText());
          if(verifiedUrl!=null){
              tableModel.addDownload(new Download(verifiedUrl));
              addTextField.setText("");//reset add Text Field
              
          }else{
              JOptionPane.showMessageDialog(this,"Invalid Download URL", "ERROR", JOptionPane.ERROR_MESSAGE);
              
          }
      }
      //VERIFY DOWNLOAD URL
      private URL verifyUrl(String url){
          //only allow HTTP URL
          if(!url.toLowerCase().startsWith("https://"))
              return null;
          //verify format of URL
          URL verifiedUrl=null;
          try{
              verifiedUrl=new URL(url);
          }catch(Exception e){
              return null;
          }
          //make sure the Url specifies a file 
          if(verifiedUrl.getFile().length()<2)
              return null;
          return verifiedUrl;
      }
      //called when the table row selection changes 
      private void tableSelectionChanged(){
          //Unregister from receiving notifications from the last selected download 
          if(selectedDownload!=null)
        selectedDownload.deleteObserver(Downloadmanager.this);  
          //if not in this the middle of clearing a download set the selected download and register to receive notifications from it 
          if(!clearing && table.getSelectedRow()>-1){
              selectedDownload=tableModel.getDownload(table.getSelectedRow());
              selectedDownload.addObserver(Downloadmanager.this);
              updateButtons();
          }
      }
    //pause the selected download 
      private void actionPause(){
          selectedDownload.pause();
          updateButtons();
      }
      //Resume the selected download 
      private void actionResume(){
          selectedDownload.resume();
          updateButtons();
      }
      //cancel the selected download
      private void actionCancel(){
          selectedDownload.cancel();
          updateButtons();
      }
      //clear the selected download 
      private void actionClear(){
          clearing=true;
          tableModel.clearDownload(table.getSelectedRow());
          clearing=false;
          selectedDownload=null;
          updateButtons();
          
      }
      //update each buttons state based off of the currrently selected downloads status
      private void updateButtons(){
          if(selectedDownload!=null){
              int status =selectedDownload.getStatus();
              switch(status){
                  case Download.DOWNLOADING:
                      pauseButton.setEnabled(true);
                      resumeButton.setEnabled(false);
                      cancelButton.setEnabled(true);
                      clearButton.setEnabled(false);
                      break;
                  case Download.PAUSED:
                      pauseButton.setEnabled(false);
                      resumeButton.setEnabled(true);
                      cancelButton.setEnabled(true);
                      clearButton.setEnabled(false);
                      break;
                  case Download.ERROR:
                        pauseButton.setEnabled(false);
                      resumeButton.setEnabled(true);
                      cancelButton.setEnabled(false);
                      clearButton.setEnabled(true);
                   default:
                        pauseButton.setEnabled(false);
                      resumeButton.setEnabled(false);
                      cancelButton.setEnabled(false);
                      clearButton.setEnabled(true);
              }
          }else{
              //No download is selected in the table
              pauseButton.setEnabled(false);
                      resumeButton.setEnabled(false);
                      cancelButton.setEnabled(false);
                      clearButton.setEnabled(false);
              
          }
                  
      }
      //update is called when a Download notifies its observers of any changes 
      public void update(Observable o, Object arg){
          //update buttons if the selected download has changed 
          if(selectedDownload!=null&& selectedDownload.equals(0))
              updateButtons();
      }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable(){
        public void run(){
            Downloadmanager manager=new Downloadmanager();
            manager.setVisible(true);
        }
    });
    }
    
}
