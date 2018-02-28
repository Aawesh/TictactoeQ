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
    }

    void saveQTable(){
        //TODO save qMap to file
    }

    public HashMap<String, double []> loadQTable(){
        HashMap<String, double []> mMap = new HashMap<>();
        /*//TODO load qMap from a file
        if(fileDoesnot exist | is empty){
            mMap = null;
        }else{
            mMap = populateMap from file;
        }*/

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
}
