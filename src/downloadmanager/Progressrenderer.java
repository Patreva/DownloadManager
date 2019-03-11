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
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
//This class renders JProgressor in table cell
public class Progressrenderer extends JProgressBar implements TableCellRenderer{
    //constructor for progresseRenderer
    public Progressrenderer(int min, int max){
        super(min, max);
    }
    //Returns this JprogressBar as the renderer for the given table cell
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row , int column){
        //set JProgressBar percent Complete value
        setValue((int ) ((Float) value).floatValue());
        return this;
    }
}
