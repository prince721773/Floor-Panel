import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class FloorPlanDesigner extends JFrame {
    private CanvasPanel canvasPanel;
    private ControlPanel controlPanel;

    public FloorPlanDesigner() {
        setTitle("2D FloorPlanDesigner");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        //ImageIcon image=new Imageicon();
        //setIconImage(image.getImage());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height; 
        setMinimumSize(new Dimension(800, 600));  
    setMaximumSize(new Dimension(screenWidth, screenHeight)); 
        

        canvasPanel=new CanvasPanel();
        controlPanel = new ControlPanel(canvasPanel);
         
        add(controlPanel, BorderLayout.WEST);
        add(canvasPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to save your work before exiting?",
                        "Save Work",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if(choice==JOptionPane.NO_OPTION){
                    System.exit(0);
                }
                else{
                    return;
                }
            }
        }); 

        pack();
    setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FloorPlanDesigner::new);
    }
}