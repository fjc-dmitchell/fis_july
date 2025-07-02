package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.dto.JitfDto;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component("fis_JitfService")
public class JitfService {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;

    public List<Appropriation> getAppropriations() {
        var minYear = dataManager.loadValue(
                        "SELECT MIN(a.budgetFiscalYear) FROM fis_Appropriation a"
                                + " WHERE a IN (SELECT j.appropriation FROM fis_Jitf j)", String.class)
                .optional().orElse(null);

        return dataManager.load(Appropriation.class)
                .query("SELECT e FROM fis_Appropriation e"
                        + " WHERE e.budgetFiscalYear >= :year"
                        + " ORDER BY e.budgetFiscalYear ASC")
                .parameter("year", minYear)
                .list();
    }

    public BigDecimal getJitfAmount(Appropriation appropriation) {
        return dataManager.loadValue(
                        "SELECT coalesce(sum(j.amount),0) from fis_Jitf j WHERE j.appropriation = :appropriation",
                        BigDecimal.class)
                .parameter("appropriation", appropriation)
                .one();
    }

    public BigDecimal getJitfExpenses(Appropriation appropriation) {
        var fund = fundService.getJitfFund();
        return dataManager.loadValue(
                        "SELECT coalesce(sum(o.amount),0)"
                                + " FROM fis_Obligation o"
                                + " INNER JOIN fis_Activity a ON a=o.activity"
                                + " INNER JOIN fis_Division d ON d=a.division"
                                + " WHERE a.fund = :fund AND d.appropriation = :appropriation", BigDecimal.class)
                .parameter("appropriation", appropriation)
                .parameter("fund", fund)
                .one();
    }

    public List<JitfDto> generateReport() {
        var appropriations = getAppropriations();

        List<JitfDto> jitfDtos = new ArrayList<>();
        JitfDto dto;

        BigDecimal balance = BigDecimal.ZERO;

        for (Appropriation appropriation : appropriations) {
            dto = dataManager.create(JitfDto.class);
            dto.setBudgetFiscalYear(appropriation.getBudgetFiscalYear());
            dto.setCarriedForward(balance);

            var deposits = getJitfAmount(appropriation);
            var expenses = getJitfExpenses(appropriation);
            balance = balance.add(deposits).subtract(expenses);

            dto.setTotalDeposits(deposits);
            dto.setTotalExpenses(expenses);
            dto.setCarryForward(balance);
            jitfDtos.add(dto);
        }
        return jitfDtos;
    }
}