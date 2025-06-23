import java.io.Serializable;
import java.awt.Rectangle;
public class Room implements Serializable{
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private int height;
    private int width;
    private String type;
    private int RowNum;
    private boolean isselected;

    public Room(String type,int x,int y,int width,int height,int RowNum){
        this.type=type;
        this.x=x;
        this.y=y;
        this.height=height;
        this.width=width;
        this.RowNum=RowNum;
        this.isselected=false;
    }
    public Rectangle getBounds() {
        return new Rectangle(this.getX(), this.getY(), this.getwidth(), this.getheight());
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) {
        this.x=x;
    }
    public void setY(int y) {
        this.y=y;
    }
    public int getheight() { return height; }
    public int getwidth() { return width; }
    public int getRowNum() {
        return RowNum;
    }
    public String getType(){
        return type;
    }
    public boolean mousecontains(int mouseX,int mouseY) {
        return (mouseX >= this.getX() && mouseX <= this.getX() + this.getwidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getheight());
    }
   
    public void changestate(boolean state){
        this.isselected=state;
    }
    public boolean isselected(){
        return isselected;
    }
 }
