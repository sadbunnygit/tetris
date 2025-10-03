import javax.swing.JPanel;
import javax.swing.JFrame; //outside, with title and exit button, frame of the window

/**
 * Driver: Creates Jframe and game thats it!
 *
 * @author (Danelle)
 * @version (Oct 2023, last: Dec 30 2023)
 */
public class Driver
{  
    final static public boolean DEVMODE = false; //toggled by me sadbunny, toggles stuff for debugging 

    public static void main(String[] args)
    {
        JFrame window = new JFrame();
        window.setTitle("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        GameGrid game = new GameGrid();
        game.startGameThread();
        window.getContentPane().add(game);
        window.pack();
        window.setVisible(true);
    }
}
//sadbunny was here
