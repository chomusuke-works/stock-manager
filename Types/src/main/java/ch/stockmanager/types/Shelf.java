package ch.stockmanager.types;

public class Shelf {
	public int id;
	public String name;
	public boolean isStock;

	@SuppressWarnings("unused")
	public Shelf() {
	}

	public Shelf(int id, String nom, boolean estStock) {
		this.id = id;
		this.name = nom;
		this.isStock = estStock;
	}
}