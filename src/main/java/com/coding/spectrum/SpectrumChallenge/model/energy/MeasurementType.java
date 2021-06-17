package com.coding.spectrum.SpectrumChallenge.model.energy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder

public class MeasurementType{
    public String ifOther;
    public String method;
}

