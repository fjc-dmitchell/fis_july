package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Fund;
import gov.fjc.fis.entity.dto.DivisionDto;
import gov.fjc.fis.service.*;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static gov.fjc.fis.FisUtilities.add;

@Component("fis_DivisionBalancesReportService")
public class DivisionBalancesReportService {

    @Autowired
    protected DataManager dataManager;
    @Autowired
    private FundService fundService;
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

    public List<DivisionDto> getDivisionBalances(Appropriation appropriation) {

        var oneYearFund = fundService.getAppropriationOneYearFund();
        var twoYearFund = fundService.getAppropriationTwoYearFund();

        var divisions = divisionService.getAppropriationDivisions(appropriation);
        var divisionAllocations = divisionAllocationService.sumDivisionAllocations(divisions);
        var oneYearProjections = activityProjectionService.sumActivityProjections(divisions, oneYearFund);
        var twoYearProjections = activityProjectionService.sumActivityProjections(divisions, twoYearFund);
        var oneYearReimbursements = activityReimbursementService.sumActivityReimbursements(divisions, oneYearFund);
        var twoYearReimbursements = activityReimbursementService.sumActivityReimbursements(divisions, twoYearFund);
        var oneYearObligations = obligationService.sumObligations(divisions, oneYearFund);
        var twoYearObligations = obligationService.sumObligations(divisions, twoYearFund);

        List<DivisionDto> divisionDtos = new ArrayList<>();

        for (var division : divisionAllocations) {
            var dto = dataManager.create(DivisionDto.class);

            dto.setTitle(division.getValue("title"));

            dto.setOneYearAllocations(division.getValue("oneYearAllocations"));
            dto.setTwoYearAllocations(division.getValue("twoYearAllocations"));

            dto.setOneYearProjections(findKvEntityAmount(oneYearProjections, division, oneYearFund));
            dto.setTwoYearProjections(findKvEntityAmount(twoYearProjections, division, twoYearFund));
            dto.setOneYearReimbursements(findKvEntityAmount(oneYearReimbursements, division, oneYearFund));
            dto.setTwoYearReimbursements(findKvEntityAmount(twoYearReimbursements, division, twoYearFund));
            dto.setOneYearObligations(findKvEntityAmount(oneYearObligations, division, oneYearFund));
            dto.setTwoYearObligations(findKvEntityAmount(twoYearObligations, division, twoYearFund));

            dto.setOneYearBalance(add(dto.getOneYearAllocations(), dto.getOneYearReimbursements())
                    .subtract(add(dto.getOneYearProjections(), dto.getOneYearObligations())));
            dto.setTwoYearBalance(add(dto.getTwoYearAllocations(), dto.getTwoYearReimbursements())
                    .subtract(add(dto.getTwoYearProjections(), dto.getTwoYearObligations())));

            divisionDtos.add(dto);
        }

        return divisionDtos;
    }

    BigDecimal findKvEntityAmount(List<KeyValueEntity> kvEntities, KeyValueEntity division, Fund fund) {
        BigDecimal amount = BigDecimal.ZERO;
        for (var kvEntity : kvEntities) {
            if (kvEntity.getValue("division").equals(division.getValue("division"))
                    && kvEntity.getValue("fund").equals(fund)) {
                amount = kvEntity.getValue("amount");
            }
        }
        return amount;
    }
}