import java.util.ArrayList;
import java.util.List;

public class Game {
    private String boardStatus;
    private double temperature;

    public Game(){
        this.boardStatus = "         ";
        this.temperature = 0.0;
    }

    public void displayBoard(){
        char[] state = boardStatus.toCharArray();

        System.out.println("\n\n");
        System.out.println("  {"+state[0]+"} {"+state[1]+"} {"+state[2]+"}");
        System.out.println("----------------");
        System.out.println("  {"+state[3]+"} {"+state[4]+"} {"+state[5]+"}");
        System.out.println("----------------");
        System.out.println("  {"+state[6]+"} {"+state[7]+"} {"+state[8]+"}");
    }

    public void updateBoard(int actionIndex,boolean turn){
        char[] state = boardStatus.toCharArray();
        state[actionIndex] = turn?'1':'0';
        boardStatus = new String(state);
    }

    public String getBoard(){
        return this.boardStatus;
    }

    public int availableMoves(){

        int count = 0;
        char[] state = boardStatus.toCharArray();

        for(int i = 0;i<9;i++){
            if(state[i] == ' '){
                count++;
            }
        }
        return count;
    }

    public boolean isValidMove(int moveIndex){
        if(moveIndex <0 || moveIndex > 8){
            return false;
        }else{
            char[] state = boardStatus.toCharArray();
            if(state[moveIndex] == ' '){
                return true;
            }else{
                return false;
            }
        }
    }

    public void resetBoard(){
        boardStatus = "         ";
    }

    public double getTemperature(){
        return this.temperature;
    }

    public void setTemperature(double t){
        this.temperature = t;
    }
}
