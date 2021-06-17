package com.coding.spectrum.SpectrumChallenge.controller;


import com.coding.spectrum.SpectrumChallenge.model.SpectrumDetails;
import com.coding.spectrum.SpectrumChallenge.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
public class ExportController {

    @Autowired
    ExportService exportService;

    @GetMapping("/exportJson")
    public ResponseEntity<List<SpectrumDetails>> exportJson(@RequestParam("sortFiled") String field, @RequestParam("sortType") int sortType) {
        List<SpectrumDetails> spectrumDetails = exportService.exportJson(field, sortType);
        if (spectrumDetails == null || spectrumDetails.size() == 0) {
            return new ResponseEntity<>(spectrumDetails, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(spectrumDetails, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/exportCSV",produces = "text/csv")
    public void exportCSV(HttpServletResponse response, @RequestParam("sortFiled") String field, @RequestParam("sortType") int sortType) throws IOException, IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"Spectrum.csv\"");
        exportService.exportCSV(field, sortType,response.getWriter());
    }
}
