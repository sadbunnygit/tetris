import java.awt.Graphics;
import java.awt.*;

/**
 * Tetriminoes: little guys that fall, made up of 4 tiles
 *
 * this is the yellw O shaped num 1 subclass of Tetrimnino
 *
 * @author (Danelle)
 * @version (Start: Nov 14 2023, Last: Jan 17 2023)
 */
public class O_Tetrimino extends Tetrimino
{
    public O_Tetrimino(int x, int y, boolean play)
    {
        super(x, y, play);
        colour = Color.YELLOW;
        type = Tetrimino.O;
        createSquares();
    }
    
    @Override
    protected void createSquares() // O shape
    {
        super.createSquares();
        squares[2] = new Tile(getX(), getY() + Tile.SIZE, colour);
        squares[3] = new Tile(getX() + Tile.SIZE, getY() + Tile.SIZE, colour);
    }
    @Override
    protected void rotate(Tile[] toRot){}//no rotate this one
}

