package com.coding.spectrum.SpectrumChallenge.service;


import com.coding.spectrum.SpectrumChallenge.model.SpectrumDetails;

import java.io.PrintWriter;
import java.util.List;

public interface ExportService {

    List<SpectrumDetails> exportJson(String field, int sortType);

    void exportCSV(String field, int sortType, PrintWriter writer);
}
