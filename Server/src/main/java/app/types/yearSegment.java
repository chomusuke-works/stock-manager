package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record yearSegment (
    @JsonProperty("id") int id,
    @JsonProperty("nom") String name,
    @JsonProperty("datedebut") Date from,
    @JsonProperty("datefin") Date to,
    @JsonProperty("priorite") int priority
) {  }