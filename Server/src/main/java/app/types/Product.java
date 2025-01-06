package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Product(
	@JsonProperty("code") long code,
	@JsonProperty("name") String name,
	@JsonProperty("price") double price,
	@JsonProperty("supplierId") int supplierId
) {
	public CountedProduct count(int count) {
		return new CountedProduct(this, count);
	}

	public record CountedProduct(
		Product product,
		@JsonProperty("count") int count
	) {}
}
