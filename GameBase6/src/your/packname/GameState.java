package your.packname;



public class GameState {
    Board board;
    int[] numKicks;
    boolean kickmove; 
    int choice1;
    int choice2;
    int playerturn;
    boolean used[][];
    int nUnused;
    int nUsed;
    int lastUsed;
    int lastPair[];
    int numTurns;
    boolean selected[];
    
    public GameState() {
        board = new Board();
        numKicks = new int[3];
        kickmove = false;
        choice1 = -1;
        choice2 = -1;
        playerturn = 1;
        used = new boolean[4][4];
        for(int i = 0; i < 4; i++ ) {
            for(int j = 0; j < 4; j++) {
                used[i][j] = false;
            }
        }
        nUnused = 0;
        nUsed = 0;
        lastUsed = 0;
        lastPair = new int[2];
        numTurns = 0;
        selected = new boolean[16];
    }
    
  /*  public String getString() {
        return ("choice 1:" + choice1 + " choice 2:" + choice2 + " nUsed:" + nUsed + " nUnused:" + nUnused);
    }*/
    
    public String string() {
        
        String s = "";
        
        s += board.string() + " ";
        s += numKicks[0] + "" + numKicks[1] + "" + numKicks[2] + " ";
        if(kickmove) s += 1 + " ";
        else s += "0" + " ";
        if(choice1 > 9 || choice1 < 0) s += choice1 + " ";
        else s += 0 + "" + choice1 + " ";
        if(choice2  > 9 || choice2 < 0) s += choice2 + " ";
        else s += 0 + "" + choice2 + " ";
        //playerturn?
        s += boolString(used) + " ";
        s += nUnused + " ";
        s += nUsed + " ";
        //lastused
        if(numTurns > 99) s += numTurns + " ";
        else if(numTurns > 9) s += "0" + numTurns + " ";
        else s += "00" + numTurns + " ";
        for(int i = 0; i < 16; i++) {
            if(selected[i]) s += "1"; //this only needs to be blank at each turn's start
            else s += "0";
        }
        
        return s;
    }
    
    public GameState (String s) {
        //System.out.println(s);
        int t = 0;
        board = new Board();
        numKicks = new int[3];
        used = new boolean[4][4];
        selected = new boolean[16];
        for(int i = 0; i < 4; i++ ) {
            for(int j = 0; j < 4; j++) {
                board.tables[i].table[j] = Integer.parseInt("" +s.charAt(i*4+j));
            }
        }
        t = 17;
        numKicks[0] = Integer.parseInt("" + s.charAt(t));
        t++;
        numKicks[1] = Integer.parseInt("" + s.charAt(t));
        t++;
        numKicks[2] = Integer.parseInt("" + s.charAt(t));
        t+=2;
        kickmove = Integer.parseInt("" + s.charAt(t)) == 1;
        t+=2;
        choice1 = Integer.parseInt(s.substring(t, t+2));
        t+=3;
        choice2 = Integer.parseInt(s.substring(t, t+2));
        t+=3;
        for(int i = 0; i < 4; i++ ) {
            for(int j = 0; j < 4; j++) {
                used[i][j] = Integer.parseInt("" + s.charAt(t)) == 1;
                t++;
            }
        }
        t++;
        nUnused = Integer.parseInt("" + s.charAt(t));
        t+=2;
        nUsed = Integer.parseInt("" + s.charAt(t));
        t+=2;
        numTurns = Integer.parseInt(s.substring(t, t+3));
        t+=4;
        
        for(int i = 0; i < 16; i++) { //needed? only needs to be falses 
            selected[i] = false; 
        }
        
        
    }
    
    public static void main(String[] args) {
        GameState g = new GameState();
        
        g.board.add(5, 1);
        g.board.add(15, 2);
        g.numKicks[2] = 1;
        g.choice1 = 5;
        g.choice2 = 13;
        g.used[3][3] = true;
        g.nUsed = 1;
        g.nUnused = 1;
        g.numTurns = 87;
        
        String s = g.string();
        
        //System.out.println(s);
        
        GameState g2 = new GameState(s);
        
        System.out.println(g.string());
        System.out.println(g2.string());
    }
    
    private String boolString(boolean[][] used) {
        String s = "";
        
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(used[i][j]) s += 1;
                else s += 0;
            } 
        }
        
        return s;
    }
    
}
