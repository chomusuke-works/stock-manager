package app.types;

public record Product(int code, String name, double price, String category) implements DataType {
}
