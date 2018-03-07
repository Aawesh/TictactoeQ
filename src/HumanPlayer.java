import java.util.Scanner;

public class HumanPlayer {
    private boolean turn;
    private Scanner scanner;
    private static boolean terminalState;

    public HumanPlayer(){
        this.scanner = new Scanner(System.in);
        terminalState = false;
    }

    public void makeMove(Game g,AIPlayer aiPlayer){
        g.displayBoard();
        int moveIndex;
        do{
            System.out.println("Enter the position starting from 0 in row major order:");
            moveIndex = scanner.nextInt();
        }while(!g.isValidMove(moveIndex));

        g.updateBoard(moveIndex,turn);
        //4. Update Q table for the move performed
        double reward = getReward(g.getBoard(),turn,g);
        if(reward != 0){
            Driver.qTable.updateQtable(aiPlayer.getCurrentState(),aiPlayer.getMoveIndex(),reward,"human");
        }
    }

    public static double getReward(String board,boolean turn,Game g){
        double reward;

        int winner = checkWinner(board,g);
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
            result("Human player does Game Draw");
            return 2;
        }

        //3 means no winner, game continues
        return 3;
    }

    public static void result(String s){
        System.out.println(s);
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
