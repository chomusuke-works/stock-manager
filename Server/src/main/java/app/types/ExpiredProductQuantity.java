package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExpiredProductQuantity(
	@JsonProperty("code") long code,
	@JsonProperty("name") String name,
	@JsonProperty("quantity") int quantity
) { }
