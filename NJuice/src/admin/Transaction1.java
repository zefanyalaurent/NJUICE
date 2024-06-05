package admin;

public class Transaction1 {

	private String TransactionId;
	private String PaymentType;
	private String Username;
	public Transaction1(String transactionId, String paymentType, String username) {
		super();
		TransactionId = transactionId;
		PaymentType = paymentType;
		Username = username;
	}
	public String getTransactionId() {
		return TransactionId;
	}
	public void setTransactionId(String transactionId) {
		TransactionId = transactionId;
	}
	public String getPaymentType() {
		return PaymentType;
	}
	public void setPaymentType(String paymentType) {
		PaymentType = paymentType;
	}
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	
	
}