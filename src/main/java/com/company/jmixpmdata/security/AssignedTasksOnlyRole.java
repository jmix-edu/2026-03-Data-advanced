package com.company.jmixpmdata.security;

import com.company.jmixpmdata.entity.Task;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "AssignedTasksOnlyRole", code = AssignedTasksOnlyRole.CODE)
public interface AssignedTasksOnlyRole {
    String CODE = "assigned-tasks-only-role";

    @JpqlRowLevelPolicy(entityClass = Task.class, where = "{E}.assignee.username = :current_user_username")
    void task();
}