package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Fund;
import gov.fjc.fis.entity.dto.CategoryDto;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.entity.dto.ObligationDto;
import gov.fjc.fis.reportdata.StatusOfFundsReportData;
import gov.fjc.fis.service.*;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component("fis_StatusOfFundsReportService")
public class StatusOfFundsReportService {

    @Autowired
    protected DataManager dataManager;
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

    public List<CategoryDto> getStatusOfFundsCategoryData(Appropriation appropriation, int scale, boolean showDefaultCategories) {
        var categoryDtos = new ArrayList<>(getCategoryData(appropriation, scale, showDefaultCategories));

        var oneYearAppropriation = appropriation.getOneYearAmount();
        var twoYearAppropriation = appropriation.getTwoYearAmount();

        var oneYearAlloc = categoryDtos.stream().map(CategoryDto::getTotalOneYearAllocations).reduce(BigDecimal.ZERO, BigDecimal::add);
        var twoYearAlloc = categoryDtos.stream().map(CategoryDto::getTotalTwoYearAllocations).reduce(BigDecimal.ZERO, BigDecimal::add);

        var unallocated = dataManager.create(CategoryDto.class);
        unallocated.setTitleAndCode("ALLOCATION DISCREPANCY");
        unallocated.setTotalOneYearAllocations(oneYearAppropriation.subtract(oneYearAlloc));
        unallocated.setTotalTwoYearAllocations(twoYearAppropriation.subtract(twoYearAlloc));
        unallocated.setShowOnReport(false);
        categoryDtos.add(unallocated);

        return categoryDtos.stream().filter(CategoryDto::getShowOnReport).toList();
    }

//    public List<CategoryDto> getStatusOfFundsCategorySpending(Appropriation appropriation, int scale) {
//        var categoryDtos = new ArrayList<>(getCategoryData(appropriation, scale, true));
//
//        var oneYearAppropriation = appropriation.getOneYearAmount();
//        var twoYearAppropriation = appropriation.getTwoYearAmount();
//
//        var oneYearAlloc = categoryDtos.stream().map(CategoryDto::getTotalOneYearAllocations).reduce(BigDecimal.ZERO, BigDecimal::add);
//        var twoYearAlloc = categoryDtos.stream().map(CategoryDto::getTotalTwoYearAllocations).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        var unallocated = dataManager.create(CategoryDto.class);
//        unallocated.setTitleAndCode("UNALLOCATED FUNDS");
//        unallocated.setTotalOneYearAllocations(oneYearAppropriation.subtract(oneYearAlloc));
//        unallocated.setTotalTwoYearAllocations(twoYearAppropriation.subtract(twoYearAlloc));
//        unallocated.setShowOnReport(false);
//        categoryDtos.add(unallocated);
//
//        return categoryDtos.stream().filter(CategoryDto::getShowOnReport).toList();
//    }

    public List<CategoryDto> getStatusOfFundsCategoryDataWithUnused(Appropriation appropriation, int scale) {
        var oneYearAppropriation = appropriation.getOneYearAmount();
        var twoYearAppropriation = appropriation.getTwoYearAmount();
        var totalAppropriation = oneYearAppropriation.add(twoYearAppropriation);

        var categoryDtos = new ArrayList<>(getCategoryData(appropriation, scale, false));

        var oneYearProj = categoryDtos.stream().map(CategoryDto::getTotalOneYearProjections).reduce(BigDecimal.ZERO, BigDecimal::add);
        var twoYearProj = categoryDtos.stream().map(CategoryDto::getTotalTwoYearProjections).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalProj = oneYearProj.add(twoYearProj);
        var oneYearOblig = categoryDtos.stream().map(CategoryDto::getTotalOneYearObligations).reduce(BigDecimal.ZERO, BigDecimal::add);
        var twoYearOblig = categoryDtos.stream().map(CategoryDto::getTotalTwoYearObligations).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalOblig = oneYearOblig.add(twoYearOblig);
        var oneYearReim = categoryDtos.stream().map(CategoryDto::getTotalOneYearReimbursements).reduce(BigDecimal.ZERO, BigDecimal::add);
        var twoYearReim = categoryDtos.stream().map(CategoryDto::getTotalTwoYearReimbursements).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalReim = oneYearReim.add(twoYearReim);

        var unused = dataManager.create(CategoryDto.class);
        unused.setTitleAndCode("UNSPENT FUNDS");
        unused.setTotalOneYearObligations(oneYearAppropriation.add(oneYearReim).subtract(oneYearProj).subtract(oneYearOblig));
        unused.setTotalTwoYearObligations(twoYearAppropriation.add(twoYearReim).subtract(twoYearProj).subtract(twoYearOblig));
        unused.setTotalObligations(totalAppropriation.add(totalReim).subtract(totalProj).subtract(totalOblig));

        unused.setShowOnReport(false);
        categoryDtos.add(unused);

        return categoryDtos.stream().filter(CategoryDto::getShowOnReport).toList();
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

        return categoryDtos.stream().filter(CategoryDto::getShowOnReport).toList();
    }

    private boolean matchDivisionAndCategory(KeyValueEntity kvEntity, DivisionDto div, CategoryDto cat) {
        return (kvEntity.getValue("divcode").equals(div.getDivisionCode()) &&
                kvEntity.getValue("moc").equals(cat.getMasterObjectClass()));
    }

}