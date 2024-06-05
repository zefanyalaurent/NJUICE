package main;
import dashboard.Login;
import dashboard.Register;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {
	
	BorderPane container;
	MenuBar landingPage;
	Menu dashboardMenu;
	MenuItem login, register;
	Scene scene;
	
	public void init() {
		
		container = new BorderPane();
		scene = new Scene(container, 600, 600);
		landingPage = new MenuBar();
		dashboardMenu = new Menu("Dashboard");
		login = new MenuItem("Login");
		register = new MenuItem("Register");
		
	}
	
	public void position() {
		
		landingPage.getMenus().add(dashboardMenu);
	    dashboardMenu.getItems().add(login);
	    dashboardMenu.getItems().add(register);

	    addAction();

	    container.setTop(landingPage);
		
	}
	
	public void addAction() {
    	register.setOnAction(this);
    	login.setOnAction(this);
    }

	public static void main(String[] args) {

		launch(args);
		
	}
	
	@Override
    public void start(Stage primaryStage) {
        init();
        position();
        addAction();
        
        primaryStage.setTitle("NJuice");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	@Override
	public void handle(ActionEvent arg0) {
		
		if(arg0.getSource() == register) {
			Stage curr = (Stage) landingPage.getScene().getWindow();
            curr.close();
			
			Stage next = new Stage();
			try {
				new Register().start(next);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		
		if (arg0.getSource() == login) {
			Stage curr = (Stage) landingPage.getScene().getWindow();
            curr.close();
			
			Stage next = new Stage();
			try {
				new Login().start(next);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}

}
