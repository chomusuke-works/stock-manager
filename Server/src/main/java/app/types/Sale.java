package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record Sale(
	@JsonProperty("date") Date date,
	@JsonProperty("code") long code,
	@JsonProperty("sold") int sold,
	@JsonProperty("thrown") int thrown
) {
}
