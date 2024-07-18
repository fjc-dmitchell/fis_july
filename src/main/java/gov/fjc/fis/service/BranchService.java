package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.Division;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("fis_BranchService")
public class BranchService {
    @Autowired
    private DataManager dataManager;

    public BranchService(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    private final DivisionService divisionService;

    public List<Branch> getBranchSearchList(List<Appropriation> fiscalYears, String divCode) {
        fiscalYears = fiscalYears.stream().sorted(Comparator.comparing(Appropriation::getBudgetFiscalYear).reversed()).toList();
        List<Branch> branchList = new ArrayList<>();
        Set<String> branchCodes = null;

        for (Appropriation year : fiscalYears) {
            List<Branch> branchesInBfyList = dataManager.load(Branch.class)
                    .query("SELECT e FROM fis_Branch e"
                            + " WHERE e.division.appropriation = :year"
                            + " AND e.division.divisionCode = :divCode"
                            + " AND e.branchCode NOT IN :branchCodes")
                    .parameter("year", year)
                    .parameter("divCode", divCode)
                    .parameter("branchCodes", branchCodes)
                    .list();
            branchList.addAll(branchesInBfyList);
            branchCodes = branchList.stream().map(Branch::getBranchCode).collect(Collectors.toSet());
        }

        return branchList.stream().sorted(Comparator.comparing(Branch::getBranchCode)).toList();
    }

    public List<Branch> getBranchesForDivision(Division division) {
        return dataManager.load(Branch.class)
                .query("SELECT e FROM fis_Branch e"
                        + " WHERE e.division = :division"
                        + " ORDER BY e.branchCode")
                .parameter("division", division)
                .list();
    }

}