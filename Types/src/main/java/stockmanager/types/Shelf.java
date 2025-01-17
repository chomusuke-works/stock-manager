package stockmanager.types;

public class Shelf {
	public int id;
	public String nom;
	public boolean estStock;

	public Shelf(int id, String nom, boolean estStock) {
		this.id = id;
		this.nom = nom;
		this.estStock = estStock;
	}
}