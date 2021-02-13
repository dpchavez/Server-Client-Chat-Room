import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

/*Project 5
 * Daniela Chavez & Jack Martin
 * CS 342
 * University Of Illinois at Chicago
 * Fall 2020 - Corona Time
 * */

public class Server{

	int TheCount = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	String[] values;
	ArrayList<Integer> ActiveClients = new ArrayList<Integer>(50);
	
	boolean newClient = false;
	boolean deletedClient = false;
	
	ClientsInfo newData = new ClientsInfo();
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
				callback.accept("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), TheCount);
				callback.accept("client has connected to server: " + "client #" + TheCount);
				clients.add(c);
				ActiveClients.add(TheCount);
				newClient = true;
				
				c.start();
				
				TheCount++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			
			Boolean CheckThreads(int count, String[] values ) 
			{
				for(int i = 0; i < values.length; i++) 
				{
					int temp = Integer.parseInt(values[i]);
					if(temp == count)
						return true;
				}
				
				return false;
			}
			
			public void updateClients(String message, int count, ArrayList<Integer> ActiveClients, ArrayList<Integer> TheClients, String[] values) {
				ClientsInfo clientInfo = new ClientsInfo();
				ActiveClients = TheClients;
				if (newClient == true) 
				{
					for(int i = 0; i < ActiveClients.size(); i++) 
					{
						clientInfo.ActiveClients.add(ActiveClients.get(i));
					}
					clientInfo.clientNumber = count;
					clientInfo.newClient = true;
				}
				
				
				
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
				synchronized(t){
					try {
						ClientsInfo newinfo = new ClientsInfo();
						if(values == null) 
						{
							newinfo = clientInfo;
							newinfo.message = message;
							t.out.writeObject(newinfo);
						}
						if (CheckThreads(t.count, values) == true) 
						{
							newinfo = clientInfo;
							newinfo.message = message;
							t.out.writeObject(newinfo);
							
						}
						

					
					}
					catch(Exception e) {}
				}
				}
			}
			
			
			
			
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				if(newClient == true) {
					updateClients("new client on server: client #"+count, count, ActiveClients, ActiveClients, values);
					newClient = false;
				}
				
				
					
				 while(true) {
					    try {
					    	ClientsInfo data = (ClientsInfo)in.readObject();
					    	data.newClient = false;
					    	values = data.SendingTo;
					    	updateClients("client #"+count+" said: "+data.message, count, data.ActiveClients, ActiveClients, values);
					    	callback.accept("client: " + count + " sent: " + data.message);
					    	
					    
					    	
					    	
					    	//updateClients("client #"+count+" said: "+data.message, count, data.ActiveClients, ActiveClients, data.SendingTo);
					    	//out.writeObject(clientinfo);
					    	
					    	
					    	}
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	
					    	for (int i = 0; i < ActiveClients.size(); i++) 
					    	{
					    		if(ActiveClients.get(i) == count) 
					    		{
					    			ActiveClients.remove(i);
					    		}
					    	}
					    	
					    	newClient = true;
					    	updateClients("Client #"+count+" has left the server!", count, ActiveClients, ActiveClients, values);
					    	clients.remove(this);
					    	newClient = false;
					    	
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
