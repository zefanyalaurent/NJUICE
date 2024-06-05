package admin;

public class Transaction2 {

	private String TransactionId;
	private String JuiceId;
	private String JuiceName;
	private int Quantity;
	public Transaction2(String transactionId, String juiceId, String juiceName, int quantity) {
		super();
		TransactionId = transactionId;
		JuiceId = juiceId;
		JuiceName = juiceName;
		Quantity = quantity;
	}
	public String getTransactionId() {
		return TransactionId;
	}
	public void setTransactionId(String transactionId) {
		TransactionId = transactionId;
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
	public int getQuantity() {
		return Quantity;
	}
	public void setQuantity(int quantity) {
		Quantity = quantity;
	}
	
	
	
	
}
