package com.coding.spectrum.SpectrumChallenge.model.energy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder

public class Root{
    public String agency;
    public MeasurementType measurementType;
    public List<Release> releases;
    public String version;
}
