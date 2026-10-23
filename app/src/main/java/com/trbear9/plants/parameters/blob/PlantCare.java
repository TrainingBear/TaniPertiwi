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
    public String watering = "Belum Diketahui";
    public String pruning = "Belum Diketahui";
    public String fertilization = "Belum Diketahui";
    public String sunlight = "Belum Diketahui";
    @JsonProperty("pest_disease_management") public String pestDiseaseManagement = "Belum Diketahui";

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

