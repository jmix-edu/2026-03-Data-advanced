package com.company.jmixpmdata.app;

import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.entity.ProjectStats;
import com.company.jmixpmdata.entity.Task;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProjectStatsService {
    private static final Logger log = LoggerFactory.getLogger(ProjectStatsService.class);
    private final DataManager dataManager;
    private final FetchPlans fetchPlans;

    public ProjectStatsService(DataManager dataManager, FetchPlans fetchPlans) {
        this.dataManager = dataManager;
        this.fetchPlans = fetchPlans;
    }

    public List<ProjectStats> fetchProjectStatistics() {
        List<Project> projects = dataManager.load(Project.class)
                .all()
                .fetchPlan("project-with-tasks-fetch-plan")
                .list();
        return projects.stream()
                .map(project -> {
                    ProjectStats stats = dataManager.create(ProjectStats.class);
                    stats.setProjectName(project.getName());

                    List<Task> tasks = project.getTasks();
                    stats.setTasksCount(tasks.size());

                    for (Task task : tasks) {
                        log.info("Assignee: " + task.getAssignee().getDisplayName());
                    }

                    Integer estimatedEfforts = tasks.stream()
                            .map(task-> task.getEstimatedEfforts() == null ? 0 : task.getEstimatedEfforts())
                            .reduce(0, Integer::sum);
                    stats.setPlannedEfforts(estimatedEfforts);
                    stats.setActualEfforts(getActualEfforts(project.getId()));
                    return stats;
                })
                .toList();
    }

    private Integer getActualEfforts(UUID projectId) {

        return dataManager.loadValue("select sum(te.timeSpent) from TimeEntry te " +
                        "where te.task.project.id = :projectId", Integer.class)
                .parameter("projectId", projectId)
                .one();
    }

    private FetchPlan createFPWithTasks() {
        return fetchPlans.builder(Project.class)
                .addFetchPlan(FetchPlan.INSTANCE_NAME)
                .add("tasks", fetchPlanBuilder ->
                        fetchPlanBuilder.add("estimatedEfforts")
                                .add("startDate")
                                .add("assignee", FetchPlan.INSTANCE_NAME))
                .build();
    }
}