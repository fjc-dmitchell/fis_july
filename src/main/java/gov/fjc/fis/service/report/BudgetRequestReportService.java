package gov.fjc.fis.service.report;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.*;
import gov.fjc.fis.reportdata.BudgetRequestReportData;
import gov.fjc.fis.service.*;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component("fis_BudgetRequestReportService")
public class BudgetRequestReportService {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private AppropriationService appropriationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ObjectClassService objectClassService;
    @Autowired
    private DivisionService divisionService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityProjectionService activityProjectionService;
    @Autowired
    private ActivityReimbursementService activityReimbursementService;
    @Autowired
    private ObligationService obligationService;

    public BudgetRequestReportData generateReportData(Appropriation appropriation) {

        var priorAppropriation = appropriationService.getPreviousFiscalYear(appropriation);
        var reportData = new BudgetRequestReportData(appropriation, priorAppropriation);

//        var activities = activityService.getBiFiscalActivityDtos(appropriation);
        var activities = activityService.getBiFiscalActivityDtos(appropriation);
        var obligations = obligationService.getObligationDtos(appropriation, activities);
        var projections = activityProjectionService.getProjectionDtos(appropriation, activities, false);
        var reimbursements = activityReimbursementService.getReimbursementDtos(appropriation, activities);
        activityService.updateActivityAmounts(activities, obligations, projections, reimbursements);

        var objectClasses = getObjectClassDtos(appropriation, obligations, projections, reimbursements);
        // categories could be derived from object classes
        var categories = getCategoryDtos(appropriation, obligations, projections, reimbursements);
        var hillPLan = getHillPlanCategoryDtos(categories);
        var divisions = getDivisionDtos(appropriation, obligations, projections, reimbursements);

        reportData.setDivisions(divisions);
        reportData.setActivities(activities);
        reportData.setObligations(obligations);
        reportData.setProjections(projections);
        reportData.setReimbursements(reimbursements);
        reportData.setObjectClasses(objectClasses);
        reportData.setCategories(categories);
        reportData.setHillPlanCategories(hillPLan);

        return reportData;
    }

    // refactor to activity service
    public List<Integer> getActivityIdsFromDtos(List<ActivityDto> activities) {
        return activities.stream().map(ActivityDto::getId).toList();
    }

    public List<CategoryDto> getCategoryDtos(Appropriation appropriation, List<ObligationDto> obligationDtos, List<ActivityProjectionDto> activityProjectionDtos, List<ActivityReimbursementDto> activityReimbursementDtos) {
        // Nancy requested specific categories for this report
        Set<String> showCategories = Set.of("11", "12", "13", "21", "22", "23", "24", "25", "26", "31", "90", "91");

        Appropriation priorBfy = appropriationService.getPreviousFiscalYear(appropriation);

        List<Appropriation> fiscalYears = new ArrayList<>();
        fiscalYears.add(appropriation);
        fiscalYears.add(priorBfy);

        List<Category> categories = categoryService.getCategorySearchList(fiscalYears);
        List<CategoryDto> categoryDtoDtos = new ArrayList<>();

        CategoryDto uncategorized = dataManager.create(CategoryDto.class);
        uncategorized.setMasterObjectClass("00");
        uncategorized.setTitle("Uncategorized");

        CategoryDto undefinedDisbursments = dataManager.create(CategoryDto.class);
        undefinedDisbursments.setMasterObjectClass("91");
        undefinedDisbursments.setTitle("Undefined Disbursements");

        CategoryDto dto;

        for (Category cat : categories) {

            var moc = cat.getMasterObjectClass();

            if (showCategories.contains(moc)) {
                // consolidate 90 and 91 into 9100
                if (moc.equals("90") || moc.equals("91")) {
                    dto = undefinedDisbursments;
                } else {
                    dto = dataManager.create(CategoryDto.class);
                    dto.setId(cat.getId());
                    dto.setMasterObjectClass(moc);
                    dto.setTitle(cat.getTitle());
                    categoryDtoDtos.add(dto);
                }
            } else {
                dto = uncategorized;
            }

            var obligations = obligationService.getObligationDtosForMasterObjectClass(obligationDtos, moc);
            dto.addPriorYearObligated(obligations.stream().map(ObligationDto::getPriorTwoYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addPriorYearDisbursed(obligations.stream().map(ObligationDto::getPriorTwoYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentYearObligated(obligations.stream().map(ObligationDto::getCurrentOneYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentYearDisbursed(obligations.stream().map(ObligationDto::getCurrentOneYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearObligated(obligations.stream().map(ObligationDto::getCurrentTwoYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearDisbursed(obligations.stream().map(ObligationDto::getCurrentTwoYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));

            var projections = activityProjectionService.getProjectionDtosForMasterObjectClass(activityProjectionDtos, moc);
            dto.addPriorYearProjected(projections.stream().map(ActivityProjectionDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var reimbursements = activityReimbursementService.getReimbursementDtosForMasterObjectClass(activityReimbursementDtos, moc);
            dto.addPriorYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));


        }
        Collections.addAll(categoryDtoDtos, undefinedDisbursments, uncategorized);
//        categoryDtoDtos.add(undefinedDisbursments);
//        categoryDtoDtos.add(uncategorized);

        return categoryDtoDtos;
    }

    public List<ObjectClassDto> getObjectClassDtos(Appropriation appropriation, List<ObligationDto> obligationDtos, List<ActivityProjectionDto> activityProjectionDtos, List<ActivityReimbursementDto> activityReimbursementDtos) {

        Set<String> showCategories = Set.of("11", "12", "13", "21", "22", "23", "24", "25", "26", "31", "91");

        Appropriation priorBfy = appropriationService.getPreviousFiscalYear(appropriation);

        List<Appropriation> fiscalYears = new ArrayList<>();
        fiscalYears.add(appropriation);
        fiscalYears.add(priorBfy);

        List<ObjectClass> objectClasses = objectClassService.getObjectClassSearchList(fiscalYears, null, true);
        List<ObjectClassDto> objectClassDtos = new ArrayList<>();

        ObjectClassDto dto;

        for (ObjectClass obj : objectClasses) {
            var boc = obj.getBudgetObjectClass();
            dto = dataManager.create(ObjectClassDto.class);
            dto.setId(obj.getId());
            dto.setBudgetObjectClass(boc);
            dto.setTitle(obj.getTitle());

            var obligations = obligationService.getObligationDtosForBudgetObjectClass(obligationDtos, boc);
            dto.addPriorTwoYearObligated(obligations.stream().map(ObligationDto::getPriorTwoYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addPriorTwoYearDisbursed(obligations.stream().map(ObligationDto::getPriorTwoYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearObligated(obligations.stream().map(ObligationDto::getCurrentOneYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearDisbursed(obligations.stream().map(ObligationDto::getCurrentOneYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearObligated(obligations.stream().map(ObligationDto::getCurrentTwoYearObligated).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearDisbursed(obligations.stream().map(ObligationDto::getCurrentTwoYearDisbursed).reduce(BigDecimal.ZERO, BigDecimal::add));

            var projections = activityProjectionService.getProjectionDtosForBudgetObjectClass(activityProjectionDtos, boc);
            dto.addPriorTwoYearProjected(projections.stream().map(ActivityProjectionDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var reimbursements = activityReimbursementService.getReimbursementDtosForBudgetObjectClass(activityReimbursementDtos, boc);
            dto.addPriorTwoYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            dto.setShowOnReport(showCategories.contains(boc));
            objectClassDtos.add(dto);
//            dto.checkCategoryTotals();
        }
//        objectClassDtos.add(uncategorized);

        return objectClassDtos.stream().filter(ObjectClassDto::showOnReport).toList();
    }

    public List<CategoryDto> getHillPlanCategoryDtos(List<CategoryDto> categoryDtos) {
        List<CategoryDto> hillPlanDtos = new ArrayList<>();

        CategoryDto personnel = dataManager.create(CategoryDto.class);
        personnel.setTitle("Personnel Compensation & Benefits");

        CategoryDto travel = dataManager.create(CategoryDto.class);
        travel.setTitle("Travel");

        CategoryDto rent = dataManager.create(CategoryDto.class);
        rent.setTitle("Rent, Communication and Utilities");

        CategoryDto undefined = dataManager.create(CategoryDto.class);
        undefined.setTitle("Undefined Disbursements");

        CategoryDto other = dataManager.create(CategoryDto.class);
        other.setTitle("Other");

        CategoryDto hillPlanDto;

        for (CategoryDto categoryDto : categoryDtos) {
            hillPlanDto = switch (categoryDto.getMasterObjectClass()) {
                case "11", "12", "13" -> personnel;
                case "21" -> travel;
                case "23" -> rent;
                case "90", "91" -> undefined;
                default -> other;
            };

            hillPlanDto.addPriorYearProjected(categoryDto.getPriorYearProjected());
            hillPlanDto.addCurrentYearProjected(categoryDto.getOneYearProjected());
            hillPlanDto.addCurrentTwoYearProjected(categoryDto.getTwoYearProjected());

            hillPlanDto.addPriorYearReimbursed(categoryDto.getPriorYearReimbursed());
            hillPlanDto.addCurrentYearReimbursed(categoryDto.getOneYearReimbursed());
            hillPlanDto.addCurrentTwoYearReimbursed(categoryDto.getTwoYearReimbursed());

            hillPlanDto.addPriorYearObligated(categoryDto.getPriorYearObligated());
            hillPlanDto.addPriorYearDisbursed(categoryDto.getPriorYearDisbursed());
            hillPlanDto.addCurrentYearObligated(categoryDto.getOneYearObligated());
            hillPlanDto.addCurrentYearDisbursed(categoryDto.getOneYearDisbursed());
            hillPlanDto.addCurrentTwoYearObligated(categoryDto.getTwoYearObligated());
            hillPlanDto.addCurrentTwoYearDisbursed(categoryDto.getTwoYearDisbursed());
        }
        Collections.addAll(hillPlanDtos, personnel, travel, rent, undefined, other);
        return hillPlanDtos;
    }

    public List<DivisionDto> getDivisionDtos(Appropriation appropriation, List<ObligationDto> obligationDtos, List<ActivityProjectionDto> activityProjectionDtos, List<ActivityReimbursementDto> activityReimbursementDtos) {
        List<Appropriation> appropriations = new ArrayList<>();
        Appropriation priorBfy = appropriationService.getPreviousFiscalYear(appropriation);
        appropriations.add(appropriation);
        appropriations.add(priorBfy);

        List<Division> divisions = divisionService.getDivisionSearchList(appropriations, false);

        List<DivisionDto> divisionDtos = new ArrayList<>();
        DivisionDto educationAndTraining = dataManager.create(DivisionDto.class);
        educationAndTraining.setTitle("Education & Training");
        DivisionDto research = dataManager.create(DivisionDto.class);
        research.setTitle("Research");
        DivisionDto programSupport = dataManager.create(DivisionDto.class);
        programSupport.setTitle("Program Support");

        DivisionDto dto;

        for (Division div : divisions) {
            var divCode = div.getDivisionCode();
            dto = switch (divCode) {
                case "2", "3", "5" -> educationAndTraining;
                case "4" -> research;
                default -> programSupport;
            };

            var projections = activityProjectionService.getProjectionDtosForDivisionCode(activityProjectionDtos, divCode);
            dto.addPriorTwoYearProjected(projections.stream().map(ActivityProjectionDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearProjected(projections.stream().map(ActivityProjectionDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var reimbursements = activityReimbursementService.getReimbursementDtosForDivisionCode(activityReimbursementDtos, divCode);
            dto.addPriorTwoYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearReimbursed(reimbursements.stream().map(ActivityReimbursementDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

            var obligations = obligationService.getObligationDtosForDivisionCode(obligationDtos, divCode);
            dto.addPriorTwoYearObligated(obligations.stream().map(ObligationDto::getPriorTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentOneYearObligated(obligations.stream().map(ObligationDto::getCurrentOneYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            dto.addCurrentTwoYearObligated(obligations.stream().map(ObligationDto::getCurrentTwoYearAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        Collections.addAll(divisionDtos, educationAndTraining, research, programSupport);

        return divisionDtos;
    }
}