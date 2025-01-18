package ch.stockmanager.types;

@SuppressWarnings("unused")
public class Shelf implements Cloneable{
	public int id;
	public String name;
	public boolean isStock;

	public Shelf() {
	}

	public Shelf(int id, String nom, boolean isStock) {
		this.id = id;
		this.name = nom;
		this.isStock = isStock;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean getIsStock() {
		return isStock;
	}

	public static Shelf of(Shelf shelf) {
		return new Shelf(shelf.id, shelf.name, shelf.isStock);
	}

	@Override
	public String toString() {
		return String.format("%s, %s", name, isStock ? "stock": "storefront");
	}
}