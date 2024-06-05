package dashboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import admin.ViewTransactions;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import user.Home;
import koneksi.Connect;
import model.Account;

public class Login extends Application implements EventHandler<ActionEvent> {

	BorderPane borderContainer;
	MenuBar landingPage;
	Menu dashboardMenu;
	MenuItem login, register;
	VBox vb;
	Scene scene;
	GridPane formContainer;

	Label titleLbl, nJuiceLbl, usernameLbl, passwordLbl, errorLbl;
	TextField usernameTf;
	PasswordField passwordPf;
	Button loginBtn;

	Connection con;
	Statement state;
	String sql;
	ResultSet rs;
	Connect connect = Connect.getConnection();

	public void init() {
		borderContainer = new BorderPane();
		scene = new Scene(borderContainer, 600, 600);
		vb = new VBox();
		formContainer = new GridPane();
		landingPage = new MenuBar();
		dashboardMenu = new Menu("Dashboard");
		login = new MenuItem("Login");
		register = new MenuItem("Register");

		titleLbl = new Label("Login");
		nJuiceLbl = new Label("NJuice");

		usernameLbl = new Label("Username");
		usernameTf = new TextField();
		usernameTf.setPromptText("Enter Username..");

		passwordLbl = new Label("Password");
		passwordPf = new PasswordField();
		passwordPf.setPromptText("Enter Password..");

		errorLbl = new Label();

		loginBtn = new Button("Login");
	}

	public void position() {
		landingPage.getMenus().add(dashboardMenu);
		dashboardMenu.getItems().add(login);
		dashboardMenu.getItems().add(register);
		
		vb.getChildren().addAll(titleLbl, nJuiceLbl, formContainer, loginBtn);
		vb.setAlignment(Pos.CENTER);

		borderContainer.setTop(landingPage);
		borderContainer.setCenter(vb);

		formContainer.setAlignment(Pos.CENTER);
		formContainer.setPadding(new Insets(20));
		formContainer.setVgap(10);

		formContainer.add(titleLbl, 0, 0, 2, 1);
		formContainer.add(nJuiceLbl, 0, 1, 2, 1);

		formContainer.add(usernameLbl, 0, 2);
		formContainer.add(usernameTf, 1, 2);

		formContainer.add(passwordLbl, 0, 3);
		formContainer.add(passwordPf, 1, 3);

		formContainer.add(errorLbl, 0, 4, 2, 1);

		formContainer.add(loginBtn, 0, 5, 2, 1);

		titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 32px; -fx-alignment: center;");
		titleLbl.setAlignment(Pos.CENTER);
		nJuiceLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-alignment: center;");
		nJuiceLbl.setAlignment(Pos.CENTER);
		errorLbl.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		usernameTf.setPrefWidth(300);
		passwordPf.setPrefWidth(300);
		loginBtn.setPadding(new Insets(5));
		loginBtn.setPrefWidth(80);

	}

	public void addAction() {
		register.setOnAction(this);
		login.setOnAction(this);
		loginBtn.setOnAction(this);
	}

	public Login() {
		init();
		position();
		addAction();
	}

	@Override
	public void start(Stage arg0) throws Exception {

		arg0.setTitle("Login");
		arg0.setScene(scene);
		arg0.show();

	}

	@Override
	public void handle(ActionEvent arg0) {
		String username = usernameTf.getText();
		String password = passwordPf.getText();
		
		if (arg0.getSource() == register) {
			Account account = new Account(username, password, "Customer");
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

		if (arg0.getSource() == loginBtn) {
			if (!username.isEmpty() && !password.isEmpty()) {
				try {
					String sql = "SELECT * FROM msuser WHERE Username='" + username + "' AND Password='" + password
							+ "'";
					ResultSet rs = connect.executeQuery(sql);

					if (rs.next()) {
						String role = rs.getString("Role");

						Stage curr = (Stage) landingPage.getScene().getWindow();
						curr.close();

						Stage next = new Stage();

						if ("Admin".equals(role)) {
							try {
								new ViewTransactions().start(next);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								String getUsername = rs.getString("Username");
						        new Home(getUsername).start(next);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						errorLbl.setText("Credentials Failed!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					errorLbl.setText("Credentials Failed!");
				}
			} else {
				errorLbl.setText("Credentials Failed!");
			}
		}
	}

}
