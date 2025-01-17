package ch.stockmanager.types;

import java.util.Date;

public class Lot {
	public Date receptionDate;
	public long productCode;
	public int quantity;
	public Date expirationDate;

	public Lot(Date receptionDate, long productCode, int quantity, Date expirationDate) {
		this.receptionDate = receptionDate;
		this.productCode = productCode;
		this.quantity = quantity;
		this.expirationDate = expirationDate;
	}
}