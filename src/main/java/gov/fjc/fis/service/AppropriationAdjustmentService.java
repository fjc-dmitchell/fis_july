package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.AppropriationAdjustment;
import gov.fjc.fis.entity.Fund;
import gov.fjc.fis.entity.dto.AmountsDto;
import gov.fjc.fis.entity.dto.CategoryDto;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static gov.fjc.fis.FisUtilities.getTotalNullAllowed;

@Component("fis_AppropriationAdjustmentService")
public class AppropriationAdjustmentService {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;

    BigDecimal sumReimbursements(Appropriation appropriation, Fund fund) {
        return dataManager.loadValue(
                        "SELECT COALESCE(SUM(r.amount),0)"
                                + " FROM fis_ActivityReimbursement r"
                                + " INNER JOIN fis_Activity act ON act=r.activity"
                                + " INNER JOIN fis_Fund fund ON fund=act.fund"
                                + " INNER JOIN fis_Division dv ON dv=act.division"
                                + " INNER JOIN fis_Appropriation app ON app=dv.appropriation"
                                + " WHERE app = :appropriation"
                                + " AND fund = :fund", BigDecimal.class)
                .parameter("appropriation", appropriation)
                .parameter("fund", fund)
                .one();
    }

    public List<AmountsDto> getAdjustments(Appropriation appropriation) {
        var oneYearFund = fundService.getAppropriationOneYearFund();
        var twoYearFund = fundService.getAppropriationTwoYearFund();

        List<AmountsDto> adjustments = new ArrayList<>();

        var dto = dataManager.create(AmountsDto.class);
        dto.setTitle("Appropriation");
        dto.setOneYearAmount(appropriation.getOneYearAmount());
        dto.setTwoYearAmount(appropriation.getTwoYearAmount());
        dto.setTotalAmount(appropriation.getTotalAmount());
        adjustments.add(dto);

        dto = dataManager.create(AmountsDto.class);
        dto.setTitle("Activity Reimbursements");
        dto.setOneYearAmount(sumReimbursements(appropriation, oneYearFund));
        dto.setTwoYearAmount(sumReimbursements(appropriation, twoYearFund));
        dto.setTotalAmount(getTotalNullAllowed(dto.getOneYearAmount(), dto.getTwoYearAmount()));
        adjustments.add(dto);

//        dto = dataManager.create(AmountsDto.class);
//        dto.setTitle("Appropriation Adjustments");
//        dto.setOneYearAmount(appropriation.getAdjustments().stream().map(AppropriationAdjustment::getOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
//        dto.setTwoYearAmount(appropriation.getAdjustments().stream().map(AppropriationAdjustment::getTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
//        dto.setTotalAmount(getTotalNullAllowed(dto.getOneYearAmount(), dto.getTwoYearAmount()));
//        adjustments.add(dto);

        dto = dataManager.create(AmountsDto.class);
        dto.setTitle("Total spending authority");
        dto.setOneYearAmount(adjustments.stream().map(AmountsDto::getOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTwoYearAmount(adjustments.stream().map(AmountsDto::getTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setTotalAmount(adjustments.stream().map(AmountsDto::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        adjustments.add(dto);

        return adjustments;
    }
}