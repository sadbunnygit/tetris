import java.awt.Graphics;
import java.awt.*;

/**
 * Tetriminoes: little guys that fall, made up of 4 tiles
 *
 *  this is the blue J shaped num 2 subclass of Tetrimnino
 *
 * @author (Danelle)
 * @version (Start: Nov 14 2023, Last: Jan 17 2023)
 */
public class J_Tetrimino extends Tetrimino
{
    public J_Tetrimino(int x, int y, boolean play)
    {
        super(x, y, play);
        colour = Color.BLUE;
        type = Tetrimino.J;
        createSquares();
    }

    @Override
    protected void createSquares() // J shape
    {
        super.createSquares();
        squares[3] = new Tile(getX(), getY()-Tile.SIZE, colour);
        //move all down 1 for rotational stuff to be better
        for(Tile i : squares)
            i.setY(i.getY()+Tile.SIZE);
    }
}

