package front;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginDataPrint {
    public static void main(String[] args)  {
        try(DataInputStream dis = new DataInputStream(new FileInputStream("src/dto/signup.txt"));) {
           while(true){
               dis.readInt();
               dis.readUTF();
               dis.readUTF();
             }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoginDataPrint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoginDataPrint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }     
}
