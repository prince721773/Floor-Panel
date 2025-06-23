import javax.swing.*; 
import java.awt.*;     
import java.awt.event.*; 
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.List;

public class ControlPanel extends JPanel {
    private CanvasPanel canvasPanel;
    private JComboBox<String> roomTypeCombo;
    private JTextField widthField,heightField;
    private JButton addRoomButton, saveButton,loadButton,refreshButton;

    public ControlPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        setLayout(new GridLayout(10, 1));
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int panelWidth = (int)(ScreenSize.width*0.25);
        int panelHeight = (ScreenSize.height);
        setPreferredSize(new Dimension(panelWidth, panelHeight));

        roomTypeCombo = new JComboBox<>(new String[]{"Bedroom", "Bathroom", "Kitchen", "Drawing Room"});
        roomTypeCombo.setSelectedIndex(0);
        add(new JLabel("Room Type"));
        add(roomTypeCombo);

        widthField = new JTextField(10);
        heightField = new JTextField(10);
        add(new JLabel("Width"));
        add(widthField);
        add(new JLabel("Height"));
        add(heightField);
        addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> {
    try {
        String roomType = roomTypeCombo.getSelectedItem().toString();
        if (roomType.isEmpty()) {
            JOptionPane.showMessageDialog(canvasPanel, "Please select a room type.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String widthText = widthField.getText().trim();
        if (widthText.isEmpty()) {
            JOptionPane.showMessageDialog(canvasPanel, "Please enter a width.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int width = Integer.parseInt(widthText);  
        String heightText = heightField.getText().trim();
        if (heightText.isEmpty()) {
            JOptionPane.showMessageDialog(canvasPanel, "Please enter a height.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int height = Integer.parseInt(heightText); 
        if((width>0)&&(height>0)){
        canvasPanel.addRoom(roomType, width, height);
        }
        else {
            JOptionPane.showMessageDialog(canvasPanel, "Width and height must be positive numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }   
    }catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(canvasPanel, "Please enter valid numbers for width and height.", 
                                      "Input Error", JOptionPane.ERROR_MESSAGE);
    }
});
        // Save button and refresh button
        saveButton = new JButton("Save Plan");
        loadButton = new JButton("load plan");
         refreshButton= new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 int choice = JOptionPane.showConfirmDialog(canvasPanel,                          
            "Do you want to proceed?,any unsaved work will be lost",      
            "Confirmation",                 
            JOptionPane.YES_NO_OPTION       
        );
            if(choice==JOptionPane.YES_OPTION){
               canvasPanel.clearCanvas();
            }
            }
        });
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Plan");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filename = fileToSave.getAbsolutePath();

                if (!filename.endsWith(".plan")) {
                    filename += ".plan";
                }
                canvasPanel.savePlan(filename);
            }else{
                    JOptionPane.showMessageDialog(this, "Save operation canceled", "Information", JOptionPane.INFORMATION_MESSAGE);
                 }
                
    });
    loadButton.addActionListener(e->{
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setAcceptAllFileFilterUsed(false);
          fileChooser.setFileFilter(new FileNameExtensionFilter("Plan Files (*.plan)", "plan"));
            fileChooser.setDialogTitle("load Plan");
            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                String filename = fileToLoad.getAbsolutePath();
                List <Room> roomobjs= canvasPanel.getlist();
                List <Wall> wallobjs= canvasPanel.getitems();
                boolean userconfirm=true;
                if(!(roomobjs.isEmpty()) || !(wallobjs.isEmpty())){
                   int confirm = JOptionPane.showConfirmDialog(this,"previous work present on canvas would be lost if not saved!,do you want to proceed?","INFO",JOptionPane.YES_NO_OPTION);
                    if(confirm==JOptionPane.NO_OPTION){
                        userconfirm=false;
                    }
                }
                if(userconfirm){
                canvasPanel.clearCanvas();
                canvasPanel.loadPlan(filename);
                }else{
                    JOptionPane.showMessageDialog(this,"plan loading was aborted","INFO",JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this, "load operation canceled", "Information", JOptionPane.INFORMATION_MESSAGE);
             }
    });
        add(addRoomButton);
        add(saveButton);
        add(loadButton);
        add(refreshButton);
    }
}