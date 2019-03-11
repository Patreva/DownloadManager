/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadmanager;

/**
 *
 * @author Patrick
 */
import java.io.*;
import java.net.*;
import java.util.*;
public class Download extends Observable implements Runnable {
    //max size of the download 
    private static final int MAX_BUFFER_SIZE=1024;
    //status names 
    public static final String STATUSES[]={"Downloading","Paused","Complete","Cancelled","Error"};
    //These are the status codes
    public static final int DOWNLOADING=0;
    public static final int PAUSED=1;
     public static final int COMPLETE=2;
      public static final int CANCELLED=3;
       public static final int ERROR=4;
       //download url
       private URL url;
       //size of the download in bytes 
       private int size;
       //number of bytes downloaded
       private int downloaded;
       //current status of download 
       private int status;
       //constructor
       public Download(URL url){
           this.url=url;
           size=-1;
           downloaded=0;
           status=DOWNLOADING;
           //Begin the download 
           download();
       }
       //Get this downloads URL
       public String getUrl(){
           return url.toString();
           
       }
       //GET this downloads size
       public int getSize(){
           return size;
       }
           //Get this downlods progress
           public float getProgress(){
               return ((float)downloaded/size)*100;
           }
       //GET this download status
       public int getStatus(){
           return status;
       }
       public void pause(){
           status=PAUSED;
           stateChanged();
           
       }
       //resume this download 
       public void resume(){
           status=DOWNLOADING;
           stateChanged();
           download();
       }
       //cancel this download 
       public void cancel(){
           status=CANCELLED;
           stateChanged();
       }
       //Mark this download as having an error
       public void error(){
           status=ERROR;
           stateChanged();
       }
       //start or resume downlading 
       private void download(){
           Thread thread=new Thread();
           thread.start();
       }
       //Get a file name portion of Url
       private String getFileName(URL url){
           String filename=url.getFile();
           return filename.substring(filename.lastIndexOf('/')+1);
       }
       //download file
       public void run(){
           RandomAccessFile file=null;
           InputStream stream=null;
           try{
               //open connection to URL
               HttpURLConnection connection=(HttpURLConnection)url.openConnection();
               //specify what portion of file to download
               connection.setRequestProperty("Range", "Bytes=" +downloaded + "-");
               //connect to the server 
               connection.connect();
               //make sure the response code is in the 200 range
               if(connection.getResponseCode()/100!=2){
                   error();
               }
               //check for valid content length
               int contentLength=connection.getContentLength();
               if((contentLength<1)){
                   error();
               }
               //set this size for this download if it hasnt been already set
               if(size==-1){
                   size=contentLength;
                   stateChanged();
               }
               //open file and seek to the end of it
               file=new RandomAccessFile(getFileName(url),"rw");
               file.seek(downloaded);
               stream=connection.getInputStream();
               while(status==DOWNLOADING){
                   //Size of buffer according to how much of the file is left to download 
                   byte buffer[];
                   if(size-downloaded>MAX_BUFFER_SIZE){
                       buffer=new byte[MAX_BUFFER_SIZE];
                       
                   }else{
                       buffer=new byte[size-downloaded];
                       
                   }
                   //Read from server into the buffer
                   int read =stream.read(buffer);
                   if(read==-1)
                       break;
                    //write buffer to file
               file.write(buffer,0,read);
               downloaded+=read;
               stateChanged();
               }
              //change status to complete if this point was reached because downloading has finished
              if(status==DOWNLOADING){
                  status=COMPLETE;
                  stateChanged();
              }
           }catch(Exception e){
               error();
           }finally{
               //close file
               if(file!=null){
                   try{
                       file.close();
                   }catch(Exception e){
                       
                   }
                   //close connection to server
                   if(stream!=null){
                       try{
                           stream.close();
                       }catch(Exception e){
                           
                       }
                   }
                   //Notify obsevers that this download status has change
               }
           }
          
       }
        private void stateChanged(){
             setChanged();
             notifyObservers();
           }
}
