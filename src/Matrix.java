package DocumentClasses;

import java.lang.reflect.Array;
import java.util.*;

public class Matrix {
    private ArrayList<ArrayList<Integer>> matrix;
    public Matrix(){
        this.matrix = new ArrayList<>();
    }

    public Matrix(ArrayList<ArrayList<Integer>> data){
        this.matrix = data;
    }

    private int findFrequency(int attribute, int value, ArrayList<Integer> rows){
        int freq = 0;
        for (Integer rowNum : rows){
            ArrayList<Integer> specifiedRow = this.matrix.get(rowNum);
            if (specifiedRow.get(attribute) == value){
                freq++;
            }
        }
        return freq;
    }

    public ArrayList<ArrayList<Integer>> getMatrix(){
        return this.matrix;
    }
    private HashSet<Integer> findDifferentValues(int attribute, ArrayList<Integer> rows){
        HashSet<Integer> result = new HashSet<>();
        for (Integer rowNum : rows){
            result.add(this.matrix.get(rowNum).get(attribute));
        }
        return result;
    }

    private ArrayList<Integer> findRows(int attribute, int value, ArrayList<Integer> rows){
        ArrayList<Integer> resultRows = new ArrayList<>();

        return resultRows;
    }

    private double log2(double number){
        return Math.log(number) / Math.log(2);
    }

    // calculating entropy for one of the options when we split on an attribute
    public double findEntropy(ArrayList<Integer> rows){
        double entropy = 0;
        HashMap<Integer, Integer> classesCount = new HashMap<>();
        for (Integer rowNum : rows){
            ArrayList<Integer> row = this.matrix.get(rowNum);       // go through each of the specified rows
            // row.get(4) is the class (1,2,3) in our case. but we use a hashmap so we can support diff num of classes
            classesCount.put(row.get(4), classesCount.getOrDefault(row.get(4), 0) + 1);     // increment the counter for items in the class that the row belongs to
        }

        // go through all identified classes and calculate total entropy
        for (Map.Entry<Integer, Integer> entry : classesCount.entrySet()){
            double ratio = (double) entry.getValue() / rows.size();
            entropy += ratio * log2(ratio);
        }

        return Math.abs(entropy);
    }

    // calculating entropy for one layer when we split on attribute
    private double findEntropy(int attribute, ArrayList<Integer> rows){
        double totalEntropy = 0;
        HashMap<Integer, ArrayList<Integer>> rowsForEachAttributeVal = split(attribute, rows);     // stores the row numbers per attribute category

        for (Map.Entry<Integer, ArrayList<Integer>> entry : rowsForEachAttributeVal.entrySet()){
            ArrayList<Integer> rowsForCategory = entry.getValue();
            double entriesInCateogory = rowsForCategory.size();        // number of elements in the node after the split
            totalEntropy += (entriesInCateogory / rows.size()) * findEntropy(rowsForCategory);
        }

        return totalEntropy;
    }

    private double findGain(int attribute, ArrayList<Integer> rows){
        double gain = 0;

        double originalEntropy = findEntropy(rows);
        double splitEntropy = findEntropy(attribute, rows);

        return Math.abs(originalEntropy - splitEntropy);
    }

    public double computeIGR(int attribute, ArrayList<Integer> rows){
        double gain = findGain(attribute, rows);
        HashMap<Integer, ArrayList<Integer>> rowsForEachAttributeVal = split(attribute, rows);     // stores the row numbers per attribute category
        double denominator = 0;

        for (Map.Entry<Integer, ArrayList<Integer>> entry : rowsForEachAttributeVal.entrySet()){
            double entriesInCateogory = entry.getValue().size();        // number of elements in the node after the split
            denominator += (entriesInCateogory / rows.size()) * log2(entriesInCateogory / rows.size());
        }

        return gain / Math.abs(denominator);
    }

    public int findMostCommonValue(ArrayList<Integer> rows, int colOfClass){
        HashMap<Integer, Integer> valuesCounts = new HashMap<>();
        for (int i=0; i<this.matrix.size(); i++){
            if (rows.contains(i)){      // to ignore the rows not specified in parameter
                ArrayList<Integer> curRow = this.matrix.get(i);     // get the row
                Integer value = curRow.get(colOfClass);
                if (valuesCounts.containsKey(value)){
                    valuesCounts.put(value, valuesCounts.get(value) + 1);       // increment count of that value
                } else{
                    valuesCounts.put(value, 1);     // add value into hashmap to keep count
                }
            }
        }

        return Collections.max(valuesCounts.entrySet(), Map.Entry.comparingByValue()).getKey(); // return the max counted key

    }

    // links the categories that the rows get put under if split on given attribute
    // if we split on age, hashMap.get("young") = {1, 4, 5} means rows 1 4 and 5 have "young" in the age category
    public HashMap<Integer, ArrayList<Integer>> split(int attribute, ArrayList<Integer> rows){
        HashMap<Integer, ArrayList<Integer>> rowsForEachAttributeVal = new HashMap<>();

        for (Integer rowNum : rows){
            ArrayList<Integer> row = this.matrix.get(rowNum);
            int attributeCategory = row.get(attribute);         // get the cateogory that the row gets put under if split on given attribute
            if (rowsForEachAttributeVal.containsKey(attributeCategory)){        // if this attribute val is already seen before, just add the row to list of related rows
                rowsForEachAttributeVal.get(attributeCategory).add(rowNum);
            } else {        // if it is a new attribute value/category, make a new arraylist for it
                ArrayList<Integer> rowsForAttribute = new ArrayList<>();
                rowsForAttribute.add(rowNum);
                rowsForEachAttributeVal.put(attributeCategory, rowsForAttribute);
            }
        }
        return rowsForEachAttributeVal;
    }

    public String toString(){
        return this.matrix.toString();
    }

    // returns index of all rows
    public ArrayList<Integer> findAllRows(){
        ArrayList<Integer> allRows = new ArrayList<>();
        for (int i=0; i<this.matrix.size(); i++){
            allRows.add(i);
        }
        return allRows;
    }

    // returns the index of the category attribute (the class)
    public int getCategoryAttribute(){
        return this.matrix.get(0).size() - 1;
    }

    //TODO
    public double findProb(ArrayList<Integer> row, int category){
        double probCategoryGivenCond = 1;
        double probCategoryAlone = (double) countRowsGivenCategory(category) / this.matrix.size();
        for (int i=0; i<row.size(); i++){
            probCategoryGivenCond = probCategoryGivenCond * probOfCategoryGivenConditionForAttribute(category, row.get(i), i);
        }
        return probCategoryGivenCond * probCategoryAlone;
    }

    // returns sth like P(A|B) or P(young | owns a car) to get prob of person being young given that they own a car where "own a car" is category and "young" is condition
    public double probOfCategoryGivenConditionForAttribute(int category, int condition, int attributeIdx){
        double prob = 0;
        int categoryAttribute = getCategoryAttribute();
        int totalNumForCatgory = countRowsGivenCategory(category);
        int numConditionGivenCategory = 0;

        for (ArrayList<Integer> row : this.matrix){
            if (row.get(attributeIdx) == condition && row.get(categoryAttribute) == category){
                numConditionGivenCategory++;
            }
        }
        HashSet<Integer> allValuesForAttribute = findDifferentValues(attributeIdx, findAllRows());

        prob = (double) (numConditionGivenCategory + (double) 1/this.matrix.size()) / (totalNumForCatgory + allValuesForAttribute.size() * (double) 1 / this.matrix.size());

        return prob;
    }

    // returns the total number of rows with the given category (class. last column)
    public int countRowsGivenCategory(int category){
        int categoryAttribute = getCategoryAttribute();
        int prob = 0;

        for (ArrayList<Integer> row : this.matrix){
            if (row.get(categoryAttribute) == category){
                prob++;
            }
        }
        return prob;
    }

    public int findCategory(ArrayList<Integer> row){
        HashMap<Integer, ArrayList<Integer>> rowsForEachAttributeVal = split(getCategoryAttribute(), findAllRows());     // stores all the different categories in the dataset
        HashMap<Integer, Double> probPerCategory = new HashMap<>();
        int maxCategory = -1;
        double maxProbability = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> entry : rowsForEachAttributeVal.entrySet()){
            probPerCategory.put(entry.getKey(), findProb(row, entry.getKey()));     // calculate probability of each category and add to hashmap
        }

        for (Map.Entry<Integer, Double> entry : probPerCategory.entrySet()){
            System.out.println("For value " + entry.getKey() + ": Probability is: " + entry.getValue());

            if (entry.getValue() > maxProbability){
                maxCategory = entry.getKey();
                maxProbability = entry.getValue();
            }
        }

        System.out.println("Expected category: " + maxCategory);

        return maxCategory;
    }
}
