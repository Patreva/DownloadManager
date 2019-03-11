/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 *
 * @author Patrick
 */
//This class manages the download tables data
public class Downloadstablemodel extends AbstractTableModel implements Observer{
    //These are the names for the tables columns 
    private static final String[] columnNames={"URL","Size","Progress","Status"};
    //These are the classes for each cloumns values 
    private static final Class[] columnClasses={String.class, String.class, JProgressBar.class, String.class};
    //The table list of downloads 
    private ArrayList<Download> downloadList=new ArrayList<Download>();
    //Add a new download to the table 
    public void addDownload(Download download){
        //Register to be notfied when the download changes
        download.addObserver(this);
        downloadList.add(download);
        //fire table row insertion notifacation to table
        fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
    }
    //Get a download for a specified  Row
    public Download getDownload(int row){
        return downloadList.get(row);
    }
    //remove download from the list
    public void clearDownload(int row){
        downloadList.remove(row);
         //fire table row deletion notification to table
         fireTableRowsDeleted(row, row);
    }
   //Get tables column count
    public int getColumnCount(){
        return columnNames.length;
    }
    //Get a columns name 
    public String getColumnName(int col){
        return columnNames[col];
    }
    //Get a columns class
    public Class getColumnClass(int col){
    return columnClasses[col];
}
 //Get tables row count
    public int getRowCount(){
        return downloadList.size();
    }
    //Get value for a specific row and column combination
    public Object getValueAt(int row , int col){
        Download download=downloadList.get(row);
        switch(col){
            case 0://URL
                return download.getUrl();
            case 1://size
                int size =download.getSize();
            case 2://Progress
                return new Float(download.getProgress());
            case 3: //status
                return Download.STATUSES[download.getStatus()];
        }
        return "";
        //update is called when a Download notifies its Observer of any changes
    }
        public void update(Observable o, Object arg){
            int index=downloadList.indexOf(o);
            //fire table row update notification to table
            fireTableRowsUpdated(index, index);
        } 
    
}
