package dashboard;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import koneksi.Connect;
import model.Account;

public class Register extends Application implements EventHandler<ActionEvent> {

	BorderPane container;
	MenuBar landingPage;
	Menu dashboardMenu;
	MenuItem login, register;
	Scene scene;
	GridPane formContainer;

	TextField usernameTxt;
	PasswordField passwordTxt;
	CheckBox check;

	Label title, njuice, username, password, errorFill, errorEmpty, errorUser;

	Button registerBtn;

	Connect connect = Connect.getConnection();

	public void init() {

		container = new BorderPane();
		scene = new Scene(container, 600, 600);
		landingPage = new MenuBar();
		dashboardMenu = new Menu("Dashboard");
		login = new MenuItem("Login");
		register = new MenuItem("Register");

		usernameTxt = new TextField();
		usernameTxt.setPromptText("Enter new unique username..");
		passwordTxt = new PasswordField();
		passwordTxt.setPromptText("Enter new password..");

		check = new CheckBox("I agree to the terms and conditions of NJuice");

		title = new Label("Register");
		njuice = new Label("NJuice");
		username = new Label("Username");
		password = new Label("Password");

		errorFill = new Label();
		errorEmpty = new Label();
		errorUser = new Label();

		registerBtn = new Button("Register");

		formContainer = new GridPane();

	}

	public void position() {
		landingPage.getMenus().add(dashboardMenu);
		dashboardMenu.getItems().add(login);
		dashboardMenu.getItems().add(register);

		container.setTop(landingPage);
		container.setCenter(formContainer);
		title.setAlignment(Pos.CENTER);
		njuice.setAlignment(Pos.CENTER);

		title.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 32px;");
		njuice.setStyle("-fx-font-weight: bold;" + "-fx-font-size: 16px;");
		errorFill.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		errorEmpty.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		errorUser.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		formContainer.setAlignment(Pos.CENTER);

		formContainer.add(title, 0, 0);
		formContainer.add(njuice, 0, 1);

		formContainer.add(username, 0, 2);
		formContainer.add(usernameTxt, 0, 3);

		formContainer.add(password, 0, 4);
		formContainer.add(passwordTxt, 0, 5);

		formContainer.add(check, 0, 6, 2, 1);
		formContainer.add(errorFill, 0, 7);
		formContainer.add(errorEmpty, 0, 7);
		formContainer.add(errorUser, 0, 7);
		formContainer.add(registerBtn, 0, 8);

		usernameTxt.setPrefWidth(300);
		passwordTxt.setPrefWidth(300);
		registerBtn.setPadding(new Insets(5));
		registerBtn.setPrefWidth(80);

		formContainer.setVgap(15);
		formContainer.setHgap(10);

	}

	Boolean unique(String username) {
		try {
	        String sql = "SELECT * FROM msuser WHERE Username = '" + username + "'";
	        ResultSet resultSet = connect.executeQuery(sql);

	        return !resultSet.next();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}

	public void addRegistersListener() {
		registerBtn.setOnAction(e -> {
			try {
				String username = usernameTxt.getText();
	            String password = passwordTxt.getText();

	            if (username.isEmpty() || password.isEmpty()) {
	                errorEmpty.setText("Please input all fields");
	            } else if (!check.isSelected()) {
	                errorFill.setText("Please accept the terms and conditions");
	            } else if (!unique(username)) {
	                errorUser.setText("Username is already taken");
	            } else {
	                String insert = String.format("INSERT INTO msuser VALUES('%s', '%s', '%s')",
	                        username, password, "Customer");

	                connect.executeUpdate(insert);

	                Stage curr = (Stage) landingPage.getScene().getWindow();
	                curr.close();

	                Stage next = new Stage();
	                try {
	                    new Login().start(next);
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
	            }
			} catch (Exception event) {
				errorFill.setText("Please fill the checkbox");
			}
		});
	}

	public void addAction() {
		register.setOnAction(this);
		login.setOnAction(this);
		registerBtn.setOnAction(this);
	}

	public Register() {

		init();
		position();
		addAction();
		addRegistersListener();
	}

	@Override
	public void start(Stage arg0) throws Exception {

		arg0.setTitle("Register");
		arg0.setScene(scene);
		arg0.show();

	}

	@Override
	public void handle(ActionEvent arg0) {

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
