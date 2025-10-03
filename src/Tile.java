import java.awt.Graphics;
import java.awt.*;

/**
 * Tiles: little guys that make up the little guys that fall
 * Tiles are squares, 
 * they move (with the thing that they made of, can be by themselves but not here)
 * make up the entire game
 * the whole game is based on their size
 * these make up the tetriminos
 *
 * @author (Danelle)
 * @version (Start: Dec 30 2023, Last: Dec 30 2023)
 */
public class Tile
{
    //variavles
    private int x, y; //location
    public final static int SIZE = 22; //just by changing this it changes the entire board sixing isnt that great
    private Color colour = Color.GRAY;
    
    //constructer
    public Tile(int x, int y, Color colour)
    {
        this.x = x;
        this.y = y;
        this.colour = colour;
    }
    //getter and setter
    public int getX() {return x;}
    public int getY() {return y;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public void setXY(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }
    public void setColour(Color colour) {this.colour = colour;}
    //for things that use those typa methods
    public int getWidth() {return SIZE;}
    public int getHeight() {return SIZE;}

    //override the sprite movement method
    /*#NOT USED IN THIS GAME**/
    public void move()
    {
        x -= (KB.pressed[KB.LEFT])?SIZE:0; //left is -
        x += (KB.pressed[KB.RIGHT])?SIZE:0; //right is +
    }
    public void fall()
    {           
        y+=SIZE;
    }
    
    
    /*#this is used tho! */
    public void draw(Graphics g)
    {
        g.setColor(colour);
        g.fillRect(x,y,SIZE,SIZE);
    }
}
