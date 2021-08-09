/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class FileIO {
    private File file;

    public FileIO() {
    }

    public FileIO(File file) {
        this.file = file;
    }
    
    public void witeObjectToFile(List list){   
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for(Object object: list){
                oos.writeObject(object);
            }
            fos.close();
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("this file " + file.getName() + " is not exist");
        } catch (IOException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }
    
    public List readObjectFromFile(){
        List list = new ArrayList();
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while(fis.available() > 0){
                Object object = ois.readObject();
                list.add(object);
            }
            fis.close();
            ois.close();
                   
        } catch (FileNotFoundException ex) {
            System.out.println("this file " + file.getName() + " is not exits. but It still oke");
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    
}
