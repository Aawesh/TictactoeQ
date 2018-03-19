import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DummyAI {
    private Scanner scanner;
    Random random;
    private boolean turn;
    private String currentState;
    private int moveIndex;
    private static boolean terminalState;

    public DummyAI(){
        this.scanner = new Scanner(System.in);
        terminalState = false;
        random = new Random();
    }

    public void makeMove(Game g,boolean randomMoveFlag){
//        g.displayBoard();

        if(randomMoveFlag){
            int moveIndex;
            do{
                moveIndex = random.nextInt(9);
            }while(!g.isValidMove(moveIndex));
            g.updateBoard(moveIndex,turn);
        }else{
            double [] actionValues;
            do{
                //0. If the current board status is not in the Q table, add an entry
                this.currentState = g.getBoard();
                Driver.qTable.saveState(this.currentState);

                //1. Select random value from the q table move if the reward is same for all or some move
                actionValues = Driver.qTable.getActionValueArray(this.currentState);

                this.moveIndex = getAIMove(actionValues,this.currentState,g);
            }while(!g.isValidMove(this.moveIndex));

            //3. update the board with AI move
            g.updateBoard(this.moveIndex,turn);
            String nextState = g.getBoard();

            if(!Driver.qTable.containsState(nextState)){
                Driver.qTable.saveState(nextState); //initializes all the action value pairs with 0
            }


            findAndSetTerminalState(g.getBoard(),g);

            double learnedSum;
            if(terminalState){
                learnedSum = getReward(nextState,g,turn);
            }else{
                learnedSum = (getReward(nextState,g,turn)+ 0.8 * getMaxReward(nextState));
            }

            //update a values
            if(Driver.aMap.containsKey(currentState+"_"+moveIndex) == false){
                Driver.aMap.put(currentState+"_"+moveIndex,2);
            }else{
                Driver.aMap.put(currentState+"_"+moveIndex,Driver.aMap.get(currentState+"_"+moveIndex)+1);
            }


            double inertiaSum = actionValues[this.moveIndex]; // get reward for current move in current state

            double a = 1.0/Driver.aMap.get(currentState+"_"+moveIndex);
            double finalReward = (1.0 - a) * inertiaSum + a * learnedSum ;

            Driver.qTable.updateQtable(this.currentState,this.moveIndex,finalReward,"ai");
        }



    }

    private double getMaxReward(String nextState) {
        double max = 0.0;
        double[] array = Driver.qTable.getActionValueArray(nextState);
        char[] st = nextState.toCharArray();
        for ( int i = 0; i < array.length; i++ ){
            if(st[i] == ' '){
                if(array[i] > max){
                    max = array[i];
                }
            }
        }
        return max;
    }


    /**
     * if we win, reward = +2
     if we lose,reward = -2
     if we draw,reward = -1
     if we are in game, reward = 0
     */
    public static double getReward(String board, Game g, Boolean turn) {
        double reward;
        int winner = checkWinner(board,g);

        if((winner == 1 && turn == true) || (winner == 0 && turn == false)){
            reward = -2;
            Driver.AILose++;
        }else if((winner == 1 && turn == false) || (winner == 0 && turn == true)){
            reward = 2;
            Driver.AIWin++;
        }else if(winner == 2){
            reward = -1;
            Driver.DummyAIDraw++;
        }else{
            reward = 0;
        }

        return reward;
    }

    private void findAndSetTerminalState(String board,Game g) {
        int winner = checkWinner(board,g);

        if((winner == 1 && turn == true) || (winner == 0 && turn == false)){
            terminalState = true;
        }else if((winner == 1 && turn == false) || (winner == 0 && turn == true)){
            terminalState = true;
        }else if(winner == 2){
            terminalState = true;
        }else{
            terminalState = false;
        }
    }


    /**
     *
     * @return index of movement
     */
    public static int getAIMove(double [] actionValues,String state,Game g){
        List<Integer> indexList = new ArrayList();
        List<Double> qValueList = new ArrayList();

        char[] st = state.toCharArray();
        for ( int i = 0; i < actionValues.length; i++ ){
            if(st[i] == ' '){ //Exclude filled position for selection of next move
                indexList.add(i);
                qValueList.add(actionValues[i]);
            }
        }

        if(indexList.size() == 1){
            return indexList.get(0);
        }else{
            List<Double> pDistribution = new ArrayList();
            double pSum = 0.0;
            for ( int i = 0; i < qValueList.size(); i++ ){
                pDistribution.add(i,Math.exp(qValueList.get(i)/g.getTemperature()));
                pSum += pDistribution.get(i);
            }

            for ( int i = 0; i < qValueList.size(); i++ ){
                pDistribution.set(i,pDistribution.get(i)/pSum);
            }

            return getRandomSample(pDistribution,indexList);
        }
    }

    /* Unequal probability sampling; with-replacement case
 * n are the lengths of p and indexList. p contains probabilities, indexList
 * contains the actual outcomes, and ans contains an array of values
 * that were sampled.
 */

    static int getRandomSample(List<Double> pDistribution, List<Integer> indexList){

        int n = pDistribution.size();
        double rU;
        int nm1 = n - 1;

  /*  *//* record element identities *//*
        for (int i = 0; i < n; i++){
            indexList.set(i,i+1);
        }*/

    /* sort the probabilities into descending order */
        double temp;
        int  temp1;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (pDistribution.get(j-1) < pDistribution.get(j)) {
                    temp = pDistribution.get(j-1);
                    pDistribution.set(j-1,pDistribution.get(j));
                    pDistribution.set(j,temp);

                    temp1 = indexList.get(j-1);
                    indexList.set(j-1,indexList.get(j));
                    indexList.set(j,temp1);
                }
            }
        }

    /* compute cumulative probabilities */
        for (int i = 1 ; i < n; i++) {
            pDistribution.set(i, pDistribution.get(i) + pDistribution.get(i - 1));
        }

    /* compute the sample */
        rU = pDistribution.get(new Random().nextInt(pDistribution.size()));
        int j;
        for (j = 0; j < nm1; j++) {
            if (rU <= pDistribution.get(j)) {
                break;
            }
        }
        return indexList.get(j);
    }

    public static double getReward(String board,boolean turn,Game g){
        double reward;

        int winner = checkWinner(board,g);

        if(winner == 0){
            Driver.AIWin++;
        }else if(winner == 1){
            Driver.AILose++;
        }else if(winner == 2){
            Driver.DummyAIDraw++;
        }

        if((winner == 1 && turn == true) || (winner == 0 && turn == false)){
            reward = -1;
        }else{
            reward = 0; //dummy value
        }

        if(winner == 0 ||winner == 1 ||winner == 2){
            terminalState = true;
        }else{
            terminalState = false;
        }

        return reward;
    }

    public static int checkWinner(String board,Game g){
        char[] state = board.toCharArray();
        int[][] boardStatus = new int[3][3];
        int k = 0;
        for(int i = 0;i<3;i++){
            for (int j = 0;j<3;j++){
                boardStatus[i][j] = state[k] - '0';
                k++;
            }
        }

        //Horizontal --- rows
        for(int i=0; i<3; i++){
            if(boardStatus[i][0] == boardStatus[i][1] && boardStatus[i][0] == boardStatus[i][2]){
                if (boardStatus[i][0]==1){
                    result("Player X wins");
                    return 1;
                }
                else if (boardStatus[i][0]==0) {
                    result("Player 0 wins");
                    return 0;
                }
            }
        }

        //Vertical --- columns
        for(int i=0; i<3; i++){
            if(boardStatus[0][i] == boardStatus[1][i] && boardStatus[0][i] == boardStatus[2][i]){
                if (boardStatus[0][i]==1){
                    result("Player X wins");
                    return 1;
                }
                else if (boardStatus[0][i]==0) {
                    result("Player 0 wins");
                    return 0;
                }
            }
        }

        //First diagonal
        if(boardStatus[0][0] == boardStatus[1][1] && boardStatus[0][0] == boardStatus[2][2]){
            if (boardStatus[0][0]==1){
                result("Player X wins");
                return 1;
            }
            else if (boardStatus[0][0]==0) {
                result("Player 0 wins");
                return 0;
            }
        }

        //Second diagonal
        if(boardStatus[0][2] == boardStatus[1][1] && boardStatus[0][2] == boardStatus[2][0]){
            if (boardStatus[0][2]==1){
                result("Player X wins");
                return 1;
            }
            else if (boardStatus[0][2]==0) {
                result("Player 0 wins");
                return 0;
            }
        }

        //2 means no moves and draw
        if(g.availableMoves() == 0){
            result("Dummy AI does Game Draw");
            return 2;
        }

        //3 means no winner, game continues
        return 3;
    }

    public static void result(String s){
//        System.out.println(s);
    }


    public static int getIndexOfLargest( double[] array ){
        if ( array == null || array.length == 0 ) return -1; // null or empty

        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ) largest = i;
        }
        return largest; // position of the first largest found
    }

    public static double getMaxValue( double[] array ){
        if ( array == null || array.length == 0 ) return -1.0;

        int largest = 0;
        for ( int i = 1; i < array.length; i++ )
        {
            if ( array[i] > array[largest] ) largest = i;
        }
        return array[largest]; // position of the first largest found
    }

    public static void setTerminalState(boolean state){
        terminalState = state;
    }

    public static boolean isTerminalState(){
        return terminalState;
    }

    public void setTurn(boolean turn){
        this.turn = turn;
    }

}
