import java.awt.Graphics;
import java.awt.*;
import java.util.*;

/**
 * Tetriminoes: little guys that fall, made up of 4 tiles connection orthiaganlly
 * my super epic rad tetrimino
 * can move side to side with arrow keys
 * falls down because gravity
 * you can make it fall down faster
 * can spin 
 * different subclass are just different shapes and colours 
 *  the 7 types
 * collides with other tiles and the walls
 * can be in play and movable or not, so if not then ignore most of above
 *
 * @author (Danelle)
 * @version (Start: Nov 14 2023, Last: Jan 19 2023)
 */
public class Tetrimino
{
    //
    static Random ran = new Random();
    //tetrimno constants
    public static final int I = 0;
    public static final int O = 1;
    public static final int J = 2;
    public static final int L = 3;
    public static final int S = 4;
    public static final int Z = 5;
    public static final int T = 6;
    
    //if someone wanna make one
    public static Tetrimino makeTetrimino(boolean play, int x, int y, int type)
    {
        Tetrimino piece;
        if (type == Tetrimino.I) 
            piece = new I_Tetrimino(x,y, play);
        else if (type == Tetrimino.O) 
            piece = new O_Tetrimino(x,y, play);
        else if (type == Tetrimino.J) 
            piece = new J_Tetrimino(x,y, play);
        else if (type == Tetrimino.L  ) 
            piece = new L_Tetrimino(x,y, play);
        else if (type == Tetrimino.S) 
            piece = new S_Tetrimino(x,y, play);
        else if (type == Tetrimino.Z) 
            piece = new Z_Tetrimino(x,y, play);
        else 
            piece = new T_Tetrimino(x,y, play);
        return piece;
    }
    
    protected Color colour = Color.GRAY;
    //stuff unqiue to each tetrimino
    protected boolean leftCollision = false;
    protected boolean rightCollision = false;
    protected boolean bottomCollision = false;
    protected int x, y; //location
    protected boolean inPlay = true;
    //the tiles that make up our tetrimino
    protected int type;
    protected Tile[] squares;
    //only if in play, shows where it would go if harddroped
    protected Tetrimino shadow;
    
    public Tetrimino(int x, int y, boolean inPlay)
    {
        this.x = x;
        this.y = y;
        this.inPlay = inPlay;
        squares = new Tile[4];
        createSquares();
    }
    //makes the squares
    protected void createSquares() //THIS PART CHANGES IN THE SUBCLASSES THIS ONE IS THE DEFAULT LINE OF THEM
    {
        if (inPlay)
            createShadow();
        squares[0] = new Tile(x,y, colour);
        squares[1] = new Tile(x+Tile.SIZE,y, colour);
        squares[2] = new Tile(x+(Tile.SIZE*2),y, colour);
        squares[3] = new Tile(x+(Tile.SIZE*3),y, colour);
    }
    //makes the shadow, same thing but darker
    protected void createShadow()
    {
        shadow = makeTetrimino(false, x,y,type);
        shadow.setColour(colour.darker());
    }    
    //getter and setter
    public int getX() {return x;}
    public int getY() {return y;}
    public void setXY(int x, int y) 
    {
        //make each square match
        int dx = x - this.x;
        int dy = y - this.y;
        for (Tile i : squares)
        {
            i.setX(i.getX()+dx);
            i.setY(i.getY()+dy);
        }
        this.x = x;
        this.y = y;
    }
    public Tile[] getSquares() {return squares;}
    public Color getColour() {return colour;}
    public void setColour(Color colour) 
    {
        this.colour = colour;
        for(Tile i : squares)
            i.setColour(colour);
    }
    public boolean isInPlay() {return inPlay;}
    public void setInPlay(boolean inPlay) 
    {
        this.inPlay = inPlay;
        //no shawdow when not in play and one when in play
        if(!inPlay)
            shadow=null;
        if(inPlay)
            createShadow();
    }
    
    //---~~~~ MOVENEMNT ~~~~_----------
    //---moveing---
    public void move()
    {
        checkForCollision();
        if(KB.pressed[KB.LEFT]&&!leftCollision) //left is -
        {
            x-=Tile.SIZE;
            for(Tile i : squares)
                i.setX(i.getX()-Tile.SIZE);
        }
        if(KB.pressed[KB.RIGHT]&&!rightCollision) //right is +
        {
            x+=Tile.SIZE;
            for(Tile i : squares)
                i.setX(i.getX()+Tile.SIZE);
        }
        //soft drop
        if(KB.pressed[KB.DOWN]&&!bottomCollision) //down is +
        {
            y+=Tile.SIZE;
            for(Tile i : squares)
                i.setY(i.getY()+Tile.SIZE);
            GameGrid.addScore(1); //+1 to score each tile softdroped
        }
        //rotate
        if(KB.pressed[KB.UP] && canRotate())
        {
            rotate(squares);
            //shadow
            shadow.setXY(x, y);//match up
            shadow.checkForCollision();
            shadow.rotate(shadow.squares);
        }
        //shadow
        if(inPlay)
        {
            shadow.setXY(x, y);//match x
            shadow.checkForCollision();
            shadow.hardDrop(shadow.squares);
        }
        ///hard drop
        if(KB.clicked[KB.SPACE]&&!bottomCollision) //down is +
        {
            GameGrid.addScore(2*(shadow.y-this.y)/Tile.SIZE); //+2 to score for every tile hard droped
            hardDrop(squares);
        }
    }
    //---dropping---
    private void hardDrop(Tile[] toDrop)
    {
        while(!bottomCollision) //change y untill it reaches the bottom
        {
            y+=Tile.SIZE;
            for(Tile i : toDrop)
                i.setY(i.getY()+Tile.SIZE);
            checkForCollision();    
        }
        setInPlay(false);
    }
    public void fall() //fall cause gravity
    {
        checkForCollision();
        if(!bottomCollision)
        {
            y+=Tile.SIZE;
            for(Tile i : squares)
                i.setY(i.getY()+Tile.SIZE);
        }
    }
    //---collision--
    private void checkForCollision() //colliding with the walls of the play area
    {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;
        blockCollision();
        //Left wall
        for(Tile i : squares)
        { 
            if (i.getX() == GameGrid.PLAYX)
                leftCollision = true;
        }
        //Right wall
        for(Tile i : squares)
        { 
            if (i.getX() + Tile.SIZE == (GameGrid.PLAYX+GameGrid.PLAYW)) 
                rightCollision = true;
        }
        //Bottom
        for(Tile i : squares)
        { 
            if(i.getY() + Tile.SIZE >= (GameGrid.PLAYY+GameGrid.PLAYH)) 
                bottomCollision = true;
        }
    }   
    private void blockCollision() //colliding with the leftover tile piece
    {
        for (Tile p : GameGrid.pieces)
        {
            //bottom
            for (Tile i : squares)
            {   
                if(i.getY()+Tile.SIZE == p.getY()&&i.getX() == p.getX()) 
                    bottomCollision = true;
            }
            //left
            for (Tile i : squares)
            {   
                if(i.getX() == p.getX()+Tile.SIZE&&i.getY() == p.getY()) 
                    leftCollision = true;
            }
            //right
            for (Tile i : squares)
            {   
                if(i.getX()+Tile.SIZE == p.getX()&&i.getY() == p.getY()) 
                    rightCollision = true;
            }
        }
    }
    //---rotation--
    protected void rotate(Tile[] toRot) //changes the orientation of the tetrimino
    {
        for(Tile i : toRot)
        {  
            int ogX = i.getX()-x;
            int ogY = i.getY()-y;
            i.setXY(x+((2*Tile.SIZE)-ogY),y+ogX);
        }
    }
    protected boolean canRotate() //checks if nothing is in the way of it rotating.
    {
        //temporay tetrimino to check for us
        Tile[] temp = new Tile[squares.length];
        for (int i = 0; i < squares.length; i++) 
            temp[i] = new Tile(squares[i].getX(), squares[i].getY(), colour); 

        rotate(temp);
        //check if out of bounds
        //Left wall
        for(Tile i : temp)
        { 
            if (i.getX() < GameGrid.PLAYX)
                return false;
        }
        //Right wall
        for(Tile i : temp)
        { 
            if (i.getX() + Tile.SIZE > (GameGrid.PLAYX+GameGrid.PLAYW)) 
                return false;
        }
        //Bottom
        for(Tile i : temp)
        { 
            if(i.getY() + Tile.SIZE > (GameGrid.PLAYY+GameGrid.PLAYH)) 
                return false;
        }
        //check if inside other tetriminos
        for (Tile p : GameGrid.pieces)
        {
            for (Tile i : temp)
            {   
                if(i.getY() == p.getY()&&i.getX() == p.getX()) 
                    return false;
            }
        }
        return true;
    }
    
    //---end turn---
    public void updatePlay()
    {
        if(bottomCollision)
            setInPlay(false);
    }
    public void reset()
    {
        //reset back into play mode
        setInPlay(true);
        setXY(GameGrid.STARTX,GameGrid.STARTY);
        checkForCollision();
    }

    ///---graphics---
    public void draw(Graphics g)
    {
        if(inPlay) //where there is a shadow
        {
            g.setColor(shadow.colour);
            /*# devmode */if(Driver.DEVMODE){System.out.println(shadow.colour); }  
            shadow.draw(g);
        }
        g.setColor(colour);
        for(Tile i : squares)
            i.draw(g);
    }
}
