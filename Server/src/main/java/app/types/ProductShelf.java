package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductShelf(
        @JsonProperty("codeproduit") long code,
        @JsonProperty("idetagere") int id) { }
