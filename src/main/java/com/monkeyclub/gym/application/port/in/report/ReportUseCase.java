package com.monkeyclub.gym.application.port.in.report;

import com.monkeyclub.gym.features.report.ExpirationReportItem;
import com.monkeyclub.gym.features.report.IncomeReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportUseCase {

    IncomeReportResponse incomeReport(LocalDate from, LocalDate to);

    List<ExpirationReportItem> expirations(LocalDate from, LocalDate to);
}
