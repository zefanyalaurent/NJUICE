package admin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import dashboard.Login;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import koneksi.Connect;
import javafx.scene.control.TextField;

public class ManageProducts extends Application {

	Scene scene;

	BorderPane bp;
	BorderPane bp2;
	GridPane gp;
	GridPane gp2;
	FlowPane fp;

	ComboBox<String> ProductIDCB;
	Spinner<Integer> ProductPriceSP;
	TextField ProductNameTF;
	TextArea ProductTA;

	Button InsertJuiceBtn;
	Button UpdatePriceBtn;
	Button RemoveJuiceBtn;

	MenuBar menuBar;
	Menu menu1;
	Menu menu2;
	MenuItem menuItem1_1;
	MenuItem menuItem1_2;
	MenuItem menuItem2_1;

	Label titleLabel;
	Label productIDLabel;
	Label priceLabel;
	Label productNameLabel;

	TableView<Products> tableProduct;

	Connect connect = Connect.getConnection();

	public void initialize() {

		gp = new GridPane();
		gp2 = new GridPane();

		bp = new BorderPane();
		bp2 = new BorderPane();

		fp = new FlowPane();

		menuBar = new MenuBar();
		menu1 = new Menu("Admins' Dashboard");
		menu2 = new Menu("Logout");

		menuItem1_1 = new MenuItem("View Transaction");
		menuItem1_2 = new MenuItem("Manage Products");

		menuItem2_1 = new MenuItem("Logout from admin");

		scene = new Scene(bp, 600, 600);

		titleLabel = new Label("Manage Products");

		ProductIDCB = new ComboBox<>();

		ProductPriceSP = new Spinner<>(10000, 100000, 10000);
		ProductNameTF = new TextField();
		ProductTA = new TextArea();

		InsertJuiceBtn = new Button("Insert Juice");
		UpdatePriceBtn = new Button("Update Price");
		RemoveJuiceBtn = new Button("Remove Juice");

		InsertJuiceBtn.setOnAction(e -> insertJuice());
		UpdatePriceBtn.setOnAction(e -> updatePrice());
		RemoveJuiceBtn.setOnAction(e -> deleteJuice());

		productIDLabel = new Label("Product ID : ");
		priceLabel = new Label("Price : ");
		productNameLabel = new Label("Product Name : ");

		tableProduct = new TableView<Products>();

		bp.setTop(menuBar);
		bp.setCenter(gp);
		gp.add(titleLabel, 0, 1);
		gp.add(tableProduct, 0, 2);
		bp.setBottom(gp2);

		gp2.add(productIDLabel, 0, 1);
		gp2.add(priceLabel, 0, 2);
		gp2.add(productNameLabel, 0, 3);
		gp2.add(ProductIDCB, 1, 1);
		gp2.add(ProductPriceSP, 1, 2);
		gp2.add(ProductNameTF, 1, 3);
		gp2.add(ProductTA, 1, 4);
		gp2.add(InsertJuiceBtn, 2, 1);
		gp2.add(UpdatePriceBtn, 2, 2);
		gp2.add(RemoveJuiceBtn, 2, 3);

	}

	public void initializeMenu() {
		menuBar.getMenus().add(menu1);
		menuBar.getMenus().add(menu2);
		menu1.getItems().add(menuItem1_1);
		menu1.getItems().add(menuItem1_2);
		menu2.getItems().add(menuItem2_1);
	}

	public void setTable() {
		TableColumn<Products, String> juiceIDCol = new TableColumn<Products, String>("Juice ID");
		juiceIDCol.setCellValueFactory(new PropertyValueFactory<>("JuiceId"));
		juiceIDCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Products, String> juiceNameCol = new TableColumn<Products, String>("Juice Name");
		juiceNameCol.setCellValueFactory(new PropertyValueFactory<>("JuiceName"));
		juiceNameCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Products, Integer> juicePriceCol = new TableColumn<Products, Integer>("Juice Price");
		juicePriceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
		juicePriceCol.setMinWidth(bp.getWidth() / 4);

		TableColumn<Products, String> juiceDescCol = new TableColumn<Products, String>("Juice Desc");
		juiceDescCol.setCellValueFactory(new PropertyValueFactory<>("JuiceDescription"));
		juiceDescCol.setMinWidth(bp.getWidth() / 4);

		tableProduct.getColumns().addAll(juiceIDCol, juiceNameCol, juicePriceCol, juiceDescCol);
	}

	public void getDataTable() {
		String query = "SELECT * FROM msjuice";
		connect.rs = connect.executeQuery(query);

		try {
			while (connect.rs.next()) {
				String JuiceId = connect.rs.getString("JuiceId");
				String JuiceName = connect.rs.getString("JuiceName");
				int Price = connect.rs.getInt("Price");
				String JuiceDescription = connect.rs.getString("JuiceDescription");

				tableProduct.getItems().add(new Products(JuiceId, JuiceName, Price, JuiceDescription));

				ProductIDCB.getItems().add(JuiceId);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refreshData() {
		tableProduct.getItems().clear();
		ProductIDCB.getItems().clear();

		getDataTable();
	}

	public void clearInputFields() {
		ProductNameTF.clear();
		ProductPriceSP.getValueFactory().setValue(10000); // Reset to default value
		ProductTA.clear();
	}

	public void insertJuice() {
		String JuiceName = ProductNameTF.getText();
		int Price = ProductPriceSP.getValue();
		String JuiceDescription = ProductTA.getText();

		if (JuiceName.isEmpty() || JuiceDescription.isEmpty() || Price < 10000) {
			return;
		} else {
			String lastIdQuery = "SELECT JuiceId FROM msjuice ORDER BY JuiceId DESC LIMIT 1";
			String newId = "JU001";
			connect.rs = connect.executeQuery(lastIdQuery);

			try {
				if (connect.rs.next()) {
					String lastId = connect.rs.getString("JuiceId");
					int num = Integer.parseInt(lastId.substring(2)) + 1;
					newId = String.format("JU%03d", num);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			String insertQuery = String.format(
					"INSERT INTO msjuice (JuiceId, JuiceName, Price, JuiceDescription) VALUES ('%s', '%s', %d, '%s')",
					newId, JuiceName, Price, JuiceDescription);

			connect.executeUpdate(insertQuery);
			refreshData();
			clearInputFields();
		}
	}

	public void updatePrice() {
		int Price = ProductPriceSP.getValue();
		String JuiceId = ProductIDCB.getValue();

		if (JuiceId == null) {
			return;
		}

		if (Price < 10000) {
			return;
		}

		String updateQuery = "UPDATE msjuice SET Price = " + Price + " WHERE JuiceId = '" + JuiceId + "'";
		connect.executeUpdate(updateQuery);

		refreshData();
	}

	public void deleteJuice() {
		String JuiceId = ProductIDCB.getValue();

		if (JuiceId == null) {
			return;
		}

		String deleteQuery = "DELETE FROM msjuice WHERE JuiceId = '" + JuiceId + "'";
		connect.executeUpdate(deleteQuery);

		refreshData();
	}

	public void changePage(Stage stage) {

		menuItem1_1.setOnAction(e -> {
			Stage curr = (Stage) menuBar.getScene().getWindow();
			curr.close();
			ViewTransactions vt = new ViewTransactions();

			try {
				vt.start(stage);
			} catch (Exception e2) {
				e2.printStackTrace();
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

	@Override
	public void start(Stage arg0) throws Exception {
		initialize();
		initializeMenu();
		setTable();
		getDataTable();
		changePage(arg0);
		arg0.setTitle("Admin's Dashboard");
		arg0.setScene(scene);
		arg0.show();
		arg0.setResizable(false);

	}

}
