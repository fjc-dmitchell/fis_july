package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("fis_DivisionAllocationService")
public class DivisionAllocationService {
    @Autowired
    private DataManager dataManager;

    /**
     * use when setting allocations during division edit?
     *
     * @param division
     * @return
     */
    public List<Category> getAvailableCategories(Division division) {
        var appropriation = division == null ? null : division.getAppropriation();

        return dataManager.load(Category.class)
                .query("SELECT c FROM fis_Category c"
                        + " WHERE c.appropriation = :appropriation "
                        + " AND c NOT IN (SELECT a.category FROM fis_DivisionAllocation a WHERE a.division = :division)"
                        + " ORDER BY c.masterObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("division", division)
                .list();
    }

    public List<DivisionAllocation> getCategoryAllocations(Category category) {
        return dataManager.load(DivisionAllocation.class)
                .query("SELECT a FROM fis_DivisionAllocation a"
                        + " INNER JOIN fis_Division dv ON dv=a.division"
                        + " WHERE a.category = :category"
                        + " ORDER BY dv.divisionCode")
                .parameter("category", category)
                .list();
    }

    /**
     * used by status of funds. Named to match other entity sum methods. Doesn't actually sum!
     *
     * @param appropriation
     * @param funds
     * @return
     */
    public List<KeyValueEntity> sumAllocations(Appropriation appropriation, List<Fund> funds) {
        return dataManager.loadValues(
                        "SELECT cat.masterObjectClass, div.divisionCode, alloc.oneYearAmount, alloc.twoYearAmount"
                                + " FROM fis_DivisionAllocation alloc"
                                + " INNER JOIN fis_Division div ON div = alloc.division"
                                + " INNER JOIN fis_Category cat ON cat = alloc.category"
                                + " WHERE div.appropriation = :appropriation"
                                + " AND div.fund IN :funds")
                .properties("moc", "divcode", "oneyearamount", "twoyearamount")
                .parameter("appropriation", appropriation)
                .parameter("funds", funds)
                .list();
    }
}