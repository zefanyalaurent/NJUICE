package user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import dashboard.Login;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import koneksi.Connect;

public class Checkout extends Application {
	Scene scene;
	BorderPane borderContainer;
	GridPane gridContainer;
	Label lblTitle, lblPayment, lblQuantity, lblPrice, lblTotalAllPrice;
	HBox paymentType, buttonContainerHB;
//	HBox itemQuantityContainer; // -- HBOX
	RadioButton rbCash, rbDebit, rbCredit;
	Button btnCancel, btnCheckout;
	VBox homeContainerVB, lblItem;
	String sql = "SELECT * FROM cartdetail JOIN msjuice ON cartdetail.juiceId = msjuice.juiceId";

	// Navigation Bar
	Menu menuBar;
	ToolBar toolBar;
	HBox toolBarContentHB;
	Button logoutButton;
	Label lblGreeting;

	Connect connect = Connect.getConnection();

	String username;

	private Home home;

	public void setHomeReference(Home home) {
		this.home = home;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private void refreshCartItems() {
		if (home != null) {
			home.refreshCart();
		}
	}

	public void initialize() {
		// Navigation Bar
		toolBar = new ToolBar();
		logoutButton = new Button("Logout");
		lblGreeting = new Label("Hi, " + home.username);

		// Halaman Checkout
		borderContainer = new BorderPane();
		gridContainer = new GridPane();
		lblTitle = new Label("Checkout");

//		lblItem = new Label("No Data!");
		lblQuantity = new Label("No Data!");
		lblPrice = new Label("No Data!");

		lblPayment = new Label("Payment Type: ");
		scene = new Scene(borderContainer, 400, 400);

		homeContainerVB = new VBox();
		buttonContainerHB = new HBox();
		lblItem = new VBox();

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		toolBar.getItems().addAll(logoutButton, spacer, lblGreeting);

		double totalPriceAllItems = 0.0;
		try {
			ResultSet resultSet = connect.executeQuery(sql);
			while (resultSet.next()) {
				String itemName = resultSet.getString("msjuice.JuiceName");
				int quantity = resultSet.getInt("cartdetail.Quantity");
				double price = resultSet.getDouble("msjuice.Price");

				// Hitung totalPrice
				double totalPrice = quantity * price;

				// Hitung totalPrice ke total harga semua item
				totalPriceAllItems += totalPrice;

				// Tampilkan informasi item dalam VBox
				VBox itemBox = new VBox(new Label(String.valueOf(quantity) + "x " + itemName + " " + "[" + quantity
						+ " x" + "Rp. " + price + ",-" + " = " + "Rp. " + totalPrice + ",-]"));
				lblTotalAllPrice = new Label("Total Price: " + "Rp. " + totalPriceAllItems + ",-");

				// Set font untuk itemBox
				Font font = Font.font("Arial", 14);
				itemBox.getChildren().forEach(label -> ((Label) label).setFont(font));

				lblItem.getChildren().add(itemBox);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Radio Button
		rbCash = new RadioButton("Cash");
		rbDebit = new RadioButton("Debit");
		rbCredit = new RadioButton("Credit");

		paymentType = new HBox(rbCash, rbDebit, rbCredit);
		paymentType.setSpacing(10);

		// Button
		btnCancel = new Button("Cancel");
		btnCheckout = new Button("Checkout");
	}

	public void setLayout() {
		// Navigation Bar
		toolBarContentHB = new HBox(logoutButton, lblGreeting);
		toolBarContentHB.setSpacing(500);

		toolBar.getItems().addAll(toolBarContentHB);

		borderContainer.setTop(toolBar);
		borderContainer.setCenter(homeContainerVB);

		lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 40px; -fx-margin-bottom: 10px;");
		lblQuantity.setFont(Font.font(15));
		lblPrice.setFont(Font.font(15));
		lblPayment.setFont(Font.font(15));
		lblTotalAllPrice.setFont(Font.font(15));
		rbCash.setFont(Font.font(15));
		rbCredit.setFont(Font.font(15));
		rbDebit.setFont(Font.font(15));

		gridContainer.add(lblItem, 0, 1);
		gridContainer.add(lblTotalAllPrice, 0, 2);
		gridContainer.add(lblPayment, 0, 3);
		gridContainer.add(paymentType, 0, 4);

		gridContainer.setHgap(10);
		gridContainer.setVgap(10);
		gridContainer.setAlignment(Pos.CENTER);

		homeContainerVB.getChildren().addAll(lblTitle, gridContainer, buttonContainerHB);
		homeContainerVB.setAlignment(Pos.CENTER);
		homeContainerVB.setSpacing(10);
		homeContainerVB.setPadding(new Insets(10));

		buttonContainerHB.getChildren().addAll(btnCancel, btnCheckout);
		buttonContainerHB.setAlignment(Pos.CENTER);
		buttonContainerHB.setSpacing(10);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		initialize();
		setLayout();
//		styling();
		setUsername(username);
		arg0.setScene(scene);
		arg0.setTitle("Checkout");
		arg0.show();
		// Event handler for Checkout button
		btnCheckout.setOnAction(e -> handleCheckout());
		arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(AlertType.ERROR, "Please Select payment type");
				Optional<ButtonType> res = alert.showAndWait();
				if (res.get() == ButtonType.CANCEL) {
					e.consume();
				}
			}
		});

		logoutButton.setOnAction(e -> {
			Stage curr = (Stage) toolBar.getScene().getWindow();
			curr.close();

			Stage next = new Stage();
			try {
				new Login().start(next);
			} catch (Exception ev) {
				// TODO Auto-generated catch block
				ev.printStackTrace();
			}

		});

	}

	private void handleCheckout() {
		// Validate payment type
		if (rbCash.isSelected() || rbDebit.isSelected() || rbCredit.isSelected()) {
			// Insert new transaction to the database
			insertTransaction();

			if (home != null) {
				home.clearCartItems();
			}

			// Display an information message
			Alert alert = new Alert(AlertType.INFORMATION, "All items checked out successfully!");
			alert.showAndWait();
			
			Stage curr = (Stage) toolBar.getScene().getWindow();
	        curr.close();
//	        home.refreshCart();
		} else {
			// Display an error message if payment type is not selected
			Alert alert = new Alert(AlertType.ERROR, "Please select a payment type");
			alert.showAndWait();
		}
	}

	private void insertTransaction() {
		String lastIdQuery = "SELECT TransactionId FROM transactiondetail ORDER BY TransactionId DESC LIMIT 1";
		String newId = "TR001";
		connect.rs = connect.executeQuery(lastIdQuery);

		try {
			if (connect.rs.next()) {
				String lastId = connect.rs.getString("TransactionId");
				int num = Integer.parseInt(lastId.substring(2)) + 1;
				newId = String.format("TR%03d", num);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String PaymentType = rbCash.isSelected() ? "Cash" : (rbDebit.isSelected() ? "Debit" : "Credit");
		// SQL query to insert transaction details
		String insertQuery = String.format(
				"INSERT INTO transactionheader (TransactionId, Username, PaymentType) " + "VALUES ('%s', '%s', '%s')",
				newId, home.username, PaymentType);

		// Execute the insert query
		connect.executeUpdate(insertQuery);
	}

}
