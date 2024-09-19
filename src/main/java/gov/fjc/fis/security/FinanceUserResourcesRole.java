package gov.fjc.fis.security;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.*;
import gov.fjc.fis.entity.form.Ape;
import gov.fjc.fis.entity.form.DistanceLearning;
import gov.fjc.fis.entity.personnel.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "Financial Management User", code = FinanceUserResourcesRole.CODE, scope = "UI", description = "Full access to most screens, entities, and reports")
public interface FinanceUserResourcesRole extends UiMinimalRole, ReportResourcesRole {
    String CODE = "resources-ofm-user";

    @EntityAttributePolicy(entityClass = ActionCode.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ActionCode.class, actions = EntityPolicyAction.READ)
    void actionCode();

    @EntityAttributePolicy(entityClass = Activity.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Activity.class, actions = EntityPolicyAction.ALL)
    void activity();

    @EntityAttributePolicy(entityClass = ActivityDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ActivityDto.class, actions = EntityPolicyAction.ALL)
    void activityDto();

    @EntityAttributePolicy(entityClass = ActivityProjection.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ActivityProjection.class, actions = EntityPolicyAction.ALL)
    void activityProjection();

    @EntityAttributePolicy(entityClass = ActivityProjectionDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ActivityProjectionDto.class, actions = EntityPolicyAction.ALL)
    void activityProjectionDto();

    @EntityAttributePolicy(entityClass = ActivityReimbursement.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ActivityReimbursement.class, actions = EntityPolicyAction.ALL)
    void activityReimbursement();

    @EntityAttributePolicy(entityClass = ActivityReimbursementDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ActivityReimbursementDto.class, actions = EntityPolicyAction.ALL)
    void activityReimbursementDto();

    @EntityAttributePolicy(entityClass = Ape.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Ape.class, actions = EntityPolicyAction.ALL)
    void ape();

    @EntityAttributePolicy(entityClass = Appropriation.class, attributes = {"oneYearAmount", "twoYearAmount", "adjustment", "status"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = Appropriation.class, attributes = {"id", "divisions", "categories", "adjustments", "budgetFiscalYear", "version", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate", "createdByString", "totalAmount"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Appropriation.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void appropriation();

    @EntityAttributePolicy(entityClass = AppropriationAdjustment.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = AppropriationAdjustment.class, actions = EntityPolicyAction.ALL)
    void appropriationAdjustment();

    @EntityAttributePolicy(entityClass = AppropriationDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = AppropriationDto.class, actions = EntityPolicyAction.ALL)
    void appropriationDto();

    @EntityAttributePolicy(entityClass = BonusProjection.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = BonusProjection.class, actions = EntityPolicyAction.ALL)
    void bonusProjection();

    @EntityAttributePolicy(entityClass = Branch.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Branch.class, actions = EntityPolicyAction.ALL)
    void branch();

    @EntityAttributePolicy(entityClass = Category.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Category.class, actions = EntityPolicyAction.ALL)
    void category();

    @EntityAttributePolicy(entityClass = CategoryDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = CategoryDto.class, actions = EntityPolicyAction.ALL)
    void categoryDto();

    @EntityAttributePolicy(entityClass = DistanceLearning.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = DistanceLearning.class, actions = EntityPolicyAction.ALL)
    void distanceLearning();

    @EntityAttributePolicy(entityClass = Division.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Division.class, actions = EntityPolicyAction.ALL)
    void division();

    @EntityAttributePolicy(entityClass = DivisionAllocation.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = DivisionAllocation.class, actions = EntityPolicyAction.ALL)
    void divisionAllocation();

    @EntityAttributePolicy(entityClass = DivisionDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = DivisionDto.class, actions = EntityPolicyAction.ALL)
    void divisionDto();

    @EntityAttributePolicy(entityClass = Document.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Document.class, actions = EntityPolicyAction.ALL)
    void document();

    @EntityAttributePolicy(entityClass = Donor.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Donor.class, actions = EntityPolicyAction.ALL)
    void donor();

    @EntityAttributePolicy(entityClass = Employee.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Employee.class, actions = EntityPolicyAction.READ)
    void employee();

    @EntityAttributePolicy(entityClass = Fund.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Fund.class, actions = EntityPolicyAction.READ)
    void fund();

    @EntityAttributePolicy(entityClass = FundControlNotice.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = FundControlNotice.class, actions = EntityPolicyAction.ALL)
    void fundControlNotice();

    @EntityAttributePolicy(entityClass = Group.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Group.class, actions = EntityPolicyAction.ALL)
    void group();

    @EntityAttributePolicy(entityClass = GroupDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = GroupDto.class, actions = EntityPolicyAction.ALL)
    void groupDto();

    @EntityAttributePolicy(entityClass = Invoice.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Invoice.class, actions = EntityPolicyAction.ALL)
    void invoice();

    @EntityAttributePolicy(entityClass = Jitf.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Jitf.class, actions = EntityPolicyAction.ALL)
    void jitf();

    @EntityAttributePolicy(entityClass = ObjectClass.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ObjectClass.class, actions = EntityPolicyAction.ALL)
    void objectClass();

    @EntityAttributePolicy(entityClass = ObjectClassDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ObjectClassDto.class, actions = EntityPolicyAction.ALL)
    void objectClassDto();

    @EntityAttributePolicy(entityClass = Obligation.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Obligation.class, actions = EntityPolicyAction.ALL)
    void obligation();

    @EntityAttributePolicy(entityClass = ObligationDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ObligationDto.class, actions = EntityPolicyAction.ALL)
    void obligationDto();

    @EntityAttributePolicy(entityClass = PayAdjustment.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = PayAdjustment.class, actions = EntityPolicyAction.READ)
    void payAdjustment();

    @EntityAttributePolicy(entityClass = PayPeriod.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = PayPeriod.class, actions = EntityPolicyAction.READ)
    void payPeriod();

    @EntityAttributePolicy(entityClass = Position.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Position.class, actions = EntityPolicyAction.ALL)
    void position();

    @EntityAttributePolicy(entityClass = PositionAction.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PositionAction.class, actions = EntityPolicyAction.ALL)
    void positionAction();

    @EntityAttributePolicy(entityClass = PositionDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = PositionDto.class, actions = EntityPolicyAction.ALL)
    void positionDto();

    @MenuPolicy(menuIds = {"fis_DashboardView", "fis_Appropriation.list", "fis_Division.list", "fis_Activity.list", "fis_Obligation.list", "fis_Invoice.list", "fis_FundControlNotice.list", "fis_Jitf.list", "fis_Branch.list", "fis_Group.list", "fis_Category.list", "fis_ObjectClass.list", "fis_Fund.list", "fis_ActivityProjection.list", "fis_Document.list", "fis_Position.list", "fis_PositionAction.list", "fis_BonusProjection.list", "fis_Employee.list", "fis_PayPeriod.list", "fis_PayAdjustment.list", "fis_ActionCode.list", "fis_Donor.list", "fis_Division.foundation-list", "fis_Activity.foundation-list", "fis_Obligation.foundation-list", "fis_Invoice.foundation-list", "fis_FundControlNotice.foundation-list", "fis_ReportRouter#openStatusOfFundsReport", "fis_ReportRouter#openEducationProgramsReport", "fis_ReportRouter#openBudgetRequestReport", "fis_DistanceLearning.list", "fis_Ape.list"})
    @ViewPolicy(viewIds = {"fis_DashboardView", "fis_Appropriation.list", "fis_Division.list", "fis_Activity.list", "fis_Obligation.list", "fis_Invoice.list", "fis_FundControlNotice.list", "fis_Jitf.list", "fis_Branch.list", "fis_Group.list", "fis_Category.list", "fis_ObjectClass.list", "fis_Fund.list", "fis_ActivityProjection.list", "fis_Document.list", "fis_Position.list", "fis_PositionAction.list", "fis_BonusProjection.list", "fis_Employee.list", "fis_PayPeriod.list", "fis_PayAdjustment.list", "fis_ActionCode.list", "fis_Donor.list", "fis_Division.foundation-list", "fis_Activity.foundation-list", "fis_Obligation.foundation-list", "fis_Invoice.foundation-list", "fis_FundControlNotice.foundation-list", "fis_ReportRouter#openStatusOfFundsReport", "fis_ReportRouter#openEducationProgramsReport", "fis_ReportRouter#openBudgetRequestReport", "fis_DistanceLearning.list", "fis_Ape.list", "fis_StatusOfFundsReportView", "fis_EducationProgramsReportView", "fis_BudgetRequestReportView", "fis_Ape.detail", "fis_Ape2.detail"})
    void screens();

    @EntityAttributePolicy(entityClass = UserMessage.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = UserMessage.class, actions = EntityPolicyAction.READ)
    void userMessage();
}