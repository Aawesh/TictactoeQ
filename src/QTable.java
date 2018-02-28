import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class QTable {
    private HashMap<String,double []> qMap;

    public QTable(){
        qMap = loadQTable();
    }

    /**
     * Update the Q table with the latest value for current action
     * @param state
     * @param actionIndex
     */
    public void updateQtable(String state, int actionIndex, double reward){
        double [] actionValues = qMap.get(state);
        actionValues[actionIndex] = reward;
        qMap.put(state,actionValues);

        writeToFile("qtable.txt",state +":"+Arrays.toString(actionValues).replace("[","").replace("]",""),true);

    }

    public HashMap<String, double []> loadQTable(){
        HashMap<String, double []> mMap = new HashMap<>();
        /*//TODO load qMap from a file
        if(fileDoesnot exist | is empty){
            mMap = null;
        }else{
            mMap = populateMap from file;
        }*/

        try (BufferedReader br = new BufferedReader(new FileReader("qtable.txt"))) {
            String line;
            double [] values = new double[9];
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] data = line.trim().split(":");


            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return mMap;
    }

    public HashMap<String, double []> getQTable(){
        return this.qMap;
    }

    public double[] getActionValueArray(String key){
        if(key != null){
            if(qMap.containsKey(key)){
                return qMap.get(key);
            }
        }

        return null;
    }


    public void saveState(String state){
        double[] actionValue = new double[9];
        Arrays.fill(actionValue,0);
        qMap.put(state,actionValue);
    }

    public void display(){
        for (String key: qMap.keySet()){
            System.out.println(key + " = " + Arrays.toString(qMap.get(key)));
        }
    }

    public static void writeToFile(String filename, String data, boolean append){
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            File file = new File(filename);

            if (!file.exists()){
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile(), append);
            bw = new BufferedWriter(fw);

            bw.write(data+"\n");
        } catch (IOException e){

            e.printStackTrace();

        } finally{
            try {
                if (bw != null)
                    bw.close();

                if (fw != null){
                    fw.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static ArrayList<Statement> getStatementsFromSourceFile(String sourceFilePath) {
        ArrayList<Statement> statementList = new ArrayList<>();
        operationTable = passOne.getOperationTable();
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith(".")) {
                    String[] data = line.trim().split(delimeter);

                    if (data.length >= 3) {
                        statementList.add(new Statement(data[0],data[1],data[2],true));
                    } else if (data.length == 2) {
                        if(data[0].equalsIgnoreCase("BASE")||data[0].equalsIgnoreCase("END")||
                                data[0].equalsIgnoreCase("EXTREF")||data[0].equalsIgnoreCase("EXTDEF")){
                            statementList.add(new Statement("",data[0],data[1],false));
                        }else{
                            if(operationTable.contains(data[0].replace("+",""))){
                                statementList.add(new Statement("",data[0],data[1],false));
                            }else{
                                statementList.add(new Statement(data[0],data[1],"",true));
                            }
                        }
                    } else if(data.length == 1 && data[0].equalsIgnoreCase("RSUB")){
                        statementList.add(new Statement("",data[0],"",false));
                    }else {
                        PrintUtils.print("Invalid data at line: " + line);
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return statementList;
    }
}
