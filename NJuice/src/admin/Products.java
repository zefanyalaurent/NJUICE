package admin;

public class Products {

	private String JuiceId;
	private String JuiceName;
	private int Price;
	private String JuiceDescription;
	public Products(String juiceId, String juiceName, int price, String juiceDescription) {
		super();
		JuiceId = juiceId;
		JuiceName = juiceName;
		Price = price;
		JuiceDescription = juiceDescription;
	}
	public String getJuiceId() {
		return JuiceId;
	}
	public void setJuiceId(String juiceId) {
		JuiceId = juiceId;
	}
	public String getJuiceName() {
		return JuiceName;
	}
	public void setJuiceName(String juiceName) {
		JuiceName = juiceName;
	}
	public int getPrice() {
		return Price;
	}
	public void setPrice(int price) {
		Price = price;
	}
	public String getJuiceDescription() {
		return JuiceDescription;
	}
	public void setJuiceDescription(String juiceDescription) {
		JuiceDescription = juiceDescription;
	}
	
	
	
}