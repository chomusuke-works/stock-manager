package app.types;

public record Product(int code, String name, double price, int supplierId) implements DataType {
}
