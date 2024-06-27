package gov.fjc.fis.service;

import gov.fjc.fis.entity.Activity;
import gov.fjc.fis.entity.Appropriation;
import gov.fjc.fis.entity.Category;
import gov.fjc.fis.entity.Division;
import gov.fjc.fis.entity.dto.CategoryDto;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component("fis_CategoryService")
public class CategoryService {
    @Autowired
    private DataManager dataManager;

    final private List<String> compensationAndBenefits = Arrays.asList("11", "12", "13");

    public List<String> getCompensationAndBenefits() {
        return compensationAndBenefits;
    }

    public List<Category> getCompensationAndBenefits(Appropriation appropriation) {
        return getCompensationAndBenefits(Collections.singletonList(appropriation));
    }

    /**
     * Category Codes which should always appear on reports
     *
     * @return mutable set of strings representing moc codes
     */
    public Set<String> getStandardReportCategoryCodes() {
        return new HashSet<>(Set.of("11", "12", "21", "22", "23", "24", "25", "26", "31"));
    }
//            return Set.of("11", "12", "21", "22", "23", "24", "25", "26", "31");

    // probably don't need this
    public List<Category> getStandardReportCategoryEntities(Appropriation appropriation) {
        var categories = getStandardReportCategoryCodes();
        return dataManager.load(Category.class)
                .query("SELECT cat FROM fis_Category cat"
                        + " WHERE cat.appropriation = :appropriation"
                        + " AND cat.masterObjectClass IN :categoryCodes")
                .parameter("appropriation", appropriation)
                .parameter("categoryCodes", getStandardReportCategoryCodes())
                .list();
    }

    // don't need this - the Dtos will not match anything else!
    public List<CategoryDto> getStandardReportCategoryDtos(Appropriation appropriation) {
        return getStandardReportCategoryEntities(appropriation).stream().map(cat -> {
            var dto = dataManager.create(CategoryDto.class);
            dto.configureCategoryDto(cat);
            return dto;
        }).toList();

    }

    public List<Category> getCompensationAndBenefits(List<Appropriation> appropriations) {
        return dataManager.load(Category.class)
                .query("SELECT cat FROM fis_Category cat" +
                        " INNER JOIN fis_Appropriation app ON app = cat.appropriation" +
                        " WHERE cat.masterObjectClass in :comp_benefits" +
                        " AND app in :appropriations")
                .parameter("comp_benefits", getCompensationAndBenefits())
                .parameter("appropriations", appropriations)
                .list();
    }

    /**
     * Create list of categories from all fiscal years in selection. Category titles
     * are from most recent year containing category
     *
     * @param fiscalYears
     * @return list of categories
     */
    public List<Category> getCategorySearchList(List<Appropriation> fiscalYears) {
        List<Category> categoryList = new ArrayList<>();
        Set<String> categoryCodes = null;

        for (Appropriation year : fiscalYears) {
            List<Category> categoriesInBfyList =
                    dataManager.load(Category.class)
                            .query("SELECT c FROM fis_Category c"
                                    + " WHERE c.appropriation = :year"
                                    + " AND c.masterObjectClass NOT IN :categoryCodes")
                            .parameter("year", year)
                            .parameter("categoryCodes", categoryCodes)
                            .list();
            categoryList.addAll(categoriesInBfyList);
            categoryCodes = categoryList.stream().map(Category::getMasterObjectClass).collect(Collectors.toSet());
        }

        return categoryList.stream().sorted(Comparator.comparing(Category::getMasterObjectClass)).toList();
    }

    /**
     * Create ordered list of Categories for an Appropriation
     *
     * @param appropriation entity
     * @return List of Categories
     */
    public List<Category> getCategoriesForBfy(Appropriation appropriation) {
        return dataManager.load(Category.class)
                .query("SELECT c FROM fis_Category c"
                        + " WHERE c.appropriation = :appropriation"
                        + " ORDER BY c.masterObjectClass")
                .parameter("appropriation", appropriation)
                .list();
    }

    public List<CategoryDto> getCategoryDtosForBfy(Appropriation appropriation) {
        return getCategoriesForBfy(appropriation).stream().map(cat -> {
            var dto = dataManager.create(CategoryDto.class);
            dto.configureCategoryDto(cat);
            return dto;
        }).toList();
    }

    /**
     * used by obligation lookup screen to find categories for given appropriation year, division, and activity
     *
     * @param appropriation required
     * @param division      null allowed
     * @param activity      null allowed
     * @param foundation    boolean to indicate whether to use FJC Foundation fund
     * @return List of Categories
     */
    public List<Category> getObligationCategoriesForDivision(
            Appropriation appropriation, Division division, Activity activity, boolean foundation) {
        return dataManager.load(Category.class)
                .query("select distinct cat from fis_Category cat" +
                        " inner join fis_Appropriation app on app = cat.appropriation" +
                        " inner join fis_ObjectClass obj on obj.category = cat" +
                        " inner join fis_Obligation obl on obl.objectClass = obj" +
                        " where obj.category.appropriation = :appropriation" +
                        " and (:divisionNull = true or obl.activity.division = :division)" +
                        " and (:activityNull = true or obl.activity = :activity)" +
                        " and ((:foundation = true and obl.activity.fund.fundCode = '812300') " +
                        " or (:foundation = false and obl.activity.fund.fundCode <> '812300'))" +
                        " order by cat.masterObjectClass")
                .parameter("appropriation", appropriation)
                .parameter("divisionNull", division == null)
                .parameter("division", division)
                .parameter("activityNull", activity == null)
                .parameter("activity", activity)
                .parameter("foundation", foundation)
                .list();
    }


}