package poeMultitool;

import java.awt.Frame;
import java.io.InputStream;
import java.util.Date;
import javax.swing.JOptionPane;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * A class whose methods are called from a static context and perform various tasks across the entire suite.
 * @author Haris Koktsidis
 */
public class HelpfulFunctions 
{ 
    /**
     * Splits the string of the current Date and returns only the hour in a desired format.
     * @param date a Date variable containing the current time and date.
     * @return the current time, in format "HH:MM:SS".
     */
    public static String timeToString(Date date)
    {
        String strArray = date.toString();
        String [] str = strArray.split(" ");
        return "".concat(str[3]);
    }
    
    /**
     * A function used to load and play various notification sounds used in the application.
     * @param version an Integer indicating which sound to load and play
     */
    public static void playNotificationSound(int version) 
    {
        InputStream inputStream;
        AudioStream audioStream;
        switch (version)
        {
            // PM NOTIFIER - ERROR CODE 160
            case 0:
                try
                {
                    inputStream = new HelpfulFunctions().getClass().getResourceAsStream("/poeMultitool/pm.wav");
                    audioStream = new AudioStream(inputStream);
                    AudioPlayer.player.start(audioStream);
                }
                catch (Exception fail)
                {
                    displayPopupError(fail.getMessage(), 160);
                }
            break;
            // MAIL CHECK - ERROR CODE 161
            case 1:
                try
                {
                    inputStream = new HelpfulFunctions().getClass().getResourceAsStream("/poeMultitool/mail.wav");
                    audioStream = new AudioStream(inputStream);
                    AudioPlayer.player.start(audioStream);
                }
                catch (Exception fail)
                {
                     displayPopupError(fail.getMessage(), 161);
                }
            break;
        }
    }
    
    /**
     * Displays a pop up to the user containing the information of a critical error that has
     * occurred.
     * @param ex a String containing a description of the error.
     * @param code an Integer indicating the error code of the error occurred.
     */
    public static void displayPopupError(String ex, int code)
    {
        JOptionPane.showMessageDialog(
                new Frame(), 
                "A runtime error occurred.\n\n" + "ERROR: " + ex + "\n" + "ERROR CODE: " + code, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Converts the given seconds into "X min Y sec" format
     * @param countdown an Integer indicating the amount of seconds to be converted
     * @return a String that contains the countdown time in "X min Y sec" format.
     */
    public static String countdownToText(int countdown)
    {
        String str = new String();
        str = str.concat(String.valueOf(countdown/60)+" min ");
        str = str.concat(String.valueOf(countdown%60)+" sec");
        return str;
    }
}
