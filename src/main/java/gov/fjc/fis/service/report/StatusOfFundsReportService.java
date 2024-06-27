package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Fund;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.reportdata.StatusOfFundsReportData;
import gov.fjc.fis.service.*;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("fis_StatusOfFundsReportService")
public class StatusOfFundsReportService {

    @Autowired
    private FundService fundService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private DivisionAllocationService divisionAllocationService;
    @Autowired
    private ActivityProjectionService activityProjectionService;
    @Autowired
    private ActivityReimbursementService activityReimbursementService;
    @Autowired
    private ObligationService obligationService;

    public StatusOfFundsReportData generateReportData(Appropriation appropriation, int scale) {
        var reportData = new StatusOfFundsReportData(appropriation);

        reportData.setCategories(getCategoryData(appropriation, scale, true));

        return reportData;
    }

    public List<CategoryDto> getStatusOfFundsCategoryData(Appropriation appropriation, int scale) {
        return getCategoryData(appropriation, scale, false);
    }

    private List<CategoryDto> getCategoryData(Appropriation appropriation, int scale, boolean showDefaultCategories) {

        Fund oneYearFund = fundService.getAppropriationOneYearFund();
        Fund twoYearFund = fundService.getAppropriationTwoYearFund();
        var funds = fundService.getFundListForReports(twoYearFund);

        var categoryDtos = categoryService.getCategoryDtosForBfy(appropriation);

        Set<String> showCategories = showDefaultCategories ? categoryService.getStandardReportCategoryCodes() : Collections.emptySet();

        var divisions = divisionService.getDivisions(appropriation, oneYearFund);
//        var divisionDtos = divisionService.getDivisionDtos(appropriation, funds);

        var allocations = divisionAllocationService.sumAllocations(appropriation, funds);
        var obligations = obligationService.sumObligations(appropriation, funds);
        var projections = activityProjectionService.sumProjections(appropriation, funds);
        var reimbursements = activityReimbursementService.sumReimbursements(appropriation, funds);

        BigDecimal amount;

        for (var categoryDto : categoryDtos) {
            for (var division : divisions) {
                var divisionDto = divisionService.divisionToDivisionDto(division);

                categoryDto.addOneYearDivision(divisionDto);
                categoryDto.addTwoYearDivision(divisionDto);

                for (var kvEntity : allocations) {
                    if (matchDivisionAndCategory(kvEntity, divisionDto, categoryDto)) {
                        amount = kvEntity.getValue("oneyearamount");
                        amount = amount.setScale(scale, RoundingMode.HALF_UP);
                        divisionDto.setOneYearAllocations(amount);
                        categoryDto.addOneYearAllocation(amount);
                        amount = kvEntity.getValue("twoyearamount");
                        amount = amount.setScale(scale, RoundingMode.HALF_UP);
                        divisionDto.setTwoYearAllocations(amount);
                        categoryDto.addTwoYearAllocation(amount);
                    }
                }

                for (var kvEntity : projections) {
                    if (matchDivisionAndCategory(kvEntity, divisionDto, categoryDto)) {
                        amount = kvEntity.getValue("amount");
                        amount = amount.setScale(scale, RoundingMode.HALF_UP);
                        if (kvEntity.getValue("fund").equals(oneYearFund)) {
                            divisionDto.setOneYearProjections(amount);
                            categoryDto.addOneYearProjection(amount);
                        } else {
                            divisionDto.setTwoYearProjections(amount);
                            categoryDto.addTwoYearProjection(amount);
                        }
                    }
                }

                for (var kvEntity : obligations) {
                    if (matchDivisionAndCategory(kvEntity, divisionDto, categoryDto)) {
                        amount = kvEntity.getValue("amount");
                        amount = amount.setScale(scale, RoundingMode.HALF_UP);
                        if (kvEntity.getValue("fund").equals(oneYearFund)) {
                            divisionDto.setOneYearObligations(amount);
                            categoryDto.addOneYearObligation(amount);
                        } else {
                            divisionDto.setTwoYearObligations(amount);
                            categoryDto.addTwoYearObligation(amount);
                        }
                    }
                }
                for (var kvEntity : reimbursements) {
                    if (matchDivisionAndCategory(kvEntity, divisionDto, categoryDto)) {
                        amount = kvEntity.getValue("amount");
                        amount = amount.setScale(scale, RoundingMode.HALF_UP);
                        if (kvEntity.getValue("fund").equals(oneYearFund)) {
                            divisionDto.setOneYearReimbursements(amount);
                            categoryDto.addOneYearReimbursement(amount);
                        } else {
                            divisionDto.setTwoYearReimbursements(amount);
                            categoryDto.addTwoYearReimbursement(amount);
                        }
                    }
                }
            }
            categoryDto.calculateTotals();
            categoryDto.setShowOnReport(showCategories.contains(categoryDto.getMasterObjectClass()));
        }

        return categoryDtos.stream().filter(CategoryDto::showOnReport).toList();
    }

    private boolean matchDivisionAndCategory(KeyValueEntity kvEntity, DivisionDto div, CategoryDto cat) {
        return (kvEntity.getValue("divcode").equals(div.getDivisionCode()) &&
                kvEntity.getValue("moc").equals(cat.getMasterObjectClass()));
    }

}