package com.coding.spectrum.SpectrumChallenge.model.energy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
@Builder

public class Permissions{
    public Object exemptionText;
    public List<License> licenses;
    public String usageType;
    @Tolerate
    public Permissions(){

    }
}
