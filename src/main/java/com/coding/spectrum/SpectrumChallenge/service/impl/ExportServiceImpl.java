package com.coding.spectrum.SpectrumChallenge.service.impl;

import com.coding.spectrum.SpectrumChallenge.Constants;
import com.coding.spectrum.SpectrumChallenge.model.SpectrumDetails;
import com.coding.spectrum.SpectrumChallenge.service.ExportService;
import com.coding.spectrum.SpectrumChallenge.utilities.ExportUtility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {


    @Override
    public List<SpectrumDetails> exportJson(String field, int sortType) {
        JSONObject data = new ExportUtility().readExportData();
        return populateData(field, sortType, data);
    }

    public List<SpectrumDetails> getFilteredData(String field, int sortType, JSONObject data) {
        return populateData(field, sortType, data);
    }


    @Override
    public void exportCSV(String field, int sortType, PrintWriter printWriter) {
        //Read data
        JSONObject jsonObject = new ExportUtility().readExportData();
        Collection<SpectrumDetails> data = populateData(field, sortType,jsonObject);

        printWriter.append(Constants.CSV_HEADER);
        printWriter.append(Constants.LINE_SEPARATOR);

        data.stream().forEach(org -> {
            printWriter.append(org.getOrganization());
            printWriter.append(Constants.COMMA_DELIMITER);
            printWriter.append(String.valueOf(org.getRelease_count()));
            printWriter.append(Constants.COMMA_DELIMITER);
            printWriter.append(String.valueOf(org.getTotal_labor_hours()));
            printWriter.append(Constants.COMMA_DELIMITER);
            printWriter.append(String.valueOf(org.isAll_in_production()));
            printWriter.append(Constants.COMMA_DELIMITER);
            printWriter.append(org.getLicenses().stream().collect(Collectors.joining(" | ")));
            printWriter.append(Constants.COMMA_DELIMITER);
            printWriter.append(org.getMost_active_months().stream().map(String::valueOf).collect(Collectors.joining(" | ")));
            printWriter.append(Constants.LINE_SEPARATOR);
        });
    }


    private List<SpectrumDetails> populateData(String field, int sortType, JSONObject data) {


        Map<String, List<LocalDate>> dates = new HashMap<>();
        Map<String, SpectrumDetails> result = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JSONArray releases = data.getJSONArray("releases");
        releases.forEach(release -> {
            JSONObject organization = (JSONObject) release;
            SpectrumDetails spectrumDetails = new SpectrumDetails();
            spectrumDetails.setOrganization(organization.getString("organization").trim());
            spectrumDetails.setAll_in_production("Production".equalsIgnoreCase(organization.getString("status")));
            spectrumDetails.setTotal_labor_hours(organization.getFloat("laborHours"));
            spectrumDetails.setRelease_count(1);
            organization.getJSONObject("permissions").getJSONArray("licenses").forEach(license -> {
                JSONObject organizationLicense = (JSONObject) license;
                spectrumDetails.getLicenses().add(organizationLicense.getString("name"));
            });

            if (result.containsKey(spectrumDetails.getOrganization())) {
                SpectrumDetails existingSpectrumDetails = result.get(spectrumDetails.getOrganization());
                existingSpectrumDetails.setRelease_count(existingSpectrumDetails.getRelease_count() + 1);
                existingSpectrumDetails.setTotal_labor_hours(existingSpectrumDetails.getTotal_labor_hours() + spectrumDetails.getTotal_labor_hours());
                existingSpectrumDetails.getLicenses().addAll(spectrumDetails.getLicenses());
                if (!spectrumDetails.isAll_in_production()) {
                    existingSpectrumDetails.setAll_in_production(false);
                }
                List<LocalDate> existingDates = new ArrayList<>(dates.get(spectrumDetails.getOrganization()));
                existingDates.add(LocalDate.parse(organization.getJSONObject("date").getString("created"), formatter));
                result.put(spectrumDetails.getOrganization(), existingSpectrumDetails);
                dates.put(spectrumDetails.getOrganization(), existingDates);
            } else {
                result.put(spectrumDetails.getOrganization(), spectrumDetails);
                dates.put(spectrumDetails.getOrganization(), Arrays.asList(LocalDate.parse(organization.getJSONObject("date").getString("created"), formatter)));
            }
        });

        populateMostActiveMonth(dates, result.values());
        return sortData(field, sortType, result.values());
    }

    private void populateMostActiveMonth(Map<String, List<LocalDate>> dates, Collection<SpectrumDetails> result) {
        result.stream().forEach(org -> {
            org.getMost_active_months().addAll(dates.get(org.getOrganization()).stream()
                    .map(LocalDate::getMonthValue)
                    .collect(Collectors.toList())
                    .stream()
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparing(Map.Entry<Integer, Long>::getValue).reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()));
        });
    }

    public List<SpectrumDetails> sortData(String field, int sortType, Collection<SpectrumDetails> result) {
        List<SpectrumDetails> sortedResult;
        switch (field) {
            case "organization":
                if (sortType == 0) {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getOrganization))
                            .collect(Collectors.toList());
                } else {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getOrganization).reversed())
                            .collect(Collectors.toList());
                }
                break;
            case "release":
                if (sortType == 0) {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getRelease_count))
                            .collect(Collectors.toList());
                } else {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getRelease_count).reversed())
                            .collect(Collectors.toList());
                }
                break;
            case "labor":
                if (sortType == 0) {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getTotal_labor_hours))
                            .collect(Collectors.toList());
                } else {
                    sortedResult = result.stream()
                            .sorted(Comparator.comparing(SpectrumDetails::getTotal_labor_hours).reversed())
                            .collect(Collectors.toList());
                }
                break;
            default:
                sortedResult = new ArrayList<>(result);
                break;
        }
        return sortedResult;
    }
}
