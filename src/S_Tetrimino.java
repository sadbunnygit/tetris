import java.awt.Graphics;
import java.awt.*;

/**
 * Tetriminoes: little guys that fall, made up of 4 tiles
 *
 * this is the green S shaped num 4 subclass of Tetrimnino
 *
 * @author (Danelle)
 * @version (Start: Nov 14 2023, Last: Jan 17 2023)
 */
public class S_Tetrimino extends Tetrimino
{
    public S_Tetrimino(int x, int y, boolean play)
    {
        super(x, y, play);
        colour = Color.GREEN;
        type = Tetrimino.S;
        createSquares();
    }

    @Override
    protected void createSquares() // S shape
    {
        super.createSquares();
        squares[2] = new Tile(getX()+Tile.SIZE, getY()-Tile.SIZE, colour);
        squares[3] = new Tile(getX()+(Tile.SIZE*2), getY()-Tile.SIZE, colour);
        //move all down 2 for rotational stuff to be better
        for(Tile i : squares)
            i.setY(i.getY()+(2*Tile.SIZE));
    }
}

