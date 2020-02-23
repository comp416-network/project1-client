import java.util.Random;
import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        // generate player id
        Random rng = new Random();
        String id = Integer.toString(rng.nextInt(10000000));

        // true if player has exited. terminates client.
        boolean exit = false;

        // from ps
        ConnectionToServer connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
        connectionToServer.Connect();

        // initiate connection, should return deck string
        String deckS = connectionToServer.SendForAnswer("0/"+id);


        String Deck[] = new String[26];
        //receive 26 values ranging from 0 to 51
        String[] d = deckS.split("/",27);
        String debug = "[";
        for (int i = 0; i< 26;i++){ // create array by splitting deck string
            Deck[i] = d[i];
            debug = debug+Deck[i]+" , ";
        }
        debug = debug+" ] ";
        System.out.println(debug);

        int round = 0; // to keep track of round count and player score
        int score = 0;


        System.out.println("Cards Received");
        boolean gameEnd =false; // true if game has ended, should disconnect.

        // terminates if client quits
        while (!exit){

            // connection should restart if game has ended and player wants new game
            if (gameEnd){
                connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
                connectionToServer.Connect();

                deckS = connectionToServer.SendForAnswer("0/"+id);

                d = deckS.split("/",27);
                debug = "[";
                for (int i = 0; i< 26;i++){
                    Deck[i] = d[i];
                    debug = debug+Deck[i]+" , ";
                }
                debug = debug+" ] ";
                System.out.println(debug);

                round = 0;
                score = 0;

                System.out.println("Cards Received");
                gameEnd =false;
            }
            Scanner scanner;
            String message =""; // used for system.in

            String response = ""; // used for server response
            boolean wrongAnswer = true;
            while(wrongAnswer){
                // from ps
                scanner = new Scanner(System.in);
                System.out.println(" 1) Play the card\n 2) Start a new game\n 3) Quit the game");
                message = scanner.nextLine();

                // handle user input, get response from server
                if (message.equals("1")){
                    System.out.println("\nWaiting for opponent response\n");
                    response = connectionToServer.SendForAnswer("2/"+Deck[round]) ;
                    wrongAnswer = false;
                }else if (message.equals("2")){ // cannot start new game if in game
                    System.out.println("Cannot restart without quiting the game");
                }else if (message.equals("3")){ // player quits
                    response = connectionToServer.SendForAnswer("5");
                    exit = true;
                    wrongAnswer = false;
                }else{
                    System.out.println("Invalid response");
                }
            }

            // game or round result
            String[] results = response.split("/",3);

            if (results[0].equals("3")){
                if (results[1].equals("0")){
                    System.out.println("You win this round");
                    score++;
                    System.out.println("Round: "+(round+1)+", Score: "+score+"\n");
                }else if (results[1].equals("1")){
                    System.out.println("You did not win nor lose this round");
                    System.out.println("Round: "+(round+1)+", Score: "+score+"\n");
                }else if (results[1].equals("2")){
                    System.out.println("You lost the round");
                    System.out.println("Round: "+(round+1)+", Score: "+score+"\n");
                }
            }else{
                if (results[1].equals("0")){
                    System.out.println("You win!\n");
                }else if (results[1].equals("1")){
                    System.out.println("You did not win nor lose!\n");
                }else if (results[1].equals("2")){
                    System.out.println("You lost!\n");
                }
                gameEnd = true;
            }

            round++;

            // disconnect after game ends
            if (gameEnd){
                connectionToServer.Disconnect();
                System.out.println("Disconnecting from current game\n");

                wrongAnswer = true;
                while(wrongAnswer && !exit){
                    scanner = new Scanner(System.in);
                    System.out.println(" 1) Play the card\n 2) Start a new game\n 3) Quit the game\n");
                    message = scanner.nextLine();

                    if (message.equals("1")){ // cannot play card without game
                        System.out.println("Cannot play card without joining a game.");

                    }else if (message.equals("2")){

                        System.out.println("Starting new game");
                        wrongAnswer = false;
                    }else if (message.equals("3")){
                        exit = true;
                        wrongAnswer = false;
                    }else{
                        System.out.println("Invalid response");
                    }
                }

            }

        }

        System.out.println("Thank you for playing\n");

    }
}


