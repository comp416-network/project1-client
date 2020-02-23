import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

class ServerThread extends Thread
{
    protected BufferedReader is1;
    protected PrintWriter os1;
    protected BufferedReader is2;
    protected PrintWriter os2;
    protected Socket s1;
    protected Socket s2;
    private String line1 = new String();
    private String line2 = new String();
    private int[] deck1 ;
    private int[] deck2 ;
    
    

    /**
     * Creates a server thread on the input socket
     *
     * @param s input socket to create a thread on
     */
    public ServerThread(Socket s1,Socket s2)
    {
        this.s1 = s1;
        this.s2 = s2;
        
    }

    public int[][] AssignDeck(){
        int[] a = new int[52];
        for (int i = 0; i < 52; ++i) {
            a[i] = i;
        }
        Random rgen = new Random();
        for (int i=0; i<a.length; i++) {
		    int randomPosition = rgen.nextInt(a.length);
		    int temp = a[i];
		    a[i] = a[randomPosition];
		    a[randomPosition] = temp;
		}

        int[] b = new int[26];
        for (int i = 0; i < 26; ++i) {
            b[i] = a[i];
        }

        int[] c = new int[26];
        for (int i = 0; i < 26; ++i) {
            c[i] = a[i+26];
        }
        int[][] d = new int[2][26];
        d[0] = b;
        d[1] = c;
    return d;
    }

    /**
     * The server thread, echos the client until it receives the QUIT string from the client
     */
    public void run()
    {
        try
        {
            is1 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            os1 = new PrintWriter(s1.getOutputStream());

            is2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
            os2 = new PrintWriter(s2.getOutputStream());
            

        }
        catch (IOException e)
        {
            System.err.println("Server Thread. Run. IO error in server thread");
        }

        try
        {

            line1 = is1.readLine();
            line2 = is2.readLine();

            

            int round = 25;
            int result = 0;

            int[][] d = AssignDeck();
            deck1 = d[0];
            deck2 = d[1];

            String senddeck1 = ""; 
            String senddeck2 = "";
            for (int i = 0;i<26;i++){
                senddeck1 = senddeck1+ Integer.toString(deck1[i])+"/";
                senddeck2 = senddeck2+ Integer.toString(deck2[i])+"/";
            }


            os1.println(senddeck1);
            os1.flush();

            os2.println(senddeck2);
            os2.flush();

            while(true){

                line1 = is1.readLine();
                line2 = is2.readLine();

                if(line1.equals("3") || line2.equals("3")){
                    if (line1.equals("3")){
                        os1.println("3"); //Quit responce
                        os2.println("5"); //Enemy quit responce
                    }else{
                        os1.println("5");
                        os2.println("3");
                    }
                    os1.flush();
                    os2.flush();
                }else{
                    String l1 = line1; 
                    String[] sp1 = l1.split("/", 2);
        
                    String command1 = sp1[0];
                    String cardS1 = sp1[1];
                    int card1 = Integer.parseInt(cardS1)%13;
        
                    String l2 = line2; 
                    String[] sp2 = l2.split("/", 2);
        
                    String command2 = sp2[0];
                    String cardS2 = sp2[1];
                    int card2 = Integer.parseInt(cardS2)%13;
        
                    if (round != 25){
                        if (card1 > card2){
                            os1.println("0");
                            os1.flush();
                            os2.println("2");
                            os2.flush();
                            System.out.println("Player1 won match "+round);
                            result+=1;
                        }else if (card1 < card2){
                            os1.println("2");
                            os1.flush();
                            os2.println("0");
                            os2.flush();
                            System.out.println("Player2 won match "+round);
                            result-=1;
                        }else{
                            os1.println("1");
                            os1.flush();
                            os2.println("1");
                            os2.flush();
                            System.out.println("Match "+round+" was a tie");
                        }
                    }else{
                        String match1 = "";
                        String match2 = "";
                        
                        if (card1 > card2){
                            match1 = "0";
                            match2 = "2";
                            System.out.println("Player1 won match "+round);
                            result+=1;
                        }else if (card1 < card2){
                            match1 = "2";
                            match2 = "0";
                            System.out.println("Player2 won match "+round);
                            result-=1;
                        }else{
                            match1 = "1";
                            match2 = "1";
                            System.out.println("Match "+round+" was a tie");
                        }
                        if (result > 0){
                            os1.println(match1+"/0");
                            os1.flush();
                            os2.println(match2+"/2");
                            os2.flush();
                            System.out.println("Winner is Player1");
                        }else if (result < 0){
                            os1.println(match1+"/2");
                            os1.flush();
                            os2.println(match2+"/0");
                            os2.flush();
                            System.out.println("Winner is Player2");
                        }else{
                            os1.println(match1+"/1");
                            os1.flush();
                            os2.println(match2+"/1");
                            os2.flush();
                            System.out.println("Tie");
                        }
                        break;
                        
                    }
                    
                    round++;
                
                }
               
    

            }





        }
        catch (IOException e)
        {
            line1 = this.getName(); //reused String line for getting thread name
            line2 = this.getName();
            System.err.println("Server Thread. Run. IO Error/ Client " + line1 + " or " + line2 + " terminated abruptly");
        }
        catch (NullPointerException e)
        {
            line1 = this.getName(); //reused String line for getting thread name
            line2 = this.getName(); //reused String line for getting thread name
            
            System.err.println("Server Thread. Run.Client " + line1 + " or " + line2 + " Closed");
        } finally
        {
            try
            {
                System.out.println("Closing the connection");
                if (is1 != null || is2 != null)
                {
                    is1.close();
                    is2.close();
                    System.err.println(" Socket Input Stream Closed");
                }

                if (os1 != null || os2 != null)
                {
                    os1.close();
                    os2.close();
                    System.err.println("Socket Out Closed");
                }
                if (s1 != null || s2 != null)
                {
                    s1.close();
                    s2.close();
                    System.err.println("Socket Closed");
                }

            }
            catch (IOException ie)
            {
                System.err.println("Socket Close Error");
            }
        }//end finally
    }
}