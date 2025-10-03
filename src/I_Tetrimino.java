import java.awt.Graphics;
import java.awt.*;

/**
 * Tetriminoes: little guys that fall, made up of 4 tiles
 * 
 * this is the cyan I shaped num 0 subclass of Tetrimnino
 *
 * @author (Danelle)
 * @version (Start: Nov 14 2023, Last: Jan 17 2023)
 */
public class I_Tetrimino extends Tetrimino
{
    //instead of this i am going to rotate it checking first its orientation, the rotate based on that that way when a shadow rotates or a a test rotate then it doesnt affect the directiono fhte piece
    public I_Tetrimino(int x, int y, boolean play)
    {
        super(x, y, play);
        colour = Color.CYAN;
        type = Tetrimino.I;
        createSquares();
        rotate(squares);
    }
    //movement
    @Override
    protected void rotate(Tile[] toRot) //this the  one with a switch more than a rotate
    { 
        /*# DEV MODE */if(Driver.DEVMODE){System.out.println("cyan rotated");}
        if (toRot[0].getY()==toRot[1].getY()&&toRot[1].getY()==toRot[2].getY()&&toRot[2].getY()==toRot[3].getY())//horizantal
        {   
            //set vertical
            toRot[0].setXY(x+Tile.SIZE,y-Tile.SIZE);
            //squares[1].setXY(DONTCHANGE); 
            toRot[2].setXY(x+Tile.SIZE,y+Tile.SIZE);
            toRot[3].setXY(x+Tile.SIZE,y+(Tile.SIZE*2));
        }
        else if (toRot[0].getX()==toRot[1].getX()&&toRot[1].getX()==toRot[2].getX()&&toRot[2].getX()==toRot[3].getX())//vertical
        {
            //set horizantal
            toRot[0].setXY(x,y);
            //squares[1].setXY(DONTCHANGE); 
            toRot[2].setXY(x+(Tile.SIZE*2),y);
            toRot[3].setXY(x+(Tile.SIZE*3),y);
        }
    }
}
