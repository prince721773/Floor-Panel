import java.io.Serializable;
import java.awt.Rectangle;
public class Wall implements Serializable{
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private int height;
    private int width;

    public Wall(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;              
        if (obj == null || getClass() != obj.getClass()) return false; 
        Wall wall = (Wall) obj;                   
        return (this.getX() == wall.getX() &&
               this.getY() == wall.getY() &&
               this.getheight() == wall.getheight() &&
               this.getwidth() == wall.getwidth());
    }
   

    public int getX() { return x; }
    public int getY() { return y; }
    public int getheight() { return height; }
    public int getwidth() { return width; }

    public Rectangle getBounds(){
        return new Rectangle(this.getX(),this.getY(),this.getwidth(),this.getheight());
    }
}
