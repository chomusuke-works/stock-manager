package app.types;

public record Product(long code, String name, double price, int supplierId) implements DataType {
}
