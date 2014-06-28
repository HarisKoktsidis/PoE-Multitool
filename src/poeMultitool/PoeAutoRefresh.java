package poeMultitool;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * A class describing the poe.xyz.is control panel auto-online refresh.
 * @author Haris Koktidis
 */
public class PoeAutoRefresh
{
    /**
     * Simulates the poe.xyz.is control panel in a Chrome iconic window, clicks
     * the prolongate online status link.
     * It grabs the contents of the site and clicks on the link with href="#" which 
     * is the prolongate online status link.
     * @param url a string containing the URL of the poe.xyz.is control panel of the user.
     * @param guiFrame a frame to display pop up messages to the user in case of error.
     * @return -1 if an error has occurred or 0 at any other case.
     */
    public int pingServer(String url, Frame guiFrame)
    {
        // Simulating a Chrome browser..
        WebClient webClient = new WebClient(BrowserVersion.CHROME); 
        try
        {
            // Grabbing the control panel site that the user has provided..
            HtmlPage poeControlPanel = webClient.getPage(url);
            // Searching for the anchor link..
            HtmlAnchor anchorLink = poeControlPanel.getAnchorByHref("#");
            // Clicking it and refreshing online status duration..
            anchorLink.click();
            // Ending all connections..
            webClient.closeAllWindows();
            return 0;
        }
        // Connection to the server failed, writing error in a log file.
        catch (FailingHttpStatusCodeException | IOException failToOpenSite)
        {
            File logFile = new File(System.getProperty("user.dir")+"/log.txt");
            if (!logFile.exists()) // If the log file doesn't exist, create it.
            {
                try 
                {
                    logFile.createNewFile();
                } 
                catch (IOException ex) 
                {
                    // Unable to create log file, display popup to the user...
                    JOptionPane.showMessageDialog(guiFrame, "Failed to create a new log file. Reason: "+ex.getMessage() +"\n\n"+
                    "ERROR: "+failToOpenSite.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            FileWriter fw; BufferedWriter bw;
            try 
            {
                // Printing the error code to the log file with an associated date...
                fw = new FileWriter(logFile.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);Date currentDate = new Date();
                bw.write(currentDate.toString() +" --- " + failToOpenSite.getMessage());
                bw.newLine();
                bw.close();
            }
            finally
            {
                webClient.closeAllWindows();
                return -1;
            }
        }
    }
}