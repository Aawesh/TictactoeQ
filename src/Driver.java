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
        N = 10000;
        N=1000;


        for (int i = 0;i<N;i++){
            game.resetBoard();
//            aiPlayer.setTerminalState(false);
            humanPlayer.setTerminalState(false);
            dummyAI.setTerminalState(false);

            //This loop is for one complete game. Need to run multiple times as per need
            while(true){
               /* dummyAI.makeMove(game,aiPlayer);
                if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                    break;
                }*/
                humanPlayer.makeMove(game,aiPlayer);
                if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){
                    break;
                }

                aiPlayer.makeMove(game);
                if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                    break;
                }
            }
            game.displayBoard();
            qTable.save();
        }

        /*for (int i = 0;i<100;i++){
            game.resetBoard();
            humanPlayer.setTerminalState(false);
            aiPlayer.setTerminalState(false);

            //This loop is for one complete game. Need to run multiple times as per need
            while(true){
                humanPlayer.makeMove(game,aiPlayer);
                if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){ //TODO if game is in terminal state then available moves should be zero
                    break;
                }

                aiPlayer.makeMove(game);
                if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){ //TODO if game is in terminal state then available moves should be zero
                    break;
                }
//                qTable.display();
            }
            game.displayBoard();
//            qTable.display();
        }*/

        qTable.save();
    }
}
