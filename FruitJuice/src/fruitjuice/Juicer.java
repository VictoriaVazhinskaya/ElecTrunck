/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fruitjuice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 *
 * @author User
 */
public class Juicer {

    private List<Juice> juices;

    public Juicer(String fileName) throws FileNotFoundException {
        juices = new ArrayList<Juice>();
        BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            juices.add(new Juice(sc.nextLine()));
        }

    }

    public void printDifferentComponents(String fileName) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
        Set<String> copyFruits = new LinkedHashSet<String>();
        for (int i = 0; i < juices.size(); i++) {
            for (int j = 0; j < juices.get(i).getNumber(); j++) //if(!copyFruits.contains(juices.get(i).getFruit(j).toLowerCase()))
            {
                copyFruits.add(juices.get(i).getFruit(j).toLowerCase());
            }
        }
        Iterator<String> iterator = copyFruits.iterator();
        while (iterator.hasNext()) {
            out.print(iterator.next() + "\r\n");
        }
        out.close();
    }

    public void printSortedComponents(String fileName) throws IOException {

        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
        List<String> copyFruits = new ArrayList<String>();
        for (int i = 0; i < juices.size(); i++) {
            for (int j = 0; j < juices.get(i).getNumber(); j++) {
                copyFruits.add(juices.get(i).getFruit(j));
            }
        }
        Thread sorting = new Thread(new Runnable() {
            synchronized public void run(){
        Collections.sort(copyFruits, new FruitComparator());
            }
        });
        sorting.start();
        if(sorting.isAlive()){
            try{
                sorting.join();
            }
            catch(InterruptedException e){}
        }
        Iterator<String> iterator = copyFruits.iterator();
        while (iterator.hasNext()) {
            out.print(iterator.next() + "\r\n");
        }
        out.close();
    }

    public void timesWash(String fileName) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
//        List<String> copyFruits = new ArrayList<String>();
//        for (int i = 0; i < juices.size(); i++) {
//            copyFruits.add(juices.get(i).getSortedString());
//        }
        FruitComparator comp = new FruitComparator();
        
        List<Juice> copyFruits = new ArrayList<Juice>();
        for(int i=0; i<juices.size(); i++)
            copyFruits.add(new Juice(juices.get(i).toString()));
        Collections.sort(copyFruits, new Comparator<Juice>(){
            @Override
            public int compare(Juice j1, Juice j2){
                if(j1.getNumber()>j2.getNumber())
                    return 1;
                if(j1.getNumber()==j2.getNumber())
                    return 0;
                if(j1.getNumber()<j2.getNumber())
                    return -1;
                return -1;
            }
        });
//        Iterator<Juice> iterator0 = copyFruits.iterator();
//        while (iterator0.hasNext()) {
//            System.out.println(iterator0.next().toString());
//        }
        Iterator <Juice> moving = copyFruits.iterator();
        int j=0;
        int k=0;
        int size=copyFruits.size();
        int subSize=size-1;
        int times=size;
        while(moving.hasNext()){
            for(int i=j+1; i<size; i++)
            {
                if(copyFruits.get(j).contained(copyFruits.get(i)))
                    break;
                k++;
            }
            if(k<subSize)
               --times;
            moving.next();
            j++;
            k=0;
            --subSize;
        }
        out.print(times);
        //System.out.println(times);
        //out.print(times);
        out.close();

    }

    public class Juice {

        public List<String> ingredients;

        public Juice(String ingredients) {
            this.ingredients = new ArrayList<String>();
            StringTokenizer tokens = new StringTokenizer(ingredients, " ");
            while (tokens.hasMoreTokens()) {
                this.ingredients.add(new String(tokens.nextToken()));
            }
        }

        int getNumber() {
            return ingredients.size();
        }
        boolean contained(Juice juice1){
            int counter=0;
            for(int i=0; i<this.getNumber(); i++){
                if(juice1.ingredients.contains(this.ingredients.get(i)))
                    counter++;
            }
            if (counter==this.getNumber())
                return Boolean.TRUE;
            else return Boolean.FALSE;
        }
        String getFruit(int i) {
            if (i < ingredients.size() && i >= 0) {
                return ingredients.get(i);
            } else {
                return null;
            }
        }

        String getSortedString() {
            List<String> copyFruits = new ArrayList<String>();
            for (int i = 0; i < ingredients.size(); i++) {
                copyFruits.add(ingredients.get(i).toLowerCase());
            }
            Collections.sort(copyFruits, new FruitComparator());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ingredients.size(); i++) {
                sb.append(copyFruits.get(i) + " ");
            }
            return sb.toString();
        }
        @Override
        public String toString(){
          StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ingredients.size(); i++) {
                sb.append(ingredients.get(i) + " ");
            }
            return sb.toString().trim().toLowerCase();  
        }

    }

}
