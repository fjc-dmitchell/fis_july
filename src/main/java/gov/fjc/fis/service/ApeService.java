package gov.fjc.fis.service;

import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Branch;
import gov.fjc.fis.entity.form.Ape;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("fis_ApeService")
public class ApeService {
    public ApeService(FetchPlans fetchPlans) {
        this.fetchPlans = fetchPlans;
    }

    @Autowired
    private DataManager dataManager;
    private final FetchPlans fetchPlans;

    //                    .query("SELECT e FROM fis_Ape e"
//                        + " LEFT JOIN fis_Branch bch ON bch=e.branch"
//                        + " INNER JOIN fis_Division dv ON dv = e.division"
//                        + " INNER JOIN fis_Appropriation app ON app = dv.appropriation"
//                        + " WHERE app IN :appropriations"
//                        + " AND (:anyBranch = true OR bch.branchCode = :branchCode)"
//                        + " AND (:anyContact = true OR e.programLeader LIKE :contactName OR e.programSupport LIKE :contactName)"
//                        + " ORDER BY app.budgetFiscalYear DESC")

    public List<Ape> getApes(List<Appropriation> appropriations, Branch branch, String contact) {
        String branchCode = branch == null ? "" : branch.getBranchCode();
        String contactName = contact == null ? "" : contact;
        return dataManager.load(Ape.class)
                .query("SELECT e FROM fis_Ape e"
                        + " LEFT JOIN fis_Branch bch ON bch=e.branch"
                        + " WHERE e.division.appropriation IN :appropriations"
                        + " AND (:anyBranch = TRUE OR bch.branchCode = :branchCode)"
                        + " AND (:anyContact = TRUE OR e.programLeader LIKE :contactName OR e.programSupport LIKE :contactName)"
                        + " ORDER BY e.division.appropriation.budgetFiscalYear DESC")
                .parameter("appropriations", appropriations)
                .parameter("anyBranch", branchCode.isBlank())
                .parameter("branchCode", branchCode)
                .parameter("anyContact", contactName.isBlank())
                .parameter("contactName", "%" + contactName + "%")
                .fetchPlan("ape-search-fetch-plan")
                .list();
    }
}
//                        + " AND (:anyBranch = TRUE OR e.branch.branchCode = :branchCode)"
//                        + " AND (:anyContact = TRUE OR e.programLeader LIKE :contactName OR e.programSupport LIKE :contactName)"