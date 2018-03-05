import java.util.Arrays;
import java.util.Random;

public class Driver {
    private static HumanPlayer humanPlayer;
    private static AIPlayer aiPlayer;
    private static DummyAI dummyAI;
    public static QTable qTable;
    private static Game game;
    /**
     * Main function to drive the program
     * 1 means X -> first player
     * 0 means O -> second player
     * For now Human plays first
     * @param args
     */
    public static void main(String[] args){
        game = new Game(); //TODO pass parameters like discount and all

        qTable = new QTable();

        //Turn is determined randomly. sometimes human play 'X' i.e. 1 sometimes 'O', i.e 0
        Random random = new Random();
        boolean turn = random.nextBoolean();

        //TODO remove this code
        turn = true;

        humanPlayer = new HumanPlayer(!turn);
        aiPlayer = new AIPlayer(turn);
        dummyAI = new DummyAI(!turn);

        int N = 255168; //possible games
        N = 1000000000;
        N=10000000;
        N=0;

        boolean swapTurn;

        for (int i = 0;i<N;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            dummyAI.setTerminalState(false);

            swapTurn = random.nextBoolean();

            if(swapTurn){
                while(true){
                    aiPlayer.makeMove(game);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }

                    dummyAI.makeMove(game,aiPlayer);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }
                }
            }else{
                while(true){
                    dummyAI.makeMove(game,aiPlayer);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }

                    aiPlayer.makeMove(game);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                }
            }
            game.displayBoard();
        }

        int P = 100;
        for (int i = 0;i<P;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            humanPlayer.setTerminalState(false);

            swapTurn = random.nextBoolean();

            if(swapTurn){
                while(true){
                    aiPlayer.makeMove(game);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }

                    humanPlayer.makeMove(game,aiPlayer);
                    if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){
                        break;
                    }
                }
            }else{
                while(true){
                    humanPlayer.makeMove(game,aiPlayer);
                    if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){
                        break;
                    }

                    aiPlayer.makeMove(game);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                }
            }

            game.displayBoard();
        }

        qTable.save();

    }
}
