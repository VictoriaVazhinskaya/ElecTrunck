/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fruitjuice;

import java.util.Comparator;

/**
 *
 * @author User
 */
public class FruitComparator implements Comparator<String> {
    
   @Override
   public int compare(String str1, String str2){
       if(str1.charAt(0)<str2.charAt(0))
                  return -1;
              if(str1.charAt(0)>str2.charAt(0))
                  return 1;
              if(str1.charAt(0)==str2.charAt(0))
              {
                     if(str1.equals(str2))
                         return 0;
                     int n=str1.length()>str2.length()?str2.length():str1.length();
                     int i=1;
                     while(i<n){
                        return compare(str1.substring(i), str2.substring(i)); 
                     }
              }             
              if(str1.length()<str2.length())
                  return -1;
              else return 1;
   }
    
}
