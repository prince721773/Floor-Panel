import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Collections;

public class CanvasPanel extends JPanel{
    private List<Room> rooms;
    private List<Wall> walls;
    private static final int WALL_WIDTH = 5;
    private Room selectedRoom=null; 
    private Rectangle fullBounds;
    private int initoffsetX,initoffsetY,initialX,initialY,canvasHeight,canvasWidth; 
    public CanvasPanel(){
        Dimension Screensize = Toolkit.getDefaultToolkit().getScreenSize();
        this.canvasWidth = (int) (Screensize.width * 0.75);
        this.canvasHeight = Screensize.height;
        setPreferredSize(new Dimension(canvasWidth,canvasHeight));
        setDoubleBuffered(true);
        rooms=new ArrayList<>();
        walls=new ArrayList<>();
        setBackground(Color.LIGHT_GRAY);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { handleMousePressed(e); }
            @Override
            public void mouseReleased(MouseEvent e) { handleMouseReleased(e); }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { handleMouseDragged(e); }
        });
    } 
    private void handleMousePressed(MouseEvent e) {   
        for (Room room : rooms) {
            if (room.mousecontains(e.getX(),e.getY())) {
                initoffsetX=(e.getX()-room.getX());
                initoffsetY=(e.getY()-room.getY());
                initialX=room.getX();
                initialY=room.getY();
                room.changestate(true);
                selectedRoom = room;
                repaint();
                break;
            }
        }
    }
Rectangle prevRect;
   
    private void handleMouseDragged(MouseEvent e) {
        if (selectedRoom != null) {
            selectedRoom.changestate(false);
            int mouseX = e.getX();
            int mouseY = e.getY();
            int newX=(mouseX)-(initoffsetX);
            int newY=(mouseY)-(initoffsetY);
            selectedRoom.setX(newX);
            selectedRoom.setY(newY);
            Rectangle currRect=new Rectangle(newX,newY,selectedRoom.getwidth(),selectedRoom.getheight());
            if(prevRect!=null){
             repaint(prevRect.union(currRect));
            }else{
                repaint(currRect);
            }
            prevRect=new Rectangle(currRect);
        }
    }
    
private void handleMouseReleased(MouseEvent e) {
    prevRect=null;
    if (selectedRoom != null) {
        Rectangle newrect = new Rectangle(selectedRoom.getX(),selectedRoom.getY(),selectedRoom.getwidth(),selectedRoom.getheight());
        if(!fullBounds.contains(newrect)){
            JOptionPane.showMessageDialog(this,"room has been dragged out of the canvas,returning to the original position","ERROR",JOptionPane.ERROR_MESSAGE);
            selectedRoom.setX(initialX);
            selectedRoom.setY(initialY);
            selectedRoom.changestate(false);
            repaint();
            
            selectedRoom=null;
            
        }
        else{
        boolean overlapDetected = false;
        // Check for overlaps with other rooms or walls
        outerLoop:
        for (Room room : rooms) {
            if (room != selectedRoom) {
                if(!walls.isEmpty()){
                for (Wall wall : walls) {
                    if (selectedRoom.getBounds().intersects(room.getBounds()) || 
                        selectedRoom.getBounds().intersects(wall.getBounds())) {
                        overlapDetected = true;
                        break outerLoop; // Break out of both loops
                    }
                }
            }else{
                 if(selectedRoom.getBounds().intersects(room.getBounds())){
                 overlapDetected =true;
                 break outerLoop;
                }
            }
        }
     }
        if (overlapDetected) {
            // Show an overlap warning dialog with a simple OK button
            JOptionPane.showMessageDialog(
                this,
                "Overlap detected! The room will revert to its original position.","OverlapError",JOptionPane.ERROR_MESSAGE
            );
            // Revert to the original position
            selectedRoom.setX(initialX);
            selectedRoom.setY(initialY);
        }
         else {
            // Update the original coordinates to the final position in case of no overlap
            Rectangle roomBounds = new Rectangle(initialX,initialY,selectedRoom.getwidth(),selectedRoom.getheight());
           // Room room=new Room(initialX,initialY,selectedRoom.getwidth(),selectedRoom.getheight());
            List<Wall> wallsToRemove = new ArrayList<>();
            for (Wall wall : walls) {
                if (Removewall(wall,roomBounds)) {
                     wallsToRemove.add(wall); // Collect walls to be removed
                }
            }
// Remove walls after the iteration is complete
            walls.removeAll(wallsToRemove);
        }
        selectedRoom.changestate(false);// Deselect the room and trigger repaint to remove the highlight
        repaint();
        // Clear selectedRoom after finalizing position
        selectedRoom = null;
        }
        newrect=null;
    }
}
    // PaintComponent method that draws the rooms and walls
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    
    // Retrieve the clipping bounds; this will be the rectangle passed by repaint(Rectangle)
    Rectangle clipBounds = g2d.getClipBounds();
     this.fullBounds = new Rectangle(0,0,canvasWidth,canvasHeight);
    boolean canvasRepaint=false;
    if(clipBounds.height>=(0.98*(fullBounds.height))&&clipBounds.width>=(0.98*(fullBounds.width))){
        canvasRepaint=true;
    }

        // Draw the rooms
        for (Room room : rooms) {
            if (room.getBounds().intersects(clipBounds)) {
                drawRoom(g, room);
            }
        }

        if((rooms.size()>1)&&canvasRepaint) {// Draw walls between adjacent rooms
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                Room room1 = rooms.get(i);
                Room room2 = rooms.get(j);

                // Check if the rooms are adjacent and draw walls accordingly
                if (isAdjacent(room1, room2)) {
                    drawWall(g, room1, room2);
                }
            }
        }
    }
    if ((!canvasRepaint)&&(!walls.isEmpty())) {
        for (Wall wall : walls) {
            if (wall.getBounds().intersects(clipBounds)) {
                redrawwall(g, wall);
            }
        }
    }
}

    // Function to draw a room
    private void drawRoom(Graphics g, Room room) {
        switch (room.getType()) {
            case "Bedroom":
                g.setColor(Color.GREEN);
                break;
            case "Bathroom":
                g.setColor(Color.BLUE);
                break;
            case "Kitchen":
                g.setColor(Color.RED);
                break;
            case "Drawing Room":
                g.setColor(Color.YELLOW);
                break;
            default:
                g.setColor(Color.LIGHT_GRAY);
        }
        g.fillRect(room.getX(), room.getY(), room.getwidth(), room.getheight()); // Draw the room
        if(room.isselected()){
            Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.ORANGE);  // Set color for the border highlight
                g2d.drawRect(room.getX(), room.getY(), room.getwidth(), room.getheight());  // Draw the selected room's border
            }
        }
    
        private boolean isAdjacent(Room room1, Room room2) {
           
        
            return horizontalAdjacent(room1,room2) || verticalAdjacent(room1,room2);
        }
        public boolean horizontalAdjacent(Room room1,Room room2){
             return ((room1.getY() < room2.getY() + room2.getheight() && 
             room1.getY() + room1.getheight() > room2.getY())||( (room2.getY() < room1.getY() + room1.getheight() && 
             room2.getY() + room2.getheight() > room1.getY()))) &&
            (Math.abs(room1.getX() + room1.getwidth() - room2.getX()) <= WALL_WIDTH);
        }
        public boolean verticalAdjacent(Room room1,Room room2){
            return ((room1.getX() < room2.getX() + room2.getwidth() && 
            room1.getX() + room1.getwidth() > room2.getX())|| (room1.getX() < room2.getX() + room2.getwidth() && 
            room1.getX() + room1.getwidth() > room2.getX())) &&
           (Math.abs(room1.getY() + room1.getheight() - room2.getY()) <= WALL_WIDTH);
        }
    

// Helper methods to check adjacency
private void drawWall(Graphics g, Room room1, Room room2) {
    if (room1 == null || room2 == null || WALL_WIDTH <= 0) {
        return;
    }

    // Horizontal Adjacency
    if (horizontalAdjacent(room1,room2)) {
        
        int wallXCoord = (room1.getX() > room2.getX()) 
                ? (room2.getX() + room2.getwidth()) 
                : (room1.getX() + room1.getwidth());
        int wallYCoord = Math.max(room1.getY(), room2.getY());
        int wallHeight = Math.min(room1.getY() + room1.getheight(), room2.getY() + room2.getheight()) - wallYCoord;

        g.setColor(Color.BLACK);
        g.fillRect(wallXCoord, wallYCoord, Math.abs(room1.getX() + room1.getwidth() - room2.getX()), wallHeight);
        if (WALL_WIDTH>0) {
            Wall wall = new Wall(wallXCoord,wallYCoord,Math.abs(room1.getX() + room1.getwidth() - room2.getX()),wallHeight);
            if(!(walls.contains(wall))) {
       walls.add(wall); 
        }
    }
}

    // Vertical Adjacency
    if (verticalAdjacent(room1,room2)) {
         int wallXCoord = Math.max(room1.getX(), room2.getX());
        int wallYCoord = (room1.getY() > room2.getY()) 
                ? (room2.getY() + room2.getheight()) 
                : (room1.getY() + room1.getheight());
        int wallWidth = Math.min(room1.getX() + room1.getwidth(), room2.getX() + room2.getwidth()) - wallXCoord;
        g.setColor(Color.BLACK);
        g.fillRect(wallXCoord, wallYCoord, wallWidth, Math.abs(room1.getY() + room1.getheight() - room2.getY()));
        if (WALL_WIDTH>0) {
            Wall wall = new Wall(wallXCoord,wallYCoord,wallWidth,Math.abs(room1.getY() + room1.getheight() - room2.getY()));
            if(!(walls.contains(wall))) {
       walls.add(wall); 
        }
    }
    }
}
public List<Room> getlist(){
    return rooms;
}
public List<Wall> getitems(){
    return walls;
}

public void redrawwall(Graphics g,Wall wall){
    Graphics2D g2d=(Graphics2D)g;
    g2d.setColor(Color.BLACK);
    g2d.fillRect(wall.getX(),wall.getY(),wall.getwidth(),wall.getheight());
    }
    // Function to calculate the wall width between two rooms (horizontal)
    

    public int rowNum=0;// Function to add a room to the canvas
    public void addRoom(String type, int width, int height) {
        int x=roomXCoord(width);
        int y=roomYCoord();
        if((x+width>canvasWidth)||(y+height>canvasHeight)){
                JOptionPane.showMessageDialog(this,"height or width field entered are too large,reverting back without adding room","WARNING",JOptionPane.ERROR_MESSAGE);
                if(x+width>canvasWidth){
                    rowNum--;
                }
                return;
        }
         Room newRoom = new Room(type, x, y, width,height,rowNum);
         boolean overlapCheck=true;
       if(!(rooms.isEmpty())) { // Check for overlap with existing rooms
        for (Room room : rooms) {
            if (newRoom.getBounds().intersects(room.getBounds())) {
                overlapCheck=false;
                JOptionPane.showMessageDialog(this, "Cannot place room here,Overlap detected","OverlapError",JOptionPane.ERROR_MESSAGE);
                return; // Don't add the room if there's an overlap
            }
        }
    }
        if(overlapCheck){
        rooms.add(newRoom);
        repaint();  // Repaint the canvas after adding a room
        }
    }
   
    public int roomXCoord(int Width){
        final int x=5;
        Room temproom = rooms.isEmpty() ? null : rooms.get((rooms.size()) - 1);
        if(rooms.isEmpty()){
            return x;
        }
        else{
            int newx=(temproom.getwidth()+temproom.getX());
            if((Width+newx+x)>canvasWidth){
                rowNum++; 
                return x;
            }
            else {
                return (x+newx);  
            }
            
        }    
    }
    public int roomYCoord(){
        final int y=5;
        if((rooms.isEmpty())){
            return y;
        }
        
        if(rowNum>0){
            int accumHeightInPreviousRow=0;
            for(int i=rowNum-1;i>=0;i--){
                int currentRow=i;
                accumHeightInPreviousRow+= rooms.stream()
                .filter(room -> room.getRowNum()== currentRow)
                .mapToInt(Room::getheight)
                .max()
                .orElse(0);
            }
            return (accumHeightInPreviousRow+((rowNum+1)*y));
            }
            return y;
        }

    public boolean Removewall(Wall wall,Rectangle Rect){
       int width=Rect.width;
       int height=Rect.height;
        return ((((wall.getX()+wall.getwidth()==initialX)||(wall.getX()==initialX+width))&&(wall.getY()==initialY))||((wall.getY()+wall.getheight()==initialY)||((wall.getY()==initialY+height)&&(wall.getX()==initialX))));   
    }

    public void clearCanvas() {
        for (Room room : rooms){
            room.changestate(false);
        }
        if(!rooms.isEmpty()){
        rooms.clear();
        }
        if(!walls.isEmpty()){
            walls.clear();
        }
        rowNum=0;
        initoffsetX=0;
        initoffsetY=0;
        initialX=0;
        initialY=0;
        if(prevRect!=null){
            prevRect=null;
        }
        if(selectedRoom!=null){
            selectedRoom=null;
        }
        repaint();
        } // Repaint the canvas to reflect the changes
    // Save the floor plan to a file
    public void savePlan(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(rooms);
            out.writeObject(walls);
            JOptionPane.showMessageDialog(this, "Plan saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save the plan.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    // Load the floor plan from a file
    public void loadPlan(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
             List<Room> loadedRooms = (List<Room>) in.readObject();
            List<Wall> loadedWalls = (List<Wall>) in.readObject();

            
             rooms = new ArrayList<Room>((loadedRooms != null )? loadedRooms : Collections.<Room>emptyList());
        walls = new ArrayList<Wall>((loadedWalls != null) ? loadedWalls : Collections.<Wall>emptyList());
            repaint();
             JOptionPane.showMessageDialog(this, "saved plan loaded succesfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Failed to load the plan.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

