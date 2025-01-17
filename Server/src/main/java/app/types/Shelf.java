package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Shelf(
    @JsonProperty("id") int id,
    @JsonProperty("nom") String nom,
    @JsonProperty("estStock") boolean estStock
) {

}