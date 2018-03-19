import java.util.HashMap;
import java.util.Random;

public class Driver {
    private static HumanPlayer humanPlayer;
    private static AIPlayer aiPlayer;
    private static DummyAI dummyAI;
    public static QTable qTable;
    private static Game game;
    public static int AIWin = 0;
    public static int AILose = 0;
    public static int AIDraw = 0;
    public static int DummyAIDraw = 0;
    public static HashMap<String,Integer> aMap;
    /**
     * Main function to drive the program
     * 1 means X -> first player
     * 0 means O -> second player
     * For now Human plays first
     * @param args
     */
    public static void main(String[] args){
        aMap = new HashMap<>();
        game = new Game(); //TODO pass parameters like discount and all

        qTable = new QTable();

        //Turn is determined randomly. sometimes human play 'X' i.e. 1 sometimes 'O', i.e 0
        Random random = new Random();
        boolean turn = random.nextBoolean();

        humanPlayer = new HumanPlayer();
        aiPlayer = new AIPlayer();
        dummyAI = new DummyAI();



        int N = 500000;
        N = 0;
        double temperatureInitial = 0.5;
        double temperatureFinal = 0.02;
        double temperature;

        boolean swapTurn;
        for (int i = 1;i<=N;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            dummyAI.setTerminalState(false);

            temperature = temperatureInitial + (temperatureFinal - temperatureInitial)*((double)i/(double)N);

            swapTurn = false; // Dummy AI plays first
            turn = false; // AI is 0 (O) Dummy AI is 1 (X)

            aiPlayer.setTurn(turn); // 0, O
            dummyAI.setTurn(!turn); // 1, X


            game.setTemperature(temperature);

            boolean randomMoveFlag;

            if(swapTurn){
                randomMoveFlag = true;
                while(true){
                    aiPlayer.makeMove(game,randomMoveFlag);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                    randomMoveFlag = false;

                    dummyAI.makeMove(game,randomMoveFlag);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }
                }
            }else{
                randomMoveFlag = true;
                while(true){
                    dummyAI.makeMove(game,randomMoveFlag);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }

                    randomMoveFlag = false;
                    aiPlayer.makeMove(game,randomMoveFlag);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                }
            }
//                game.displayBoard();
        }

        System.out.println("N = " + N);
        System.out.println("AIWin = " + AIWin);
        System.out.println("AILose = " + AILose);
        System.out.println("AIDraw = " + AIDraw);
        System.out.println("DummyAIDraw = " + DummyAIDraw);
        System.out.println();
        System.out.println();

        qTable.save();



        int P = 100;
        game.setTemperature(0.02);
        for (int i = 0;i<P;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            humanPlayer.setTerminalState(false);

//            swapTurn = random.nextBoolean();
            swapTurn = false; //human first
//            turn = random.nextBoolean();
            turn = false;
            aiPlayer.setTurn(turn);
            humanPlayer.setTurn(!turn);

            if(swapTurn){
                while(true){
                    aiPlayer.makeLaernedMove(game);
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

                    aiPlayer.makeLaernedMove(game);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                }
            }

            game.displayBoard();

        }

    }
}
