package com.trbear9.plants.parameters.blob;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PlantCare implements Serializable {
    public String watering = "Unknown";
    public String pruning = "Unknown";
    public String fertilization = "Unknown";
    public String sunlight = "Unknown";
    @JsonProperty("pest_disease_management")
    public String pestDiseaseManagement = "Unknown";

    public PlantCare() {
    }

    public PlantCare(String watering, String pruning, String fertilization, String sunlight, String pestDiseaseManagement) {
        this.watering = watering;
        this.pruning = pruning;
        this.fertilization = fertilization;
        this.sunlight = sunlight;
        this.pestDiseaseManagement = pestDiseaseManagement;
    }
}

