import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {

        String id = "0";
        //Change with an id
        
        
        boolean exit = false;

        ConnectionToServer connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
        connectionToServer.Connect();

        
    
        String deckS = connectionToServer.SendForAnswer("1");
    
        String Deck[] = new String[26];
        //receive 26 values ranging from 0 to 51
        String[] d = deckS.split("/",27);
        String debug = "[";
        for (int i = 0; i< 26;i++){
            Deck[i] = d[i];
            debug = debug+Deck[i]+" , ";
        }
        debug = debug+" ] ";
        System.out.println(debug);


        int round = 0;
        
        
        System.out.println("Cards Received");
        boolean gameEnd =false;    

        while (!exit){


            if (gameEnd){
                connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
                connectionToServer.Connect();
        
                
            
                deckS = connectionToServer.SendForAnswer("1");
            
                //receive 26 values ranging from 0 to 51
                d = deckS.split("/",27);
                debug = "[";
                for (int i = 0; i< 26;i++){
                    Deck[i] = d[i];
                    debug = debug+Deck[i]+" , ";
                }
                debug = debug+" ] ";
                System.out.println(debug);
        
    
                round = 0;
                
                
                System.out.println("Cards Received");
                gameEnd =false;    
            }
            Scanner scanner;
            String message ="";
            
    
            String responce = "";
            boolean wrongAnswer = true;
            while(wrongAnswer){
                scanner = new Scanner(System.in);
                System.out.println(" 1) Play the card\n 2) Start a new game\n 3) Quit the game");
                message = scanner.nextLine();
                
                
                if (message.equals("1")){
                    
                    responce = connectionToServer.SendForAnswer("1/"+Deck[round]) ;
                    wrongAnswer = false;
                }else if (message.equals("2")){
                    System.out.println("Cannot restart without quiting the game");
                }else if (message.equals("3")){
                    responce = connectionToServer.SendForAnswer("3");
                    exit = true;
                    wrongAnswer = false;
                }else{
                    System.out.println("Invalid responce");
                }
            }

            
    
            if (responce.equals("0")){
                System.out.println("You win this round");
                round++;
            }else if (responce.equals("1")){
                System.out.println("You did not win nor lose this round");
                round++;
            }else if (responce.equals("2")){
                System.out.println("You lost the round");
                round++;
            }else if (responce.equals("5")){
                System.out.println("Your enemy quit\nYou win");
                gameEnd = true;
            }else if (responce.equals("3")){
                System.out.println("You quit\nEnd of game");
                gameEnd = true;
            }else{
                String[] results = responce.split("/",3);
                System.out.println(results[1]);
                if (results[0].equals("0")){
                    System.out.println("You win this round");
                    round++;
                }else if (results[0].equals("1")){
                    System.out.println("You did not win nor lose this round");
                    round++;
                }else if (results[0].equals("2")){
                    System.out.println("You lost the round");
                    round++;
                }
                if (results[1].equals("0")){
                    System.out.println("You win!\n");
                }else if (results[1].equals("1")){
                    System.out.println("You did not win nor lose!\n");
                }else if (results[1].equals("2")){
                    System.out.println("You lost!\n");
                }
                gameEnd = true;
            }

            if (gameEnd){
                connectionToServer.Disconnect();
                System.out.println("Disconnecting from current game\n");
               
                

                wrongAnswer = true;
                while(wrongAnswer && !exit){
                    scanner = new Scanner(System.in);
                    System.out.println(" 1) Play the card\n 2) Start a new game\n 3) Quit the game");
                    message = scanner.nextLine();
                    
                    
                    if (message.equals("1")){
                        System.out.println("Cannot play card without joining a game (2)");
                        
                    }else if (message.equals("2")){

                        System.out.println("Starting new game");
                        wrongAnswer = false;
                    }else if (message.equals("3")){
                        
                        exit = true;
                        wrongAnswer = false;
                    }else{
                        System.out.println("Invalid responce");
                    }
                }


            }


    
        }

        System.out.println("Thank you for playing\n");
        
    }
}


