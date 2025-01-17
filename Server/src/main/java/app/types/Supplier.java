package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Supplier(
        @JsonProperty("id") int id,
        @JsonProperty("nom") String name,
        @JsonProperty("email") String email,
        @JsonProperty("frequencecommande") int orderFrequency
) { }
