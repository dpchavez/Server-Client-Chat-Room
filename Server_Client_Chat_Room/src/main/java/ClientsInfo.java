import java.io.Serializable;
import java.util.ArrayList;

/*Project 5
 * Daniela Chavez & Jack Martin
 * CS 342
 * University Of Illinois at Chicago
 * Fall 2020 - Corona Time
 * */

@SuppressWarnings("serial")
public class ClientsInfo implements Serializable
{
	//This is where I am going to keep the data sent by the clients...
	ArrayList<Integer> ActiveClients;
	String message;
	boolean newClient;
	String[] SendingTo;
	int clientNumber;
	ClientsInfo()
	{
		//UPDATE; FOR NOW ONLY SEND MSGS. I WILL LATER IMPLEMENT THE NUMBER OF CLIENTS
		ActiveClients = new ArrayList<Integer>(50);
		message = "-";
		newClient = false;

		clientNumber = 0;
	}

}