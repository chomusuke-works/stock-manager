package app.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductYearSegment (
    @JsonProperty("codeproduit") long productCode,
    @JsonProperty("idsegment") int segmentId,
    @JsonProperty("cible") int target,
    @JsonProperty("seuil") int thresold) {  }
