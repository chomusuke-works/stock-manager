package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record Lot (
    @JsonProperty("datereception") Date receptionDate,
    @JsonProperty("codeproduit") long productCode,
    @JsonProperty("quantite") int quantity,
    @JsonProperty("dateexpiration") Date expirationDate) { }