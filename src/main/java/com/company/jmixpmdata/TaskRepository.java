package com.company.jmixpmdata;

import com.company.jmixpmdata.entity.Task;
import io.jmix.core.repository.ApplyConstraints;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface TaskRepository extends JmixDataRepository<Task, UUID> {

    @Override
    @ApplyConstraints(value = false)
    Task getById(UUID uuid);
}