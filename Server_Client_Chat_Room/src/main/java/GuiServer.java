/*Project 5
 * Daniela Chavez & Jack Martin
 * CS 342
 * University Of Illinois at Chicago
 * Fall 2020 - Corona Time
 * */
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1;
	static Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	Pane startPane;
	Server serverConnection;
	Client clientConnection;
	TextField t2 = new TextField();
	Button refresh = new Button("Refresh");
	Button recipients = new Button("Add Client");
	
	Button Exit = new Button("Exit Server");
	 
	String[] values;
	
	Text recipientsText = new Text("Choose which client(s) you want \nto text ex: 1,2");
	Text yourMessages = new Text("~Your Messages~");
	Text activeClientsText = new Text("Clients in server");
	Text refreshMsg = new Text("Click to delete recipients \n"+"and send to all clients");
	
	
	int counter = 0;
	ListView<String> listItems = new ListView<String>();
	ListView<ClientsInfo>listItems2 = new ListView<ClientsInfo>();
	ListView<String>listItems3 = new ListView<String>();
	ListView<ClientsInfo>information = new ListView<ClientsInfo>();
	
	ListView<String> Recipients = new ListView<String>();
	
	ListView<String>ActiveClients = new ListView<String>();
	 ClientsInfo dummy;
	 Stage primaryStage;
	 ArrayList<String> clients;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		serverChoice = new Button("Server");
		serverChoice.setStyle("-fx-font: 12px Verdana; -fx-base: pink;");
		serverChoice.setLayoutX(100);
		serverChoice.setLayoutY(130);
	
		serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});
		
		
		clientChoice = new Button("Client");
		clientChoice.setStyle("-fx-font: 12px Verdana; -fx-base: lavender;");
		clientChoice.setLayoutX(200);
		clientChoice.setLayoutY(130);
		
		clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
				primaryStage.setTitle("This is a client");

				clientConnection = new Client(data->{

			Platform.runLater(()->{
				listItems2.getItems().add((ClientsInfo)data);
				information.getItems().add((ClientsInfo)data);
				ClientsInfo clientsInformation = (ClientsInfo)data;
				clients = new ArrayList<String>();
				dummy = clientsInformation;
				
				listItems3.getItems().add(clientsInformation.message);
		

				
				if(clientsInformation.newClient == true) {
					
					ActiveClients.getItems().clear();
					
					
					for(int i = 0; i < clientsInformation.ActiveClients.size(); i ++) 
					{
						if(clients.contains(clientsInformation.ActiveClients.get(i).toString()) == false) 
						{
							clients.add(clientsInformation.ActiveClients.get(i).toString());

							
						}
						ActiveClients.getItems().add("Clients #" + clients.get(i));
					}

					counter++;

				 
				}
			});
		});
		clientConnection.start();
	});

	startPane = new Pane();
	startPane.setStyle("-fx-background-color: beige");
	startPane.getChildren().addAll(serverChoice, clientChoice);
		
	startScene = new Scene(startPane, 350,300);
	
	refresh.setOnAction(R->{
		Recipients.getItems().clear();
		values = null;
		
	});
	
	Exit.setOnAction(E->{
		primaryStage.close();
	});
		
		
	c1 = new TextField();
	b1 = new Button("Send");
	
		
	b1.setOnAction(e->{
		ClientsInfo info = new ClientsInfo();
		info.message = c1.getText();
			
			
		if(values == null) 
		{
			synchronized(clientConnection) {
			clientConnection.send(info);
			}
		c1.clear();
		}
		
		else 
		{
			info.SendingTo = values;
			synchronized(clientConnection) {
				clientConnection.send(info);
			}
			
			c1.clear();	
		}
		});
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  CreateClientGUI());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: pink");
//		pane.setStyle("-fx-font: 12px Verdana; fx-background-color: pink;");
		listItems.setStyle("-fx-font: 12px Verdana;");
		pane.setCenter(listItems);
	
		return new Scene(pane, 500, 400);
		
		
	}
	
	//not used atm
	public Scene createClientGui() {
		
		clientBox = new VBox(10, c1,b1,listItems2);
		clientBox.setStyle("-fx-background-color: pink");
		return new Scene(clientBox, 500, 500);
		
	}
	
	
	public Scene CreateClientGUI() 
	{
		Pane thePane = new Pane();
		thePane.setStyle("-fx-background-color: lavender");            //listview       button
		thePane.getChildren().addAll(c1, b1, listItems3, ActiveClients, Recipients, t2, recipients, Exit, 
				recipientsText, yourMessages, activeClientsText, refresh , refreshMsg);
		
		b1.setStyle("-fx-font: 12px Verdana; -fx-base: plum;");
		b1.setLayoutX(240);
		b1.setLayoutY(480);
		b1.setPrefSize(50, 50);
		
		c1.setStyle("-fx-font: 12px Verdana; ");
		c1.setLayoutX(30);
		c1.setLayoutY(480); 
		c1.setPrefSize(200, 50);
		
		Exit.setStyle("-fx-font: 12px Verdana; -fx-base: plum;");
		Exit.setLayoutX(530);
		Exit.setLayoutY(200);
		
		listItems3.setStyle("-fx-font: 12px Verdana; ");
		listItems3.setPrefSize(257, 410);
		listItems3.setLayoutY(60);
		listItems3.setLayoutX(30);
		
		//listview of clients in server
		ActiveClients.setStyle("-fx-font: 12px Verdana; ");
		ActiveClients.setPrefSize(200, 200);
		ActiveClients.setLayoutX(300);
		ActiveClients.setLayoutY(60);
		
		//listview
		Recipients.setStyle("-fx-font: 12px Verdana; ");
		Recipients.setPrefSize(200, 190);
		Recipients.setLayoutX(300);
		Recipients.setLayoutY(300);
		 
		//button 
		recipients.setStyle("-fx-font: 11px Verdana; -fx-base: plum;");
		recipients.setPrefSize(80, 25);
		recipients.setLayoutX(420);
		recipients.setLayoutY(500);
		
		//textfield for recip
		t2.setStyle("-fx-font: 12px Verdana; ");
		t2.setPrefSize(110, 30);
		t2.setLayoutX(300);
		t2.setLayoutY(500);
		
		//Refresh Button
		refresh.setLayoutX(540);
		refresh.setLayoutY(440);
		refresh.setStyle("-fx-font: 11px Verdana; -fx-base: plum;");
		
		//msg for refresh button
		refreshMsg.setLayoutX(510);
		refreshMsg.setLayoutY(410);
		
		//instructions for recips
		recipientsText.setStyle("-fx-font: 12px Verdana; ");
		recipientsText.setLayoutX(300);
		recipientsText.setLayoutY(282);
		
		yourMessages.setStyle("-fx-font: 12px Verdana; ");
		yourMessages.setLayoutX(100);
		yourMessages.setLayoutY(45);
		
		activeClientsText.setStyle("-fx-font: 12px Verdana; ");
		activeClientsText.setLayoutX(360);
		activeClientsText.setLayoutY(45);


		recipients.setOnAction(R->{
			String temp = t2.getText();
			
			values = temp.split(",");
			t2.clear();
			
			for (int i = 0; i < values.length; i++) 
			{
				Recipients.getItems().add("Client #"+values[i]);
			}
			
		});
		return new Scene(thePane, 655, 600);
	}
	
	
	
	
	
	
	
	
	
	
	

}
