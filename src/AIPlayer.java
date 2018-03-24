import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.*;

public class AIPlayer {
    private boolean turn;
    private String currentState;
    private int moveIndex;
    private static boolean terminalState;
    Random random;

    public AIPlayer(){
        this.currentState = "";
        this.moveIndex = 0;
        terminalState = false;
        random = new Random();
    }

    public void makeMove(Game g,boolean randomMoveFlag){

        if(randomMoveFlag){
            int moveIndex;
            do{
                moveIndex = random.nextInt(9);
            }while(!g.isValidMove(moveIndex));
            g.updateBoard(moveIndex,turn);
        }else{
            double [] actionValues;
            do{
                this.currentState = g.getBoard();
                Driver.qTable.saveState(this.currentState);

                actionValues = Driver.qTable.getActionValueArray(this.currentState);

                this.moveIndex = getAIMove(actionValues,this.currentState,g);
            }while(!g.isValidMove(this.moveIndex));

            g.updateBoard(this.moveIndex,turn);
            String nextState = g.getBoard();

            if(!Driver.qTable.containsState(nextState)){
                Driver.qTable.saveState(nextState);
            }

            findAndSetTerminalState(nextState,g);

            double learnedSum;
            if(terminalState){
                learnedSum = getReward(nextState,g);
            }else{
                learnedSum = (getReward(nextState,g) + 0.8 * getMinReward(nextState));
            }

            if(Driver.aMap.containsKey(currentState+"_"+moveIndex) == false){
                Driver.aMap.put(currentState+"_"+moveIndex,2);
            }else{
                Driver.aMap.put(currentState+"_"+moveIndex,Driver.aMap.get(currentState+"_"+moveIndex)+1);
            }


            double inertiaSum = actionValues[this.moveIndex];

            double a = 1.0/(double)Driver.aMap.get(currentState+"_"+moveIndex);
            double finalReward = ((1.0 - a) * inertiaSum + (a * learnedSum)) ;

            Driver.qTable.updateQtable(this.currentState,this.moveIndex,finalReward,"ai");
        }
    }

    public void makeLaernedMove(Game g){
            double [] actionValues;
            do{
                this.currentState = g.getBoard();
                Driver.qTable.saveState(this.currentState);

                actionValues = Driver.qTable.getActionValueArray(this.currentState);

                this.moveIndex = getAIMove(actionValues,this.currentState,g);
            }while(!g.isValidMove(this.moveIndex));
        g.updateBoard(moveIndex,turn);

        findAndSetTerminalState(g.getBoard(),g);
    }



    private void findAndSetTerminalState(String board,Game g) {
        int winner = checkWinner(board,g);

        if((winner == 0|| winner == 1 || winner == 2)){
            terminalState = true;
        }else{
            terminalState = false;
        }
    }

    private double getMinReward(String nextState) {
        double min = 999999;
        double[] array = Driver.qTable.getActionValueArray(nextState);
        char[] st = nextState.toCharArray();
        for ( int i = 0; i < array.length; i++ ){
            if(st[i] == ' '){
                if(array[i] < min){
                    min = array[i];
                }
            }
        }
        return min;
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


        winner = 1: X wins
        winner = 0: 0 wins
        winner = 2: Game Draw
        winner = 3: Game continue
     */
    public static double getReward(String board, Game g) {
        double reward;
        int winner = checkWinner(board,g);

        if((winner == 0)){
            reward = 2;
            Driver.AIWin++;
        }else if((winner == 1)){
            reward = -2;
            Driver.AILose++;
        }else if(winner == 2){
            reward = 0;
            Driver.AIDraw++;
        }else{
            reward = 0;
        }

        return reward;
    }

    /*
    * 1 - Win X return 0
    * 2 - Lose
    * 3 - Draw
    * */
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
            result("AI does Game Draw");
            return 2;
        }

        //3 means no winner, game continues
        return 3;
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
//            return getRandomSampleNew(pDistribution,indexList);
        }
    }

    private static int getRandomSampleNew(List<Double> pDistribution, List<Integer> indexList) {
        int[] values = new int[pDistribution.size()];
        double pdf[] = new double[pDistribution.size()];

        for(int i = 0;i<pDistribution.size(); i++){
            values[i] = indexList.get(i);
            pdf[i] = pDistribution.get(i);
        }

        return randsample(values,1,true,pdf);
    }

    /* Unequal probability sampling; with-replacement case
 * n are the lengths of p and indexList. p contains probabilities, indexList
 * contains the actual outcomes, and ans contains an array of values
 * that were sampled.
 */

    static int getRandomSample(List<Double> pDistribution, List<Integer> indexList){
        int[] ind = new int[pDistribution.size()];
        double p[] = new double[pDistribution.size()];

        for(int i = 0;i<pDistribution.size(); i++){
            ind[i] = i;
            p[i] = pDistribution.get(i);
        }
        EnumeratedIntegerDistribution dist = new EnumeratedIntegerDistribution(ind,p);

        int idx = dist.sample();

        return indexList.get(idx);

/*
        int n = pDistribution.size();

        RandomCollection<Integer> rc = new RandomCollection();

        for (int i = 1 ; i < n; i++) {
            rc.add(pDistribution.get(i),indexList.get(i));
        }

        return rc.next();*/
    }


    public static int randsample(int[] values, int numsamples, boolean withReplacement, double [] pdf) {
        try{
            if(withReplacement) {
                double[] cdf = new double[pdf.length];
                cdf[0] = pdf[0];
                for (int i = 1; i < pdf.length; i++) {
                    cdf[i] = cdf[i - 1] + pdf[i];
                }

                int[] results = new int[numsamples];
                for(int i=0; i<numsamples; i++) {
                    int currentPosition = 0;

                    while(new Random().nextDouble() > cdf[currentPosition] && currentPosition < cdf.length) {
                        currentPosition++; //Check the next one.
                    }

                    if(currentPosition < cdf.length) { //It worked!
                        results[i] = values[currentPosition];
                    } else { //It didn't work.. let's fail gracefully I guess.
                        results[i] = values[cdf.length-1];
                        // And assign it the last value.
                    }
                }

                //Now we're done and can return the results!
                return results[numsamples-1];
            } else { //Without replacement.
                System.out.println("This is unimplemented!");
            }
        }catch (Exception e){
            System.out.println("e = " + e);
        }
        return -1;
    }

    public static void result(String s){
//        System.out.println(s);
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
