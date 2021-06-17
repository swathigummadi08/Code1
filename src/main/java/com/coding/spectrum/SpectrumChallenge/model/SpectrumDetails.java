package com.coding.spectrum.SpectrumChallenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class SpectrumDetails {

    private String organization;
    private int release_count;
    private float total_labor_hours;
    private boolean all_in_production = true;
    private Set<String> licenses = new HashSet<>();
    private List<Integer>  most_active_months = new ArrayList<>();
}
