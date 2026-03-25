package com.company.jmixpmdata.security;

import com.company.jmixpmdata.entity.User;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "UserSafeModify", code = UserSafeModifyRole.CODE, scope = "UI")
public interface UserSafeModifyRole {
    String CODE = "user-safe-modify";

    @EntityAttributePolicy(entityClass = User.class, attributes = {"firstName", "lastName", "timeZoneId", "projects", "contactInformation"}, action = EntityAttributePolicyAction.MODIFY)
    @EntityAttributePolicy(entityClass = User.class, attributes = {"id", "username", "version"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @MenuPolicy(menuIds = "User.list")
    @ViewPolicy(viewIds = {"User.list", "User.detail"})
    void screens();
}