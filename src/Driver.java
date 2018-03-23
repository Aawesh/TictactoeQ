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

        qTable = new QTable("qtable_ai_second.txt");

        //Turn is determined randomly. sometimes human play 'X' i.e. 1 sometimes 'O', i.e 0
        Random random = new Random();
        boolean turn;

        humanPlayer = new HumanPlayer();
        aiPlayer = new AIPlayer();
        dummyAI = new DummyAI();



//        int N = 200000;
        int N = 10000000;
//        int N = 0;
        double temperatureInitial = 0.5;
        double temperatureFinal = 0.005;
        double temperature;

        boolean swapTurn;
        for (int i = 1;i<=N;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            dummyAI.setTerminalState(false);

            temperature = temperatureInitial + (temperatureFinal - temperatureInitial)*((double)i/(double)N);

            swapTurn = false; // false: Dummy AI plays first, true: AI plays first
//            swapTurn = random.nextBoolean(); // Anyone can play first
            turn = false; // AI is 0 (O) Dummy AI is 1 (X)

            aiPlayer.setTurn(turn); // 0, O
            dummyAI.setTurn(!turn); // 1, X


            game.setTemperature(temperature);

            boolean randomMoveFlag;

            int count;
            if(swapTurn){
                randomMoveFlag = true;
                count = 0;
                while(true){
                    aiPlayer.makeMove(game,randomMoveFlag,count);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                    count++;
                    randomMoveFlag = false;

                    dummyAI.makeMove(game,randomMoveFlag,count);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }
                    count++;
                }
            }else{
                randomMoveFlag = true;
                count = 0;
                while(true){
                    dummyAI.makeMove(game,randomMoveFlag,count);
                    if(game.availableMoves() == 0 || dummyAI.isTerminalState()){
                        break;
                    }
                    count++;

                    randomMoveFlag = false;
                    aiPlayer.makeMove(game,randomMoveFlag,count);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                    count++;
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
        game.setTemperature(0.005);

        for (int i = 0;i<P;i++){
            game.resetBoard();
            aiPlayer.setTerminalState(false);
            humanPlayer.setTerminalState(false);

            swapTurn = false; //human first
//            swapTurn = random.nextBoolean(); //Anyone can go first
            turn = false;
            aiPlayer.setTurn(turn);
            humanPlayer.setTurn(!turn);
            int count;
            if(swapTurn){
                qTable = new QTable("qtable_ai_first.txt");
                count = 0;
                while(true){
                    aiPlayer.makeLaernedMove(game,count);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }

                    count ++;
                    humanPlayer.makeMove(game,count);
                    if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){
                        break;
                    }
                    count ++;
                }
            }else{
                qTable = new QTable("qtable_ai_second.txt");
                count = 0;

                while(true){
                    humanPlayer.makeMove(game,count);
                    if(game.availableMoves() == 0 || humanPlayer.isTerminalState()){
                        break;
                    }

                    count ++;

                    aiPlayer.makeLaernedMove(game,count);
                    if(game.availableMoves() == 0 || aiPlayer.isTerminalState()){
                        break;
                    }
                    count ++;
                }
            }

            game.displayBoard();

        }

    }
}
