package labs;

import DocumentClasses.ItemSet;
import DocumentClasses.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.*;

public class Lab7 {
    public static void main (String[] args){
        Matrix data = new Matrix(process("D:\\College\\Com Sci\\Junior Year\\466\\Labs\\mainProject\\src\\InputFiles\\data.txt"));
        System.out.println(data);
//        ArrayList<Integer> rows = new ArrayList<>();
//        rows.add(0);
//        rows.add(1);
//        rows.add(2);
//        System.out.println(data.findMostCommonValue(rows));
        printDecisionTree(data, getAttributes(data), getAllRows(data), 0, 100);

    }

    public static ArrayList<ArrayList<Integer>> process (String filePath){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitLine = line.split(",");
                ArrayList<Integer> newEntry = new ArrayList<>();
                for (String elem : splitLine){
                    Double value = Double.parseDouble(elem);
                    Integer flooredVal = (int) Math.floor(value);
                    newEntry.add(flooredVal);
                }
                result.add(newEntry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // TODO: finish function
    public static void printDecisionTree(Matrix data, ArrayList<Integer> attributes, ArrayList<Integer> rows, int level, double currentIGR){
        String tabs = "";
        for (int i=0; i<level; i++) {
            tabs.concat("\t");
        }
        int totalNumAttributes = data.getMatrix().get(0).size() - 1;
        ArrayList<Double> IGRPerAttribute = new ArrayList<>(totalNumAttributes);
        for (int i=0; i<totalNumAttributes; i++){
            IGRPerAttribute.add((double) -1);
        }

        if (attributes.size()>0){

            for (Integer attribute : attributes){
                double IGR = data.computeIGR(attribute, rows);      // compute the IGR if you were to split on this attribute for the given rows
                IGRPerAttribute.set(attribute, IGR);    // add this IGR to the arrayList where index 0 represents the IGR of "attribute 1" (technically attribute 0 in data but printed as attribute 1)
            }
            double maxIGR = Collections.max(IGRPerAttribute);       // find best attribute to split on based on their IGRs
            if (maxIGR >= 0.01 && Math.abs(maxIGR - currentIGR) >= 0.01) {     // only split if the change in IGR is more than 0.01
                int attributeToSplit = IGRPerAttribute.indexOf(maxIGR);
                HashMap<Integer, ArrayList<Integer>> splitRows = data.split(attributeToSplit, rows);        // split the data based on attribute and store in map where map[attributeValue] = {rows related}

                for (Map.Entry<Integer, ArrayList<Integer>> node : splitRows.entrySet()) {      // loop thorugh map[attributeValue] = {rows related} to go through all the nodes formed from split
                    for (int i=0; i<level; i++){
                        System.out.print("      ");
                    }
                    int attributeToSplitPrint = attributeToSplit + 1;
                    System.out.println(tabs + "When attribute " + attributeToSplitPrint + " has value " + node.getKey());
//                    double nodeEntropy = data.findEntropy(node.getValue());     // get the entorpy of the rows belonging to specific attribute category
//                    if (nodeEntropy == 0){      //if the node is homogenous
//                        for (int i=0; i<level; i++){
//                            System.out.print("      ");
//                        }
//                        System.out.println(tabs + "  value = " + data.findMostCommonValue(node.getValue(), totalNumAttributes));      //print the class that it belongs to
//                    } else {

                        // make a copy of attributes list and remove the attribute we split on from it.
                        ArrayList<Integer> newAttributes = (ArrayList<Integer>) attributes.clone();
                        Integer idxToRemove = newAttributes.indexOf(attributeToSplit);
                        newAttributes.remove(idxToRemove);

                        // recursively call the printDecisionTree with the attribute split on being removed, and the rows of the attribute cateory we are currently printing subtree of.
                        printDecisionTree(data, newAttributes, node.getValue(), level + 1, maxIGR);
//                    }
                }
            }
            else{
                for (int i=0; i<level; i++){
                    System.out.print("      ");
                }
                System.out.println(tabs + "value = " + data.findMostCommonValue(rows, totalNumAttributes));
            }
        }
    }

    public static ArrayList<Integer> getAttributes(Matrix data){
        ArrayList<Integer> attributes = new ArrayList<>();
        for (int i=0 ; i<data.getMatrix().get(0).size() - 1; i++){
            attributes.add(i);
        }
        return attributes;
    }

    public static ArrayList<Integer> getAllRows(Matrix data){
        ArrayList<Integer> rows = new ArrayList<>();
        for (int i=0 ; i<data.getMatrix().size(); i++){
            rows.add(i);
        }
        return rows;
    }

}
