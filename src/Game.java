import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        System.out.println("  {"+decode(state[0])+"} {"+decode(state[1])+"} {"+decode(state[2])+"}");
        System.out.println("----------------");
        System.out.println("  {"+decode(state[3])+"} {"+decode(state[4])+"} {"+decode(state[5])+"}");
        System.out.println("----------------");
        System.out.println("  {"+decode(state[6])+"} {"+decode(state[7])+"} {"+decode(state[8])+"}");
    }

    public static char decode(char s){

        if(s != ' '){
            if((((int)s)) < 88){
                return 'O';
            }else{
                return 'X';
            }
        }else{
            return s;
        }

    }

    public void updateBoard(int actionIndex,boolean turn,int count){
        char[] state = boardStatus.toCharArray();
        state[actionIndex] = turn?encode('1',count):encode('0',count);
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

    public static char encode(char s,int count){

        char ret;
        if(s == '1'){
            ret =  (char)(88+count);
        }else{
            ret  = (char)(79+count);
        }
        return ret;
    }
}
