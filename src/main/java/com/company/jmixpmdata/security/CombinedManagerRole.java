package com.company.jmixpmdata.security;

import com.company.jmixpmdata.entity.Roadmap;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "CombinedManagerRole", code = CombinedManagerRole.CODE, scope = "UI")
public interface CombinedManagerRole
        extends ProjectFullAccessRole,
        TaskFullAccessRole,
        TimeEntryFullAccessRole,
        UserSafeModifyRole,
        UiMinimalRole {
    String CODE = "combined-manager-role";

    @EntityAttributePolicy(entityClass = Roadmap.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Roadmap.class, actions = EntityPolicyAction.ALL)
    void roadmap();
}