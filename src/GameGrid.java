import javax.swing.JPanel;
import java.awt.Dimension; 
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform; 
import java.awt.Color;
import java.awt.*;
import java.util.Random; 
import java.awt.Shape;
import java.awt.GradientPaint;
import java.awt.Font;
import java.util.ArrayList;
import java.util.*;
import javax.swing.*;

/**
 * GameGrid: Visual Part of the game. draws game stuff 
 * has the tetriminos and the leftover tiles
 * Where the gameplay happens
 * has all the game logic, how turns work and losing, scores and stuff
 * has time stuff, keyboard lisenter is added
 * 
 * @author (Danelle)
 * @version (Started: Oct 23 2023, Last: Jan 20 2023)
 */
public class GameGrid extends JPanel implements Runnable //runable for time stuff (thread) 
{                                                  
    /*# Screen settings */
    public static final int HEIGHT = 480; // classic resolution - do not change!
    public static final int WIDTH = 640; // do not change !! 
    //borders: x and y position, W = width, H = height
    //full game
    public final static int GAMEX = Tile.SIZE;
    public final static int GAMEY = 0;
    
    //public final static int GAMEW = ;
    //public final static int GAMEH = ;
    //play area
    public final static int PLAYX = GAMEX+(Tile.SIZE*8);
    public final static int PLAYY = GAMEY+Tile.SIZE;
    public final static int PLAYW = Tile.SIZE*10;
    public final static int PLAYH = Tile.SIZE*20;
    public final static Color PLAYCOLOUR = new Color(150,50,205);// dark purple

    //start
    public final static int STARTX = PLAYX+(PLAYW/2);
    public final static int STARTY = PLAYY-(2*Tile.SIZE);
    
    //Holding spot
    public final static int HOLDX = GAMEX+Tile.SIZE;
    public final static int HOLDY = GAMEY+Tile.SIZE;
    public final static int HOLDW = Tile.SIZE*6; 
    public final static int HOLDH = Tile.SIZE*6;
    //scores/stats
    public final static int SCOREX = HOLDX;
    public final static int SCOREY = HOLDY+Tile.SIZE+HOLDH;
    public final static int SCOREW = Tile.SIZE*6;
    public final static int SCOREH = Tile.SIZE*12;
    //level
    public final static int LEVELX = PLAYX+PLAYW+Tile.SIZE;
    public final static int LEVELY = PLAYY;
    public final static int LEVELW = Tile.SIZE*6;
    public final static int LEVELH = Tile.SIZE*3;
    //next
    public final static int NEXTX = LEVELX;
    public final static int NEXTY = LEVELY+LEVELH+Tile.SIZE;
    public final static int NEXTW = Tile.SIZE*6;
    public final static int NEXTH = Tile.SIZE*6;
    //controls
    public final static int CONTROLSX = LEVELX;
    public final static int CONTROLSY = NEXTY+NEXTH+Tile.SIZE;
    public final static int CONTROLSW = Tile.SIZE*6;
    public final static int CONTROLSH = Tile.SIZE*8;
    
    
    /*# time stuff (nanoTime) */
    Thread gameThread;
    long checkTime = 0;
    long frameTime, fallTime, displayTime,  startTime, pauseStart = System.nanoTime();
    long secsPassed; //(checkTime - startTime)/ONESEC;
    long timePaused;
    //final vars for time keeping
    public final static int FPS = 10;
    public final static int ONESEC = 1000000000;
    public final static int GAMEUPDATEFRQNCY = ONESEC/FPS; 
   
    /*# keyboard input */
    static KB keyB = new KB();
    
    /*# random stuff */
    Random ran = new Random();

    /*# game stuff */
    static private Tetrimino current; //piece in play
    static private Tetrimino next; //the next piece in play
    static private Tetrimino held; //piece not in play being held
    static private boolean dontHold = false; //for if held already that turn
    
    static public ArrayList<Tile> pieces = new ArrayList<Tile>(); //the tiles that will be saved onto the game board
    
    //scoring and level
    static private int linesRemoved = 0; // the lines the player has cleared
    static private int score = 0; //players score based on them removing lines and dropping tetrminos
    static private int level = 1; //their level and speed due to it
    static private int previousAction = 0; //what their previous non 0 line clear was
    static private int combo = 0; //count of line clears in a row
    static private String addScoreDisplay = ""; // to display the addition score based on line cleared
    static private int highscore; //highest score player has made
    static private int bestTime; //highest time player has made
    static private int mostLines; //highest amount lines player has cleard
    
    public GameGrid() // default constructor
    {
        //grid = new ArrayList<ArrayList<Tile>>();
        //createGrid(WIDTH,HEIGHT, Tile.SIZE);
        this.setPreferredSize( new Dimension(WIDTH, HEIGHT) );
        this.setDoubleBuffered(true);
        //to recieve key input
        this.addKeyListener(keyB);
        this.setFocusable(true);
        reset();
        //run();        
    }
    
    /*public void createGrid(int width, int height, int size)
    {
        for (int x= 0; x<width; x+=size)
        {
            grid.add(new ArrayList<Tile>());
            for (int y = 0; y<height; y+=size)
                grid.get(x/size).add(new Tile(x,y));
        }
    }
    */
    //start clock
    public void startGameThread() //runs the game or something
    {
        gameThread = new Thread(this); 
        gameThread.start();
    }
    
    // over-ride runnable run method
    @Override
    public void run() //gmae game game time!
    {
        reset();
        repaint();
        secsPassed = 0;

        //game loop
        while(gameThread != null)
        {
            // a cooldown to updating info and drawing, the FPS
            checkTime = 0; timePaused = 0;
            frameTime = System.nanoTime();
            fallTime = System.nanoTime();
            displayTime = System.nanoTime();
            /*
             * xTime: x is frame or fall or display, these are the cooldown wahterves                                         
             * when the difference of checktime and -xTime is n, that means a n amount of time has passed
             * when n amount of time has passed do whatever is in the if or while
             */
            while (checkTime - frameTime < GAMEUPDATEFRQNCY && !checkForGameover()) 
            {
                checkTime = System.nanoTime(); //always updating
                repaint(); 
                if(KB.clicked[KB.ESC] && KB.toggled[KB.ESC]) //so when toggeld on
                {
                    pauseStart = System.nanoTime();
                    timePaused = 0;
                }
                if(!KB.toggled[KB.ESC]) //when not paused do the game stuff
                {
                    startTime += timePaused; //account for the time the game was paused for
                    secsPassed = (checkTime - startTime)/ONESEC; //to make it into seconds
                    timePaused = 0; //reset it
                    if (checkTime - frameTime >= GAMEUPDATEFRQNCY) //everyfrmae
                    {
                        if (checkTime - displayTime >= ONESEC*5) //every 5 secs
                        {
                            addScoreDisplay = ""; //so it flashes
                            displayTime = System.nanoTime(); //reset cooldown
                            //this part isn't working**
                        }    
                        if(current.isInPlay()) //for the piece thats falling
                        {
                            if(KB.pressed[KB.C] && !dontHold) //if they pressed the hold buton
                            {
                                holdPiece();
                                dontHold = true;//they can't hold anymore once they alredy have
                            }
                            current.move(); //move it
                            if (checkTime - fallTime >= ONESEC*Math.pow((0.8-((level-1)*0.007)),(level-1))) //fall speed cooldown
                            {
                                current.fall(); 
                                current.updatePlay(); //checks for bottom collision
                                fallTime = System.nanoTime(); //reset cooldown
                            }
                        }
                        //next piece if done
                        if(!current.isInPlay())
                        {
                            //save current pieces
                            for(Tile i : current.getSquares())
                                pieces.add(new Tile(i.getX(),i.getY(), current.getColour()));
                            //remove lines
                            checkLines();
                            current = next;
                            current.reset();
                            if(!Driver.DEVMODE)
                                next = Tetrimino.makeTetrimino(false, NEXTX+Tile.SIZE,NEXTY+(2*Tile.SIZE),ran.nextInt(7));
                            else    /*#DEVMODE*/ 
                                 next = pickTetrimino(false);
                            dontHold = false; //they can hold again
                        }
                        frameTime = System.nanoTime(); //reset cooldown
                        KB.resetAllClicks(); //reset clicks so can hardrop again
                        repaint(); //need to update screen yk
                    }
                }
                else //so when pause is toggled
                    timePaused = (System.nanoTime() - pauseStart); //the change in time when paused
                KB.resetClick(KB.ESC);//just pause key, not harddrop because will not be able to harddrop
            }
            repaint(); 
            KB.resetClick(KB.ESC);//just pause key, not harddrop because will not be able to harddrop
            if(checkForGameover())
                displayEndScreen(); //always check if the game is done
        }
    }
    public void startGame()
    {
        run(); //starts the game
    }
    //game logic
    private void checkLines()
    {
        //*# devmode */if(Driver.DEVMODE){System.out.println("cheicking!!"); }  
        //keep track of old lines clearing for scoring
        int newLinesCleared = 0;
        for (int l = 0; l<HEIGHT; l+=Tile.SIZE) //go through each y level
        {
            int count = 0;
            for(Tile i : pieces)//check if 10 in a horizantal line
            {   
                if (i.getY()==l)
                    count++;
            }
            if(count==10) //if there is ten in a line clear that line
            {
                removeLine(l);
                newLinesCleared++;
            }
        }
        if (newLinesCleared > 0) //when we cleared lines
        {   
            score += clearLineScoreCalc(newLinesCleared);   //get the score!!
            previousAction = newLinesCleared; //preivous line clear gets upadted
            combo++; //yay bigger combo
        }    
        else      
        {
            addScoreDisplay = "";//no lines cleared nothing to display
            combo = 0; //no lines cleared so reset comob
        }
        displayTime = System.nanoTime();
    }
    private void removeLine(int line)
    {
        /*# devmode */if(Driver.DEVMODE){System.out.println("line "+line+" removed"); }  
        for(int i = 0; i<pieces.size(); i++) //go through every piece
        {
            int check = pieces.get(i).getY(); 
            if (check==line) //if the y matchs the line then we delete that piece
            {
                pieces.remove(i);
                i--; //fix index because one is removed
                repaint();
            }
        }     
        //move the higher ones down
        for(int i = 0; i<pieces.size(); i++)
        {
            if (pieces.get(i).getY()<line)
                pieces.get(i).setY(pieces.get(i).getY()+Tile.SIZE);
        }
        linesRemoved++;
        if(linesRemoved==level*10) //if they removed a certain amount of lines they level up!!
            level++;
        repaint();
    }
    //scoring
    private int clearLineScoreCalc(int newLinesCleared)
    {
        addScoreDisplay = "";
        int addScore = 0;
        addScore = (int) Math.pow(2,newLinesCleared-1)*100*level; //math eqation!! 1=100,2=200,3=400,4=800. if there was more it would keep doubling
        //combo
        addScore += 50*level*combo; //more points for combos
        //back-to-back 4 lines tetris
        if(previousAction == newLinesCleared && previousAction == 4) 
        {
            addScore += 400*level;
            addScoreDisplay = "Back-to-back Tetris!  "; //tell them why tehy get mmore
        }
        if(pieces.isEmpty()) //when the board gets cleared
        {
            addScore *= 3; 
            addScoreDisplay = "All clear!  ";//tell them why tehy get mmore
        }
        addScoreDisplay += " + " +addScore; 
        return addScore;
    }
    public static void addScore(int toAdd)
    {
        score+=toAdd; //this is used for the soft and harddrops
    }
    //holding
    private void holdPiece()
    {
        if(held==null) //if nothing is currenlty held then we get a new next
        {
            held = current; 
            current = next;
            current.reset();
            if(!Driver.DEVMODE)
                next = Tetrimino.makeTetrimino(false, NEXTX+Tile.SIZE,NEXTY+(2+Tile.SIZE),ran.nextInt(7));
            else    /*#DEVMODE*/ 
                 next = pickTetrimino(false);
        }
        else // if something is being held we swap it
        {
            Tetrimino temp = held; 
            held = current;
            current = temp;
            current.reset();
        }
        held.setInPlay(false); ///cant move held piece nono
        held.setXY(HOLDX+Tile.SIZE,HOLDY+(2*Tile.SIZE)); //place in held box
    }
    //end game
    private boolean checkForGameover()
    {
        for(Tile i : pieces)
        {
            if(i.getY()==PLAYY)  //if a piece has reached the top of the board gameover!!!1
                return true;
        } 
        return false;
    }
    public void reset() //resets all to be 0 or new tetriminos
    {
        if(!Driver.DEVMODE)
        {
            current = Tetrimino.makeTetrimino(true, STARTX, STARTY, ran.nextInt(7));       
            next = Tetrimino.makeTetrimino(false, NEXTX+Tile.SIZE,NEXTY+(2+Tile.SIZE),ran.nextInt(7));
        }
        else    /*#DEVMODE*/ 
        {     
            current = pickTetrimino(true);
            next = pickTetrimino(false);
        }
        held = null;
        pieces.removeAll(pieces);
        linesRemoved = 0;
        score = 0;
        combo = 0;
        dontHold = false;
        level = 1;
        previousAction = 0;
        addScoreDisplay = "";
        secsPassed = 0;
        startTime = System.nanoTime();
    }
    private void updateBest() //updates the highest score and stuff
    {
        if (score > highscore)
            highscore = score;
        if (linesRemoved > mostLines)
            mostLines = linesRemoved;
        if ((int)secsPassed > bestTime)
            bestTime = (int)secsPassed;
    } 
    //--other panels and/or panes--
    private void displayEndScreen() //when the game ends tell them waht happended and their stats
    {
        //update best!
        updateBest();
        //the coloured and formated text to appear
        String message = "<html>" + "<h1 style='color: #6610F2;'>" + "Game over!"+"</h1>"; 
        message +=  "<p>Score: "+score+"</p>" ;
        message +=  "<p>Lines Cleared: "+linesRemoved+"</p>" ;
        message +=  "<p>Final level: "+level+"</p>" ;
        message +=  "<p>Time played: "+secsPassed+"s </p>" ;
        
        message +=  "<p><br>Highscore: "+highscore+"</p>" ;
        message +=  "<p>Best Time: "+bestTime+"s </p>" ;
        message +=  "<p>Most lines Cleared: "+mostLines+" </p>" ;
        
        //give them optiosn
        message +=  "<p><br>Do you want to play again?</p>" + "</html>";
        if(JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(this, message, "End Game", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,null,null))
        {    
            reset();
            //dont have to do time stuff because it still in loop
        }
        else//quit
        {
            this.setVisible(false);
            System.exit(0); //Quit the app
        }
    }
    //-----drawings stuff-----
    // over-ride our parent's paint method
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); // allow our parent's method to run
        //*# devmode */if(Driver.DEVMODE){System.out.println("running"); }  
        
        //game area!!!!
        g.setColor(PLAYCOLOUR);//purple
        g.fillRect(PLAYX,PLAYY,PLAYW,PLAYH);
        
        current.draw(g); //draw the current piece in play

        g.setColor(Color.BLACK);
        drawGrid(g, WIDTH,HEIGHT,Tile.SIZE); //grid
        
        drawStats(g); //score, time, lines 
        drawControls(g); //how to move
        
        drawOtherPieces(g); //held and next
        
        drawTiles(g, pieces); //the pieces that landed already
        
        //paused game
        if (KB.toggled[KB.ESC]) 
            drawStringInBox(g,PLAYX+(3*Tile.SIZE)+Tile.SIZE/2,200,"PAUSED",Color.BLACK,Color.WHITE);
    }
    private void drawTiles(Graphics g,  ArrayList<Tile> tiles)//the pieces that landed already
    {
        for(int i = 0; i<tiles.size(); i++)
        {
            tiles.get(i).draw(g);
            g.setColor(Color.WHITE);
            /*# devmode */if(Driver.DEVMODE){g.drawString(""+i,tiles.get(i).getX(),tiles.get(i).getY()+Tile.SIZE); } 
        }
    }
    public void drawGrid(Graphics g,int width, int height, int size)//a grid
    {
        for (int x= 0; x<width; x+=size)
        {
            for (int y = 0; y<height; y+=size)
            {
                g.drawRect(x,y,size,size);
                /*# devmode -  show the y of each line*/
                if(Driver.DEVMODE)
                {
                    if(x==176)
                        g.drawString(""+y,x,y+Tile.SIZE);
                }
            }
        }
    }
    private void drawStats(Graphics g) //the stats: lines cleared, score, combo, time, highscore, highesttime, and level(seperate spot)
    {
        g.setColor(Color.CYAN);
        g.fillRect(SCOREX,SCOREY,SCOREW,SCOREH);
        g.setColor(Color.BLACK);
        g.drawString("Lines Cleared: "+linesRemoved, SCOREX+5, SCOREY+20);
        g.drawString("Score: "+score, SCOREX+5, SCOREY+60);
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(Color.CYAN);
        g.fillRect(SCOREX+15,SCOREY+80-metrics.getHeight(),metrics.stringWidth(addScoreDisplay),metrics.getHeight());
        g.setColor(Color.BLACK);
        g.drawString(addScoreDisplay, SCOREX+15, SCOREY+80);
        g.drawString("Combo: "+combo, SCOREX+5, SCOREY+100);
        g.drawString("Time: "+secsPassed+"s", SCOREX+5, SCOREY+140);
        g.drawString("Highscore : "+highscore, SCOREX+5, SCOREY+180);
        g.drawString("Most Lines: "+mostLines, SCOREX+5, SCOREY+220);
        g.drawString("Best Time: "+bestTime+"s", SCOREX+5, SCOREY+260);
        
        g.setColor(Color.CYAN);
        g.fillRect(LEVELX,LEVELY,LEVELW,LEVELH);
        g.setColor(Color.BLACK);
        g.drawString("Level "+level, LEVELX+5, LEVELY+20);
    }
    //for the above and below methods, the x and y are based on the box they go in. 
    private void drawControls(Graphics g) //how to move the piece
    {
        g.setColor(Color.CYAN);
        g.fillRect(CONTROLSX,CONTROLSY,CONTROLSW,CONTROLSH);
        g.setColor(Color.BLACK);
        g.drawString("Controls:", CONTROLSX+5, CONTROLSY+20);
        g.drawString("←→ - Move "+linesRemoved, CONTROLSX+5, CONTROLSY+60);
        g.drawString("↑ - Rotate", CONTROLSX+5, CONTROLSY+80);
        g.drawString("↓ - Softdrop", CONTROLSX+5,CONTROLSY+100);
        g.drawString("Space - Harddrop", CONTROLSX+5, CONTROLSY+120);
        g.drawString("C - Hold", CONTROLSX+5, CONTROLSY+140);
        g.drawString("Esc - Pause", CONTROLSX+5, CONTROLSY+160);
    }
    private void drawOtherPieces(Graphics g) //next and held piece
    {
        g.setColor(PLAYCOLOUR);//purple
        g.fillRect(NEXTX, NEXTY,NEXTW, NEXTH);
        g.setColor(Color.BLACK);
        g.drawString("Next: ",NEXTX+5,NEXTY+Tile.SIZE);
        next.draw(g);
        g.setColor(PLAYCOLOUR);//purple
        g.fillRect(HOLDX, HOLDY,HOLDW, HOLDH);
        g.setColor(Color.BLACK);
        g.drawString("Hold: ",HOLDX+5,HOLDY+Tile.SIZE);
        if(held!=null)
            held.draw(g);
    }
    private void drawStringInBox(Graphics g, int x, int y, String string, Color boxC, Color stringC) //wanna draw a string with a box around it?
    {
        //word centered in the box
        FontMetrics fm = g.getFontMetrics();
        g.setColor(boxC);
        g.fillRect(x,y,fm.stringWidth(string)+20,fm.getHeight()+20); //the box!
        g.setColor(stringC);
        g.drawString("PAUSED",x+10, y+8+fm.getHeight()); //string drawings starts form the bottom so we add its hiehgt
        //the +20 and +10 is for there to be kinda padding
    }
    
    //dev mode stuff
    private Tetrimino pickTetrimino(Boolean current) //lets teh dev pick a tetrmino
    {
        int index; // what the num of the tetrimino is, i know what they are an i am dev
        JSpinner sadbunnywashere = new JSpinner(new SpinnerNumberModel(0, 0, 6, 1));
        int option = JOptionPane.showOptionDialog(null, sadbunnywashere, "Choose "+((current)?"current":"next"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    
        if (option == JOptionPane.OK_OPTION) 
            index = (int) sadbunnywashere.getValue();
         else 
            index = ran.nextInt(7); //dont pick is random 
        
        if (current)
            return Tetrimino.makeTetrimino(current,STARTX,STARTY, index);
        else
            return Tetrimino.makeTetrimino(current,NEXTX+Tile.SIZE,NEXTY+(2*Tile.SIZE), index);
    }
}
//sadbunny was here
