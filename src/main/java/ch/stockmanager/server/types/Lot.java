package ch.stockmanager.server.types;

import java.util.Date;

public class Lot {
	public Date receptionDate;
	public long productCode;
	public int quantity;
	public Date expirationDate;

	@SuppressWarnings("unused")
	public Lot() {
	}

	public Lot(Date receptionDate, long productCode, int quantity, Date expirationDate) {
		this.receptionDate = receptionDate;
		this.productCode = productCode;
		this.quantity = quantity;
		this.expirationDate = expirationDate;
	}
}