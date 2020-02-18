import java.util.Scanner;

public class Client
{
    public static void main(String args[])
    {
        ConnectionToServer connectionToServer = new ConnectionToServer(ConnectionToServer.DEFAULT_SERVER_ADDRESS, ConnectionToServer.DEFAULT_SERVER_PORT);
        connectionToServer.Connect();
        
        // System.out.println("Enter a message for the echo");
        
        System.out.println("Thank you for connecting, Waiting for opponent!");
        while (connectionToServer.SendForAnswer("ready") != "ok"){                           //Will change
        }
        System.out.println("Opponent Connected, Ready to Start!");
        bool want = true;
        while (want){
            int Deck[] = new int[26];
            //receive 26 values ranging from 0 to 51
            Deck = connectionToServer.SendForAnswer("deck");

            System.out.println("Cards Received");
        
            for (int i = 0 ; i < 26; i++){
                Scanner scanner = new Scanner(System.in);
                System.out.println(" 1) Play the card \n 2) Start a new game \n 3) Quit the game\n");
                System.out.print("Please choose an option");
                String option = scanner.nextLine();

                if (option == "1"){

                    //send server the Deck[i]%13 card
                    //wait for the response

                    String response = connectionToServer.SendForAnswer(String.valueOf(Deck[i]%13));
                    System.out.println(i); //Debug purposes
                    if (response == "restart"){
                        System.out.println("Your opponent wants a rematch \n You win :D");
                    }else if (response == "exit"){
                        System.out.println("Your opponent Quit the game \n You win :D");
                    } else {
                        System.out.println(response);
                    }
                    
                }else if (option == "2"){

                    String response = connectionToServer.SendForAnswer("restart");
                    System.out.println(response);
                    Deck = connectionToServer.SendForAnswer("deck");
                    System.out.println("Cards Received");
                    i = -1; 
                }else if (option == "3"){
                    
                    //send server quit

                    String response = connectionToServer.SendForAnswer("quit");
                    System.out.println(response);
                    want = false;
                    break;
                }else{
                    System.out.println("That is not an option, please  choose another! \n");
                    i -= 1;
                }
            }

        
        
        

            // while (!message.equals("QUIT"))
            // {
            //     System.out.println(connectionToServer.SendForAnswer(message));
            //     message = scanner.nextLine();
            // }
            // connectionToServer.Disconnect();
        }
        connectionToServer.Disconnect();  
    }
}


