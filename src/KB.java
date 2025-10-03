import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * KeyboardButtons: Handles the keyboard button presses
 * changeed to KB so eaiser to type
 * you pressed a key (so held down)
 * you can click it
 * and you can toggle it (switches on and off every click)
 *
 * @author (Danelle)
 * @version (oct 25 2023, last: Jan 20th 2024)
 */
public class KB implements KeyListener
{
    //const var of which index each key in the array is (not the same as the keyevent ones)
    public final static int SPACE = 0;
    public final static int C = 1; 
    public final static int UP = 2;
    public final static int DOWN = 3;
    public final static int LEFT = 4;
    public final static int RIGHT = 5;
    public final static int ESC = 6;
    public final static int NUMOFKEYS = 7; //make it the last num
    //array of all the keys
    public static boolean[] pressed = new boolean[NUMOFKEYS]; //held
    public static boolean[] clicked = new boolean[NUMOFKEYS]; //clicked, not held, false when held
    public static boolean[] toggled = new boolean[NUMOFKEYS];
    
    public KB()
    {
        //set all to false
        for (int i = 0; i<NUMOFKEYS; i++)
        {
            pressed[i] = false;
            clicked[i] = false;
            toggled[i] = false;
        }
    }
    
    public void keyTyped(KeyEvent e) {/*#UN USED*/}
    
    /*when a button is pressed*/
    public void keyPressed(KeyEvent e) 
    {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_SPACE) 
            pressed[SPACE] = true;
        
        if (key == KeyEvent.VK_C) 
            pressed[C] = true;
            
        if (key == KeyEvent.VK_LEFT) 
            pressed[LEFT] = true;
        if (key == KeyEvent.VK_RIGHT)
            pressed[RIGHT] = true;
        if (key == KeyEvent.VK_UP)
            pressed[UP] = true;
        if (key == KeyEvent.VK_DOWN) 
            pressed[DOWN] = true;
            
        if (key == KeyEvent.VK_ESCAPE) 
            pressed[ESC] = true; 
            
        resetAllClicks(); //all false when being held
    }

    // when a button is unpressed
    public void keyReleased(KeyEvent e) 
    {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_SPACE)
        {
            pressed[SPACE] = false;
            clicked[SPACE] = true;
            toggle(SPACE);
        }
    
        if (key == KeyEvent.VK_C) 
        {
            pressed[C] = false;
            clicked[C] = true;
            toggle(C);
        }
    
        if (key == KeyEvent.VK_LEFT) 
        {
            pressed[LEFT] = false;
            clicked[LEFT] = true;
            toggle(LEFT);
        }
    
        if (key == KeyEvent.VK_RIGHT)
        {
            pressed[RIGHT] = false;
            clicked[RIGHT] = true;
            toggle(RIGHT);
        }
    
        if (key == KeyEvent.VK_UP)
        {
           pressed[UP] = false;
           clicked[UP] = true;
           toggle(UP);
        }
    
        if (key == KeyEvent.VK_DOWN) 
        {
            pressed[DOWN] = false;
            clicked[DOWN] = true;
            toggle(DOWN);
        }
        
        if (key == KeyEvent.VK_ESCAPE) 
        {
            pressed[ESC] = false;
            clicked[ESC] = true;
            toggle(ESC);
        }
    }
    
    public static void toggle(int key)
    {
        toggled[key] = !toggled[key];
    }
    public static void resetClick(int key)
    {
        clicked[key] = false;
    }
    public static void resetAllClicks()
    {
        for (int i = 0; i<NUMOFKEYS; i++) //all false when being held
            clicked[i] = false;
    }
}


