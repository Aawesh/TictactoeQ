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
    public void updateQtable(String state, int actionIndex, double reward,String who){
        double [] actionValues = qMap.get(state);
        actionValues[actionIndex] = reward;

        checkValidityOfQTable(state,reward,actionIndex,who);

        qMap.put(state,actionValues);
    }

    private void checkValidityOfQTable(String state, double reward, int actionIndex,String who) {
        char[] st = state.toCharArray();
        if(st[actionIndex] != ' '){
            System.out.println("Error in=="+state+":"+actionIndex+":"+reward+":"+who);
        }

    }

    public HashMap<String, double []> loadQTable(){
        HashMap<String, double []> mMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("qtable.txt"))) {
            String line;
            double [] values = new double[9];
            while ((line = br.readLine()) != null) {
                String[] data = line.split(":");
                String[] array = data[1].split(",");
                for(int i= 0;i<9;i++){
                    values[i] = Double.parseDouble(array[i]);
                }

                mMap.put(data[0],values);
            }
        }catch (IOException e) {
            try {
                File file = new File("qtable.txt");

                if (!file.exists()){
                    file.createNewFile();
                }
            } catch (IOException exp){
                exp.printStackTrace();
            }
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
        if(!qMap.containsKey(state)){
            double[] actionValue = new double[9];
            Arrays.fill(actionValue,0);
            qMap.put(state,actionValue);
        }
    }

    public void display(){
        for (String key: qMap.keySet()){
            System.out.println(key + " = " + Arrays.toString(qMap.get(key)));
        }
    }

    public void save(){
        try {
            File file = new File("qtable.txt");

            if (file.exists()){
                file.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        for (String state : qMap.keySet()) {
            writeToFile("qtable.txt",state +":"+Arrays.toString(qMap.get(state)).replace("[","").replace("]",""),true);
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
}
