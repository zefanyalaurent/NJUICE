package user;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import koneksi.Connect;

public class AddNewItem extends Application implements EventHandler<ActionEvent> {
	Scene mainScene;
	Stage mainStage;

	BorderPane mainBPane;
	VBox mainVBox;
	HBox mainHBox;

	ComboBox<String> juiceBox;
	Label titleLbl, optionLbl, priceLbl, descLbl, qtyLbl, totalPriceLbl;
	Spinner<Integer> qtySpinner;
	Button addItemBtn;

	public String username;

	private Home homeReference;

	public void setHomeReference(Home homeReference) {
		this.homeReference = homeReference;
	}

	private void refreshCartItems() {
		if (homeReference != null) {
			homeReference.refreshCart();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	Connect connect = Connect.getConnection();

	Statement st;
	ResultSet rs;
	PreparedStatement ps;
	ResultSetMetaData rsm;

	public void initialize() {
		mainBPane = new BorderPane();
		mainVBox = new VBox();

		titleLbl = new Label("Add new item");
		optionLbl = new Label("Juice: ");
		priceLbl = new Label("Juice Price: ");
		descLbl = new Label("Description: ");
		qtyLbl = new Label("Quantity: ");
		totalPriceLbl = new Label("Total Price: ");

		juiceBox = new ComboBox<>();

		qtySpinner = new Spinner<>(1, 10, 1);
		qtySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
			updateTotalPrice();
		});

		addItemBtn = new Button("Add Item");

		loadJuiceNames();

		mainHBox = new HBox(10, juiceBox, priceLbl);

		mainScene = new Scene(mainBPane, 400, 400);

	}

	public void addComponent() {
		mainBPane.setTop(titleLbl);
		mainBPane.setCenter(mainVBox);
		mainVBox.getChildren().addAll(optionLbl, mainHBox, descLbl, qtySpinner, totalPriceLbl, addItemBtn);

		juiceBox.setOnAction(e -> {
			updateJuiceInfo();
		});

		mainHBox.setMaxWidth(Double.MAX_VALUE);
		mainHBox.setSpacing(5);

	}

	public void styling() {
		titleLbl.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 5px");
		titleLbl.setMaxWidth(Double.MAX_VALUE);
		titleLbl.setAlignment(Pos.CENTER);

		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.setSpacing(10);

		mainHBox.setAlignment(Pos.CENTER);

		descLbl.setAlignment(Pos.CENTER);

		descLbl.setWrapText(true);

		descLbl.setPadding(new javafx.geometry.Insets(0, 30, 0, 40));
	}

	public void addAction() {
		addItemBtn.setOnAction(e -> {
			String juiceNameSelected = juiceBox.getValue();
			if (juiceNameSelected == null || juiceNameSelected.isEmpty()) {
				showAlert("Select a juice item");
				return;
			}

			int qty = qtySpinner.getValue();
			if (qty < 1) {
				showAlert("Quantity must be at least 1");
				return;
			}

			if (username == null || username.isEmpty()) {
				showAlert("Failed to get username for the selected juice item");
			}

			String juiceID = getjuiceID(juiceNameSelected);
			if (juiceID != null) {
				if (productInCart(username, juiceID)) {
					updateItemQuantity(username, juiceID, qty);
				} else {
					insertCartItem(username, juiceID, qty);
				}
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initialize();
		addComponent();
		styling();
		addAction();

		this.mainStage = primaryStage;
		primaryStage.setTitle("Add new item");
		primaryStage.setScene(mainScene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	@Override
	public void handle(ActionEvent event) {
		updateJuiceInfo();

	}

	private void updateJuiceInfo() {
		String juiceNameSelected = juiceBox.getValue();

		if (juiceNameSelected != null && !juiceNameSelected.isEmpty()) {
			String query = "SELECT Price, JuiceDescription FROM msjuice WHERE JuiceName=?";
			try (PreparedStatement ps = connect.prepareStatement(query)) {
				ps.setString(1, juiceNameSelected);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					int juicePrice = rs.getInt("Price");
					String juiceDesc = rs.getString("JuiceDescription");

					priceLbl.setText("Price: " + juicePrice);
					descLbl.setText("Description: " + juiceDesc);

					updateTotalPrice();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private void updateTotalPrice() {
		String juiceNameSelected = juiceBox.getValue();
		if (juiceNameSelected != null && !juiceNameSelected.isEmpty()) {
			int juicePrice = getJuicePrice(juiceNameSelected);
			int qty = qtySpinner.getValue();

			int totalPrice = juicePrice * qty;

			totalPriceLbl.setText("Total Price: " + totalPrice);
		}

	}

	private void loadJuiceNames() {
		String query = "SELECT JuiceName FROM msjuice";
		try (ResultSet rs = connect.executeQuery(query)) {
			while (rs.next()) {
				String juiceName = rs.getString("JuiceName");
				juiceBox.getItems().add(juiceName);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private int getJuicePrice(String juiceName) {
		int juicePrice = 0;

		String query = "SELECT Price FROM msjuice WHERE JuiceName=?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, juiceName);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				juicePrice = rs.getInt("Price");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return juicePrice;
	}

	private String getjuiceID(String juiceName) {
		String juiceID = null;

		String query = "SELECT JuiceId FROM msjuice WHERE JuiceName=?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, juiceName);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				juiceID = rs.getString("JuiceId");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return juiceID;
	}

	private boolean productInCart(String username, String juiceID) {
		boolean inCart = false;

		String query = "SELECT * FROM cartdetail WHERE Username=? AND JuiceId =?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, username);
			ps.setString(2, juiceID);
			ResultSet rs = ps.executeQuery();

			inCart = rs.next();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return inCart;
	}

	private void updateItemQuantity(String username, String juiceID, int qty) {
		String query = "UPDATE cartdetail SET Quantity=? WHERE Username=? AND JuiceId=?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setInt(1, qty);
			ps.setString(2, this.username);
			ps.setString(3, juiceID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void insertCartItem(String username, String juiceID, int qty) {
		String query = "INSERT INTO cartdetail (Username, JuiceId, Quantity) VALUES (?, ?, ?)";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, this.username);
			ps.setString(2, juiceID);
			ps.setInt(3, qty);
			ps.executeUpdate();

			connect.commit();
			refreshCartItems();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void showAlert(String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		});

	}

}
