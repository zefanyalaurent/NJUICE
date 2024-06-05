package model;

public class CartItem {
    private int quantity;
    private String juiceName;
    private int price;
    private int totalPrice = quantity * price;
	public CartItem(int quantity, String juiceName, int price, int totalPrice) {
		super();
		this.quantity = quantity;
		this.juiceName = juiceName;
		this.price = price;
		this.totalPrice = totalPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getJuiceName() {
		return juiceName;
	}
	public void setJuiceName(String juiceName) {
		this.juiceName = juiceName;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}
