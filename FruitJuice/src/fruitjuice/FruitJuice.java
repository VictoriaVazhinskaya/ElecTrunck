/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fruitjuice;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author User
 */
public class FruitJuice {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        Juicer myJuicer = new Juicer("juice.in");
        myJuicer.printDifferentComponents("juice1.out");
        myJuicer.printSortedComponents("juice2.out");
        myJuicer.timesWash("juice3.out");
    }
    
}
