/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package poeMultitool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Haris Koktsidis
 */
public class DESCipher 
{
    // The password used to encrypt/decrypt the user data. Ommitted in the repository for obvious reasons.
    // For future developers replace it with a "your-password".toCharArray();
    // Minium size: 8 characters
    private static final char[] PASSWORD = "r10nwuEdokaQ".toCharArray();
    private static final byte[] SALT = 
    {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };
    private static File userData = new File(System.getProperty("user.dir")+"/userdata.txt");
    private static File encryptedUserData = new File(System.getProperty("user.dir")+"/user data.txt");
    private static BufferedReader reader;
    
    public static void createEncryptedFile()
    {
        if (!userData.exists())
        {
            try 
            {
                userData.createNewFile();
            }
            catch (IOException ex) 
            {
                HelpfulFunctions.displayPopupError(ex.getMessage(), 162);
            }
            try 
            {
                BufferedWriter out = new BufferedWriter(new FileWriter("userdata.txt", false));
                out.write("// THIS FILE CONTAINS SENSITIVE INFORMATION"); out.newLine();
                out.write("// Although encrypted it's highy advisable that nobody else gets access to the contents of this file."); out.newLine();
                out.write("// DO NOT MODIFY OR EDIT THIS FILE."); out.newLine();
                out.write("Username: "); out.newLine();
                out.write("Password: "); out.newLine();
                out.write("URL: "); out.newLine();
                out.write("DIR: ");
                out.close();
                System.gc();
            } 
            catch (IOException ex) 
            {
                HelpfulFunctions.displayPopupError(ex.getMessage(), 162);
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    public static boolean deleteUserData()
    {
         boolean delete = userData.delete();
         createEncryptedFile();
         return delete;
    }
    
    /**
     * 
     * @param username
     * @param password
     * @param url
     * @param dir 
     */
    public static void encrypt(String username, String password, String url, String dir) throws GeneralSecurityException
    {
        try 
        {
            username = encryptPassword (username);
            password = encryptPassword (password);
            url = encryptPassword (url);
            BufferedWriter out= new BufferedWriter(new FileWriter(userData, false));
            out.write("// THIS FILE CONTAINS SENSITIVE INFORMATION"); out.newLine();
            out.write("// Although encrypted it's highy advisable that nobody else gets access to the contents of this file."); out.newLine();
            out.write("// DO NOT MODIFY OR EDIT THIS FILE."); out.newLine();
            out.write("Username: " + username); out.newLine();
            out.write("Password: " + password); out.newLine();
            out.write("URL: " + url); out.newLine();
            out.write("DIR: " + dir);
            out.close();
        }
        catch (FileNotFoundException ex) 
        {
            HelpfulFunctions.displayPopupError(ex.getMessage(), 163);
        }
        catch (IOException ex)
        {
            HelpfulFunctions.displayPopupError(ex.getMessage(), 165);
        }
    }
    
    /**
     * 
     * @param identifier
     * @return 
     */
    public static String decryptIdentifier(String identifier) throws GeneralSecurityException
    {
        try 
        {
            reader = new BufferedReader(new FileReader("userdata.txt"));
        } 
        catch (FileNotFoundException ex) 
        {
            HelpfulFunctions.displayPopupError(ex.getMessage(), 166);
        }
        String tempIdentifier = null;
        try 
        {
            // While userdata has more lines AND tempIdentifer doesn't contain the required indentifier continue...
            while ( (tempIdentifier = reader.readLine())!=null && !(tempIdentifier.contains(identifier+": ")))
            {
                    continue;   
            }
            // username var now contains "Identifier: attribute", strip the attribute
            String temp[] = tempIdentifier.split(": ");
            // attribute is not NULL, return the attribute
            if (temp.length>1)
            {
                reader.close();
                System.gc();
                if (temp[0].contains("Password") | temp[0].contains("Username") | temp[0].contains("URL"))
                { // We found a decrtypted text, must decrypt it before using...
                    temp[1] = decryptPassword(temp[1]);
                }
                return temp[1];
            }
        }
        catch (IOException ex) 
        {
            HelpfulFunctions.displayPopupError(ex.getMessage(), 180);
        }
        try
        {
            reader.close();
            System.gc();
            return "";
        }
        catch (IOException ex)
        {
            HelpfulFunctions.displayPopupError(ex.getMessage(), 181);
            return "";
        }
    }
    
    /**
     * Used to encrypt a given user password before storing it to the userdata file.
     * @param property a String containing the password that is to be encrypted
     * @return the encrypted password in a String format
     */
    private static String encryptPassword (String property) throws GeneralSecurityException, UnsupportedEncodingException 
    {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }
    
    private static String base64Encode(byte[] bytes) 
    {
        return new BASE64Encoder().encode(bytes);
    }
    
    /**
     * Used to decrypt an encrypted password and return it as a string to the program.
     * @param property an encrypted password that is stored to the userdata file.
     * @return a String containing the decrypted password to be used by the program. 
     */
     private static String decryptPassword (String property) throws GeneralSecurityException, IOException 
     {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }
     
    private static byte[] base64Decode(String property) throws IOException 
    {
        return new BASE64Decoder().decodeBuffer(property);
    }
}
