package poeMultitool;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.awt.Frame;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Haris Koktsidis
 */
public class PoeAutoMessageRefresh 
{
    /**
     * Simulates a chrome browser and logs on to the Path Of Exile site, reads
     * and returns the number of new mails currently waiting in the mailbox.
     * There are 2 sites we need to check. When the user does have new mails the link href
     * changes to "/private-messages/inbox" otherwise it's the default "/private-messages".
     * @param guiFrame A frame to display a pop up error in case something goes wrong.
     * @param accountName a String containing the account name of the player.
     * @param password a String containing the password of the player.
     * @return -1 if an error has occurred or any other number >=0 indicating the number of new messages.
     */
    public int checkMessages(Frame guiFrame, String accountName, String password)
    {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); // Turning off warnings by htmlUnit
        WebClient webClient = new WebClient(BrowserVersion.CHROME); // Simulating a Chrome browser
        try 
        {
            // Grabbing the login site and the correct form...
            HtmlPage poeLogin = webClient.getPage("https://www.pathofexile.com/login");
            List<HtmlForm> logins = poeLogin.getForms();
            HtmlForm first = logins.get(0);
            // Grabbing the Account name/Password inputs and the submit button...
            final HtmlSubmitInput login = first.getInputByValue("Login");
            final HtmlTextInput usernameTextField = first.getInputByName("login_email");
            final HtmlPasswordInput passwordTextField = first.getInputByName("login_password");
            // Setting the textfields according to the given Accountname/password...
            usernameTextField.setValueAttribute(accountName);
            passwordTextField.setValueAttribute(password);
            // Submitting the form...
            HtmlPage loggedIn = login.click();
            // Switching to logged in site...
            loggedIn = webClient.getPage("http://www.pathofexile.com/my-account");
            try 
            { // Verifying succesful login by looking up for the messages link...
                HtmlAnchor anchorLink = loggedIn.getAnchorByHref("/private-messages/inbox");
                String substring[] = (anchorLink.getTextContent().split(" "));
                webClient.closeAllWindows();
                return Integer.parseInt(substring[0]);
            }
            catch(ElementNotFoundException exception)
            { // Or the specified link was not found, trying to verify with default link.
                try
                {
                    HtmlAnchor anchorLink = loggedIn.getAnchorByHref("/private-messages");
                    webClient.closeAllWindows();
                    return 0;
                }
                catch (ElementNotFoundException ex)
                { // Both links failed, we are not logged in, popup error message and return
                    HelpfulFunctions.displayPopupError(ex.getMessage(), 151);
                    webClient.closeAllWindows();
                    return -1;
                }
            }
        }
        catch (IOException | FailingHttpStatusCodeException failToOpenSite)
        {  // Error logging..
            HelpfulFunctions.displayPopupError(failToOpenSite.getMessage(), 152);
            webClient.closeAllWindows();
            return -1;
        }
      }
}
