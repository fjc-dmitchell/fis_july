package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.ObligationDto;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("fis_ObjectClassService")
public class ObjectClassService {

    @Autowired
    private DataManager dataManager;
    @Autowired
    private FundService fundService;

    public List<ObjectClass> getProjectionObjectClasses(Activity activity) {
        Appropriation appropriation = null;
        if (activity != null) {
            appropriation = activity.getDivision().getAppropriation();
        }
        return dataManager.load(ObjectClass.class)
                .query("SELECT o FROM fis_ObjectClass o"
                        + " INNER JOIN fis_Category cat ON cat=o.category"
                        + " WHERE cat.appropriation = :appropriation"
                        + " AND o NOT IN (SELECT e.objectClass FROM fis_ActivityProjection e WHERE e.activity = :activity)"
                        + " ORDER BY o.budgetObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("activity", activity)
                .list();
    }

    public List<ObjectClass> getReimbursementObjectClasses(Activity activity) {
        Appropriation appropriation = null;
        if (activity != null) {
            appropriation = activity.getDivision().getAppropriation();
        }
        return dataManager.load(ObjectClass.class)
                .query("SELECT o FROM fis_ObjectClass o"
                        + " INNER JOIN fis_Category cat ON cat=o.category"
                        + " WHERE cat.appropriation = :appropriation"
                        + " AND o NOT IN (SELECT e.objectClass FROM fis_ActivityReimbursement e WHERE e.activity = :activity)"
                        + " ORDER BY o.budgetObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("activity", activity)
                .list();
    }

    public List<ObjectClass> getObjectClassesByCategory(Category category, boolean generic) {
        return dataManager.load(ObjectClass.class)
                .query("SELECT o FROM fis_ObjectClass o"
                        + " WHERE o.category = :category"
                        + " AND ((o.budgetObjectClass NOT LIKE '%00') "
                        + " OR (:generic = true AND o.budgetObjectClass LIKE '%00'))"
                        + " ORDER BY o.budgetObjectClass")
                .parameter("category", category)
                .parameter("generic", generic)
                .list();
    }

    /**
     * used by obligation lookup screen to find categories for given appropriation year,
     * division, activity, and category
     *
     * @param appropriation required
     * @param division      null allowed
     * @param activity      null allowed
     * @param category      null allowed
     * @param foundation    boolean to indicate whether to use FJC Foundation fund
     * @return List of ObjectClasses
     */
    public List<ObjectClass> getObligationObjectClasses(Appropriation appropriation,
                                                        Division division,
                                                        Activity activity,
                                                        Category category,
                                                        boolean foundation) {
        Fund foundationFund = fundService.getFoundationFund();
        return dataManager.load(ObjectClass.class)
                .query("SELECT DISTINCT obj FROM fis_ObjectClass obj"
                        + " INNER JOIN fis_Appropriation app ON app = obj.category.appropriation"
                        + " INNER JOIN fis_Obligation obl ON obl.objectClass = obj"
                        + " WHERE obj.category.appropriation = :appropriation"
                        + " AND (:divisionNull = true OR obl.activity.division = :division)"
                        + " AND (:activityNull = true OR obl.activity = :activity)"
                        + " AND (:categoryNull = true OR obj.category = :category)"
                        + " AND ((:foundation = true AND obl.activity.fund = :foundationFund) "
                        + " OR (:foundation = false AND obl.activity.fund <> :foundationFund))"
                        + " ORDER BY obj.budgetObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("division", division)
                .parameter("divisionNull", division == null)
                .parameter("category", category)
                .parameter("categoryNull", category == null)
                .parameter("activityNull", activity == null)
                .parameter("activity", activity)
                .parameter("foundation", foundation)
                .parameter("foundationFund", foundationFund)
                .list();
    }

    public List<ObjectClass> getObjectClassSearchList(List<Appropriation> fiscalYears, String moc, boolean includeGenerics) {
        List<ObjectClass> bocList = new ArrayList<>();
        Set<String> bocCodes = null;

        for (Appropriation year : fiscalYears) {
            List<ObjectClass> objectClassesInBfyList =
                    dataManager.load(ObjectClass.class)
                            .query("select c from fis_ObjectClass c" +
                                    " where c.category.appropriation = :year" +
                                    " and c.budgetObjectClass not in :bocCodes" +
                                    " and (:generic = true or c.budgetObjectClass not like '%00')" +
                                    " and (:moc is null or c.category.masterObjectClass = :moc)")
                            .parameter("year", year)
                            .parameter("bocCodes", bocCodes)
                            .parameter("moc", moc)
                            .parameter("generic", includeGenerics)
                            .list();
            bocList.addAll(objectClassesInBfyList);
            bocCodes = bocList.stream().map(ObjectClass::getBudgetObjectClass).collect(Collectors.toSet());
        }

        return bocList.stream().sorted(Comparator.comparing(ObjectClass::getBudgetObjectClass)).toList();
    }
}