package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Product(
	@JsonProperty("code") long code,
	@JsonProperty("name") String name,
	@JsonProperty("price") double price,
	@JsonProperty("supplierId") int supplierId) implements DataType {
}
