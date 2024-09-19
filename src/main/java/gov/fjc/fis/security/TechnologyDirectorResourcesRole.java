package gov.fjc.fis.security;

import gov.fjc.fis.entity.*;
import gov.fjc.fis.entity.dto.ObligationDto;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "ITO Director resources", code = TechnologyDirectorResourcesRole.CODE, scope = "UI", description = "entity and report access")
public interface TechnologyDirectorResourcesRole extends UiMinimalRole, ReportResourcesRole {
    String CODE = "resources-ito-director";

    @EntityAttributePolicy(entityClass = Activity.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Activity.class, actions = EntityPolicyAction.READ)
    void activity();

    @EntityAttributePolicy(entityClass = Appropriation.class, attributes = {"id", "budgetFiscalYear"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Appropriation.class, actions = EntityPolicyAction.READ)
    void appropriation();

    @EntityAttributePolicy(entityClass = Branch.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Branch.class, actions = EntityPolicyAction.READ)
    void branch();

    @EntityAttributePolicy(entityClass = Division.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Division.class, actions = EntityPolicyAction.READ)
    void division();

    @EntityAttributePolicy(entityClass = Fund.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Fund.class, actions = EntityPolicyAction.READ)
    void fund();

    @EntityAttributePolicy(entityClass = Obligation.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Obligation.class, actions = EntityPolicyAction.READ)
    void obligation();

    @EntityAttributePolicy(entityClass = ObligationDto.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ObligationDto.class, actions = EntityPolicyAction.ALL)
    void obligationDto();
}