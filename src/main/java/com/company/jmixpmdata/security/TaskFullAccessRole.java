package com.company.jmixpmdata.security;

import com.company.jmixpmdata.entity.Task;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "TaskFullAccess", code = TaskFullAccessRole.CODE, scope = "UI")
public interface TaskFullAccessRole {
    String CODE = "task-full-access";

    @EntityAttributePolicy(entityClass = Task.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Task.class, actions = EntityPolicyAction.ALL)
    void task();

    @MenuPolicy(menuIds = "Task_repo.list")
    @ViewPolicy(viewIds = {"Task_repo.list", "Task_.detail"})
    void screens();
}