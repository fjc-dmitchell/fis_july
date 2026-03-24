package gov.fjc.fis.service;

import gov.fjc.fis.entity.*;
import io.jmix.core.DataManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service to create new budget fiscal year and associated entities with skeleton data in FIS 2.1.
 * Only non-Foundation divisions, branches, groups, and activities are included.
 * Activity criteria requested by Mark Hannan and Mary Greiner in April 1999.
 * Unlike FIS 1.0, no empty division allocations or activity projections are created
 * to ensure the new audit function includes the initial creation.
 *
 * @author Doug Mitchell
 * @version 2.1
 * @since 2.1
 */
@Component("fis_NewFiscalYearService")
public class NewFiscalYearService {

    private final DataManager dataManager;
    private final GroupService groupService;
    private final BranchService branchService;
    private final DivisionService divisionService;
    private final FundService fundService;

    public NewFiscalYearService(DataManager dataManager, FundService fundService, DivisionService divisionService, BranchService branchService, GroupService groupService) {
        this.dataManager = dataManager;
        this.fundService = fundService;
        this.divisionService = divisionService;
        this.branchService = branchService;
        this.groupService = groupService;
    }

    /**
     * Creates new Appropriation and associated entities. If unsuccessful, rollback entire
     * transaction and throw exception to user interface.
     *
     * @param nextFiscalYear String representing the budget fiscal year to create
     */
    @Transactional
    public synchronized void createAppropriation(String nextFiscalYear) {
        if (!nextFiscalYear.equals(getNextFiscalYear())) {
            throw new DataIntegrityViolationException(
                    "The requested fiscal year is not the next fiscal year in sequence: ".concat(nextFiscalYear));
        }
        var oldAppropriation = getMaxAppropriation();

        Appropriation newAppropriation = dataManager.create(Appropriation.class);
        newAppropriation.setBudgetFiscalYear(nextFiscalYear);
        newAppropriation.setStatus(true);
        newAppropriation.setOneYearAmount(BigDecimal.ZERO);
        newAppropriation.setTwoYearAmount(BigDecimal.ZERO);
        newAppropriation.setOneYearAdjustment(BigDecimal.ZERO);
        newAppropriation.setTwoYearAdjustment(BigDecimal.ZERO);
        newAppropriation.setReimbursedAmount(BigDecimal.ZERO);
        dataManager.save(newAppropriation);

        createCategories(oldAppropriation, newAppropriation);
        createDivisions(oldAppropriation, newAppropriation);
    }

    /**
     * Increment the most recent fiscal year by one
     *
     * @return String containing the next available fiscal year
     */
    public String getNextFiscalYear() {
        var maxBudgetFiscalYear = getMaxAppropriation().getBudgetFiscalYear();
        String nextFiscalYear = null;
        try {
            Integer maxYear = Integer.parseInt(maxBudgetFiscalYear);
            Integer nextYear = maxYear + 1;
            nextFiscalYear = nextYear.toString();
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse year from".concat(maxBudgetFiscalYear));
        }
        return nextFiscalYear;
    }

    /**
     * Fetches the most recent Appropriation in FIS.
     *
     * @return Appropriation entity
//     * @throws NoResultException if Appropriation not found
     */
    private Appropriation getMaxAppropriation() {
        return dataManager.load(Appropriation.class)
                .query("SELECT a FROM fis_Appropriation a WHERE a.budgetFiscalYear = "
                        + "(SELECT MAX(e.budgetFiscalYear) FROM fis_Appropriation e)")
                .one();
    }

    /**
     * Creates budget object class categories for new fiscal year based on prior year.
     *
     * @param oldAppropriation
     * @param newAppropriation
     */
    private void createCategories(Appropriation oldAppropriation, Appropriation newAppropriation) {
        var oldCategories = oldAppropriation.getCategories();

        Category newCategory;
        for (var oldCategory : oldCategories) {
            newCategory = dataManager.create(Category.class);
            newCategory.setAppropriation(newAppropriation);
            newCategory.setMasterObjectClass(oldCategory.getMasterObjectClass());
            newCategory.setTitle(oldCategory.getTitle());
            dataManager.save(newCategory);
            createObjectClasses(oldCategory, newCategory);
        }
    }

    /**
     * Creates object classes for new category based on old category.
     *
     * @param oldCategory
     * @param newCategory
     */
    private void createObjectClasses(Category oldCategory, Category newCategory) {
        var oldObjectClasses = oldCategory.getBudgetObjectClasses();

        ObjectClass newObjectClass;
        for (var oldObjectClass : oldObjectClasses) {
            newObjectClass = dataManager.create(ObjectClass.class);
            newObjectClass.setCategory(newCategory);
            newObjectClass.setBudgetObjectClass(oldObjectClass.getBudgetObjectClass());
            newObjectClass.setTitle(oldObjectClass.getTitle());
            dataManager.save(newObjectClass);
        }
    }

    /**
     * Creates non-foundation divisions for new fiscal year based on prior year.
     *
     * @param oldAppropriation
     * @param newAppropriation
     */
    private void createDivisions(Appropriation oldAppropriation, Appropriation newAppropriation) {
        var oldDivisions = divisionService.getDivisions(oldAppropriation, false);

        Division newDivision;
        for (var oldDivision : oldDivisions) {
            newDivision = dataManager.create(Division.class);
            newDivision.setAppropriation(newAppropriation);
            newDivision.setFund(oldDivision.getFund());
            newDivision.setDivisionCode(oldDivision.getDivisionCode());
            newDivision.setTitle(oldDivision.getTitle());
            newDivision.setShortTitle(oldDivision.getShortTitle());
            newDivision.setBudgetOrg(oldDivision.getBudgetOrg());
            newDivision.setOneYearAmount(BigDecimal.ZERO);
            newDivision.setTwoYearAmount(BigDecimal.ZERO);
            dataManager.save(newDivision);
            createBranches(oldDivision, newDivision);
            createGroups(oldDivision, newDivision);
            createActivities(oldDivision, newDivision);
        }
    }

    /**
     * Creates branches for new division based on old division
     *
     * @param oldDivision
     * @param newDivision
     */
    private void createBranches(Division oldDivision, Division newDivision) {
        var oldBranches = oldDivision.getBranches();

        Branch newBranch;
        for (var oldBranch : oldBranches) {
            newBranch = dataManager.create(Branch.class);
            newBranch.setDivision(newDivision);
            newBranch.setBranchCode(oldBranch.getBranchCode());
            newBranch.setTitle(oldBranch.getTitle());
            newBranch.setSortCode(oldBranch.getSortCode());
            dataManager.save(newBranch);
        }
    }

    /**
     * Creates groups for new division based on old division
     *
     * @param oldDivision
     * @param newDivision
     */
    private void createGroups(Division oldDivision, Division newDivision) {
        var oldGroups = oldDivision.getGroups();

        Group newGroup;
        for (var oldGroup : oldGroups) {
            newGroup = dataManager.create(Group.class);
            newGroup.setDivision(newDivision);
            newGroup.setGroupCode(oldGroup.getGroupCode());
            newGroup.setTitle(oldGroup.getTitle());
            newGroup.setSortCode(oldGroup.getSortCode());
            dataManager.save(newGroup);
        }
    }

    /**
     * Creates certain activities for new division based on those in old division and
     * associates each activity with matching branches and groups of the new division.
     * For generic training activities, city and state are included. No activities
     * set to two year fund. Based on criteria provided by Mark and Mary in 1999.
     *
     * @param oldDivision
     * @param newDivision
     */
    private void createActivities(Division oldDivision, Division newDivision) {
        var oneYearFund = fundService.getAppropriationOneYearFund();
        var twoYearFund = fundService.getAppropriationTwoYearFund();
        var newBranches = branchService.getBranches(newDivision);
        var newGroups = groupService.getGroups(newDivision);
        var oldActivities = oldDivision.getActivities();

        Activity newActivity;
        for (var oldActivity : oldActivities) {
            // only copy non-training projects OR training projects ending in 00
            if (!oldActivity.getTrainingProject() ||
                    (oldActivity.getTrainingProject() && oldActivity.getActivityNumber().endsWith("00"))) {
                newActivity = dataManager.create(Activity.class);
                newActivity.setDivision(newDivision);
                if (oldActivity.getFund().equals(twoYearFund)) {
                    newActivity.setFund(oneYearFund);
                } else {
                    newActivity.setFund(oldActivity.getFund());
                }
                newActivity.setActivityNumber(oldActivity.getActivityNumber());
                newActivity.setTitle(oldActivity.getTitle());
                newActivity.setSortCode(oldActivity.getSortCode());
                newActivity.setGenericProjection(oldActivity.getGenericProjection());
                newActivity.setTrainingProject(oldActivity.getTrainingProject());
                if (oldActivity.getTrainingProject()) {
                    newActivity.setShortTitle(oldActivity.getShortTitle());
                    newActivity.setCity(oldActivity.getCity());
                    newActivity.setState(oldActivity.getState());
                    newActivity.setProgramDirector(oldActivity.getProgramDirector());
                }
                if (oldActivity.getBranch() != null) {
                    var oldBranchCode = oldActivity.getBranch().getBranchCode();
                    for (var newBranch : newBranches) {
                        if (newBranch.getBranchCode().equals(oldBranchCode)) {
                            newActivity.setBranch(newBranch);
                        }
                    }
                }
                if (oldActivity.getGroup() != null) {
                    var oldGroupCode = oldActivity.getGroup().getGroupCode();
                    for (var newGroup : newGroups) {
                        if (newGroup.getGroupCode().equals(oldGroupCode)) {
                            newActivity.setGroup(newGroup);
                        }
                    }
                }
                dataManager.save(newActivity);
            }
        }
    }
}