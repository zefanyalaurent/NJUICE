package user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import dashboard.Login;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import koneksi.Connect;
import model.CartItem;

public class Home extends Application implements EventHandler<ActionEvent> {

	String username;

	BorderPane bp;
	GridPane gp;
	ToolBar navbar;
	Scene scene;

	Button logoutBtn;
	Button newItemBtn;
	Button deleteItemBtn;
	Button checkoutBtn;

	Label noItemLbl, mainTitle, usernameLbl, totalPriceLbl;

	ListView<String> listView;

	Connect connect = Connect.getConnection();

	public void init() {
		bp = new BorderPane();
		gp = new GridPane();
		navbar = new ToolBar();
		scene = new Scene(bp, 600, 600);

		usernameLbl = new Label("Hi, " + username);

		noItemLbl = new Label();
		mainTitle = new Label("Your cart");

		logoutBtn = new Button("Logout");
		newItemBtn = new Button("Add new Item to Cart");
		deleteItemBtn = new Button("Delete Item from Cart");
		checkoutBtn = new Button("Checkout");

		listView = new ListView<>();
	}

	public void position() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		navbar.getItems().addAll(logoutBtn, spacer, usernameLbl);
		mainTitle.setAlignment(Pos.CENTER);

		gp.setAlignment(Pos.CENTER);
		gp.add(mainTitle, 0, 0, 2, 1);
		gp.add(noItemLbl, 0, 1, 2, 1);
		gp.add(newItemBtn, 0, 4);
		gp.add(deleteItemBtn, 1, 4);
		gp.add(checkoutBtn, 2, 4);

		newItemBtn.setPadding(new Insets(10));
		deleteItemBtn.setPadding(new Insets(10));
		checkoutBtn.setPadding(new Insets(10));

		mainTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 32px;");
		noItemLbl.setStyle("-fx-font-size: 10px;");

		bp.setTop(navbar);
		bp.setCenter(gp);
	}

	public void getCart() {
		try {
			int totalPrice = 0;
			String sql = "SELECT * FROM cartdetail cd JOIN msjuice mj ON cd.JuiceId = mj.JuiceId"
					+ " WHERE cd.Username = '" + username + "'";
			connect.rs = connect.executeQuery(sql);
					
			if (!connect.rs.next()) {
//				listView.setVisible(false);
	            noItemLbl.setText("Your cart is empty, try adding items!");
	        } else {
	            do {   	
	                int quantity = connect.rs.getInt("Quantity");
	                String juiceName = connect.rs.getString("JuiceName");
	                int price = connect.rs.getInt("Price") * quantity;
	                totalPrice += price;

	                String item = String.format("%dx %s - [Rp. %d]", quantity, juiceName, price);
	                listView.getItems().add(item);
	            } while (connect.rs.next());
	            totalPriceLbl = new Label("Total Price : " + totalPrice);
	            gp.add(listView, 0, 2, 3, 1);
	            gp.add(totalPriceLbl, 0, 3, 3, 1);
	            listView.setVisible(true);
	            totalPriceLbl.setVisible(true);
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteItemFromCart() {
	    int index = listView.getSelectionModel().getSelectedIndex();

	    if (index < 0) {
	        Alert alert = new Alert(Alert.AlertType.WARNING);
	        alert.setTitle("Error");
	        alert.setHeaderText("Error");
	        alert.setContentText("Please choose which juice to delete");
	        alert.showAndWait();
	        return;
	    }

	    String item = listView.getSelectionModel().getSelectedItem();
	    Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmDelete.setTitle("Error");
	    confirmDelete.setHeaderText("Delete Item");
	    confirmDelete.setContentText("Are you sure you want to delete this item?\n" + item);

	    Optional<ButtonType> result = confirmDelete.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
			String[] itemDetails = item.split(" - ");
			String juiceName = itemDetails[0].split("x ")[1];
			String deleteQuery = "DELETE FROM cartdetail WHERE JuiceId IN (SELECT "
					+ "JuiceId FROM msjuice WHERE JuiceName = '" + juiceName + "') AND Username = '" + username + "'";
			connect.executeUpdate(deleteQuery);

			listView.getItems().remove(index);

			Alert itemDeleted = new Alert(Alert.AlertType.INFORMATION);
			itemDeleted.setTitle("Success");
			itemDeleted.setHeaderText("Item Deleted");
			itemDeleted.setContentText("Selected item has been successfully deleted!");
			itemDeleted.showAndWait();
	    }
	}
	
	public void openAddNewItem() {
	    Stage addNewItemStage = new Stage();
	    AddNewItem addNewItem = new AddNewItem();
	    addNewItem.setUsername(username);
	    addNewItem.setHomeReference(this);
	    try {
	        addNewItem.start(addNewItemStage);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    addNewItemStage.setOnCloseRequest(event -> refreshCart());
	}

	public void addAction() {
		logoutBtn.setOnAction(this);
		newItemBtn.setOnAction(e -> {
			Stage next = new Stage();
		    AddNewItem addNewItem = new AddNewItem();
		    addNewItem.setUsername(username);
		    addNewItem.setHomeReference(this);
	        try {
	            addNewItem.start(next);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    });
		checkoutBtn.setOnAction(e->{
			Checkout checkout = new Checkout();
			checkout.setHomeReference(this);
			checkout.setUsername(username);
			checkout.initialize();
			Stage next = new Stage();;
	        try {
				checkout.start(next);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		deleteItemBtn.setOnAction(e->deleteItemFromCart());
		
	}
	
	public void clearCartItems() {
        listView.getItems().clear();
        totalPriceLbl.setText("Total Price : 0");
    }
	
	public void refreshCart() {
	    listView.getItems().clear();
	    getCart();
	}

	public Home(String username) {
		this.username = username;
	}

	@Override
	public void start(Stage arg0) throws Exception {
		init();
		position();
		addAction();
		getCart();
		arg0.setTitle("NJuice");
		arg0.setScene(scene);
		arg0.show();
	}

	@Override
	public void handle(ActionEvent arg0) {
		if (arg0.getSource() == logoutBtn) {
			Stage curr = (Stage) navbar.getScene().getWindow();
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
