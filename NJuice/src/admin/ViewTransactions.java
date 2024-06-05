package admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import dashboard.Login;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import koneksi.Connect;

public class ViewTransactions extends Application {

	Scene scene;

	BorderPane bp;
	GridPane gp;
	FlowPane fp;

	MenuBar menuBar;
	Menu menu1;
	Menu menu2;
	MenuItem menuItem1_1;
	MenuItem menuItem1_2;
	MenuItem menuItem2_1;

	Connect connect = Connect.getConnection();

	Label titleLabel;

	TableView<Transaction1> table1;

	TableView<Transaction2> table2;

	public void initialize() {

		titleLabel = new Label("View Transaction");

		gp = new GridPane();

		bp = new BorderPane();

		fp = new FlowPane();

		menuBar = new MenuBar();
		menu1 = new Menu("Admins' Dashboard");
		menu2 = new Menu("Logout");

		menuItem1_1 = new MenuItem("View Transaction");
		menuItem1_2 = new MenuItem("Manage Products");

		menuItem2_1 = new MenuItem("Logout from admin");

		scene = new Scene(bp, 600, 600);

		table1 = new TableView<Transaction1>();
		table2 = new TableView<Transaction2>();

		titleLabel.setAlignment(Pos.CENTER);
		titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px");

		bp.setTop(menuBar);
		bp.setCenter(gp);
		gp.add(titleLabel, 1, 0);
		gp.add(table1, 1, 1);
		gp.add(table2, 1, 2);

	}

	public void changePage(Stage stage) {

		menuItem1_2.setOnAction(e -> {
			Stage curr = (Stage) menuBar.getScene().getWindow();
			curr.close();
			ManageProducts mp = new ManageProducts();

			try {
				mp.start(stage);
			} catch (Exception ev) {
				ev.printStackTrace();
			}

		});

		menuItem2_1.setOnAction(e -> {
			Stage curr = (Stage) menuBar.getScene().getWindow();
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

	public void initializeMenu() {
		menuBar.getMenus().add(menu1);
		menuBar.getMenus().add(menu2);
		menu1.getItems().add(menuItem1_1);
		menu1.getItems().add(menuItem1_2);
		menu2.getItems().add(menuItem2_1);
	}

	public void setTable1() {

		TableColumn<Transaction1, String> transactionIDCol = new TableColumn<Transaction1, String>("Transaction ID");
		transactionIDCol.setCellValueFactory(new PropertyValueFactory<>("TransactionId"));
		transactionIDCol.setMinWidth(bp.getWidth() / 3);

		TableColumn<Transaction1, String> paymentTypeCol = new TableColumn<Transaction1, String>("Payment Type");
		paymentTypeCol.setCellValueFactory(new PropertyValueFactory<>("PaymentType"));
		paymentTypeCol.setMinWidth(bp.getWidth() / 3);

		TableColumn<Transaction1, String> usernameCol = new TableColumn<Transaction1, String>("Username");
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("Username"));
		usernameCol.setMinWidth(bp.getWidth() / 3);

		table1.getColumns().addAll(transactionIDCol, paymentTypeCol, usernameCol);
	}

	public void getAllDataTable1() {
		String query = "SELECT * FROM transactionheader";
		connect.rs = connect.executeQuery(query);

		try {
			while (connect.rs.next()) {
				String TransactionID = connect.rs.getString("TransactionId");
				String Username = connect.rs.getString("Username");
				String PaymentType = connect.rs.getString("PaymentType");

				table1.getItems().add(new Transaction1(TransactionID, PaymentType, Username));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setTable2() {
		TableColumn<Transaction2, String> transactionIDCol = new TableColumn<Transaction2, String>("Transaction ID");
		transactionIDCol.setCellValueFactory(new PropertyValueFactory<>("TransactionId"));
		transactionIDCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Transaction2, String> JuiceIDCol = new TableColumn<Transaction2, String>("Juice ID");
		JuiceIDCol.setCellValueFactory(new PropertyValueFactory<>("JuiceId"));
		JuiceIDCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Transaction2, String> juiceNameCol = new TableColumn<Transaction2, String>("Juice Name");
		juiceNameCol.setCellValueFactory(new PropertyValueFactory<>("JuiceName"));
		juiceNameCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Transaction2, Integer> quantityCol = new TableColumn<Transaction2, Integer>("Quantity");
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
		quantityCol.setMinWidth(bp.getWidth() / 4);

		table2.getColumns().addAll(transactionIDCol, JuiceIDCol, juiceNameCol, quantityCol);
	}

	public void showSelectedData() {
		table1.setOnMouseClicked(e -> {
			Transaction1 selectedTransaction = table1.getSelectionModel().getSelectedItem();

			if (selectedTransaction != null) {
				table2.getItems().clear();
				String selectedTransactionID = selectedTransaction.getTransactionId();

				String query = "SELECT td.TransactionId, mj.JuiceId, mj.JuiceName, td.Quantity FROM "
						+ "transactionheader th JOIN transactiondetail td ON th.transactionid = "
						+ "td.transactionid JOIN msjuice mj ON mj.juiceid = td.juiceid " + "WHERE td.TransactionId = ?";

				try {
					PreparedStatement preparedStatement = connect.prepareStatement(query);
					preparedStatement.setString(1, selectedTransactionID);
					connect.rs = preparedStatement.executeQuery();

					while (connect.rs.next()) {
						String TransactionID = connect.rs.getString("TransactionId");
						String JuiceId = connect.rs.getString("JuiceId");
						String JuiceName = connect.rs.getString("JuiceName");
						int Quantity = connect.rs.getInt("Quantity");

						table2.getItems().add(new Transaction2(TransactionID, JuiceId, JuiceName, Quantity));
					}

					preparedStatement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void start(Stage arg0) throws Exception {
		initialize();
		initializeMenu();
		setTable1();
		setTable2();
		changePage(arg0);
		getAllDataTable1();
		showSelectedData();
		arg0.setTitle("Admin");
		arg0.setScene(scene);
		arg0.show();
		arg0.setResizable(false);

		arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent e) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				Optional<ButtonType> res = alert.showAndWait();

				if (res.get() == ButtonType.CANCEL) {
					e.consume();
				}

			}

		});

	}

}
