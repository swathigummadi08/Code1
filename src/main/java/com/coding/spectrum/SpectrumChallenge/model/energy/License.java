package com.coding.spectrum.SpectrumChallenge.model.energy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

@Data
@Builder

public class License{

    public String uRL;
    public String name;
    @Tolerate
    public License(){

    }
}
