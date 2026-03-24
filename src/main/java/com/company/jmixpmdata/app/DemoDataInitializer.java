package com.company.jmixpmdata.app;

import com.company.jmixpmdata.datatype.ProjectLabels;
import com.company.jmixpmdata.entity.ContactInformation;
import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.entity.ProjectStatus;
import com.company.jmixpmdata.entity.Roadmap;
import com.company.jmixpmdata.entity.Task;
import com.company.jmixpmdata.entity.TimeEntry;
import com.company.jmixpmdata.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.security.SystemAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DemoDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final DataManager dataManager;
    private final SystemAuthenticator systemAuthenticator;

    public DemoDataInitializer(DataManager dataManager, SystemAuthenticator systemAuthenticator) {
        this.dataManager = dataManager;
        this.systemAuthenticator = systemAuthenticator;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Demo data initialization started");
        systemAuthenticator.begin("admin");
        try {
            User manager = ensureUser("olga.pm", "Olga", "Petrova", "olga.pm@example.com");
            User analyst = ensureUser("ivan.dev", "Ivan", "Sidorov", "ivan.dev@example.com");
            User designer = ensureUser("anna.qa", "Anna", "Volkova", "anna.qa@example.com");

            Project supportPortal = ensureProject(
                    "Client Support Portal",
                    manager,
                    List.of(manager, analyst, designer),
                    "Support Portal Q2",
                    "Speed up request processing and expose client self-service tools.",
                    LocalDate.now().minusWeeks(6).atTime(9, 0),
                    LocalDate.now().plusWeeks(8).atTime(18, 0),
                    ProjectStatus.IN_PROGRESS,
                    List.of("support", "client", "portal")
            );

            Project billingHub = ensureProject(
                    "Billing Insights Hub",
                    manager,
                    List.of(manager, analyst),
                    "Billing Analytics Rollout",
                    "Prepare finance dashboards and weekly usage anomaly reports.",
                    LocalDate.now().minusWeeks(5).atTime(10, 0),
                    LocalDate.now().plusWeeks(6).atTime(18, 0),
                    ProjectStatus.IN_PROGRESS,
                    List.of("billing", "analytics", "reporting")
            );

            Task portalBacklog = ensureTask(supportPortal, "Refine support backlog", manager, 18,
                    LocalDate.now().minusWeeks(4).atTime(10, 0), "analysis", false);
            Task portalUi = ensureTask(supportPortal, "Implement client dashboard", analyst, 36,
                    LocalDate.now().minusWeeks(3).atTime(9, 30), "frontend", false);
            Task portalQa = ensureTask(supportPortal, "Regression pass for self-service", designer, 20,
                    LocalDate.now().minusWeeks(2).atTime(11, 0), "qa", false);

            Task billingMetrics = ensureTask(billingHub, "Build revenue metrics model", analyst, 30,
                    LocalDate.now().minusWeeks(4).atTime(9, 0), "backend", false);
            Task billingAlerts = ensureTask(billingHub, "Configure anomaly alerts", manager, 22,
                    LocalDate.now().minusWeeks(3).atTime(10, 30), "ops", false);
            Task billingReview = ensureTask(billingHub, "Prepare stakeholder review", designer, 14,
                    LocalDate.now().minusWeeks(1).atTime(12, 0), "report", true);

            seedTimeEntriesIfMissing(Map.of(
                    portalBacklog, manager,
                    portalUi, analyst,
                    portalQa, designer,
                    billingMetrics, analyst,
                    billingAlerts, manager,
                    billingReview, designer
            ));
            log.info("Demo data initialization finished");
        } catch (Exception e) {
            log.error("Demo data initialization failed", e);
        } finally {
            systemAuthenticator.end();
        }
    }

    private User ensureUser(String username, String firstName, String lastName, String email) {
        return dataManager.load(User.class)
                .query("e.username = :username")
                .parameter("username", username)
                .optional()
                .orElseGet(() -> {
                    User user = dataManager.create(User.class);
                    user.setUsername(username);
                    user.setPassword("{noop}demo");
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setActive(true);
                    user.setTimeZoneId("Europe/Moscow");
                    user.setContactInformation(buildContactInformation(email));
                    return dataManager.save(user);
                });
    }

    private ContactInformation buildContactInformation(String email) {
        ContactInformation contactInformation = new ContactInformation();
        contactInformation.setEmail(email);
        contactInformation.setPhone("+7 900 000-00-00");
        contactInformation.setAddress("Moscow");
        contactInformation.setUrl("https://example.com");
        return contactInformation;
    }

    private Project ensureProject(String name,
                                  User manager,
                                  List<User> participants,
                                  String roadmapName,
                                  String roadmapGoal,
                                  LocalDateTime startDate,
                                  LocalDateTime endDate,
                                  ProjectStatus status,
                                  List<String> labels) {
        Optional<Project> existingProject = dataManager.load(Project.class)
                .query("e.name = :name")
                .parameter("name", name)
                .optional();
        if (existingProject.isPresent()) {
            return existingProject.get();
        }

        Roadmap roadmap = dataManager.create(Roadmap.class);
        roadmap.setName(roadmapName);
        roadmap.setGoal(roadmapGoal);
        roadmap.setStartDate(startDate.toLocalDate());
        roadmap.setEndDate(endDate.toLocalDate());
        roadmap = dataManager.save(roadmap);

        Project project = dataManager.create(Project.class);
        project.setName(name);
        project.setManager(manager);
        project.setParticipants(participants);
        project.setRoadmap(roadmap);
        project.setStatus(status);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setProjectLabels(new ProjectLabels(labels));

        Project savedProject = dataManager.save(project);
        log.info("Created demo project {}", name);
        return savedProject;
    }

    private Task ensureTask(Project project,
                            String name,
                            User assignee,
                            int estimatedEfforts,
                            LocalDateTime startDate,
                            String label,
                            boolean isClosed) {
        return dataManager.load(Task.class)
                .query("e.project = :project and e.name = :name")
                .parameter("project", project)
                .parameter("name", name)
                .optional()
                .orElseGet(() -> {
                    Task task = dataManager.create(Task.class);
                    task.setProject(project);
                    task.setName(name);
                    task.setAssignee(assignee);
                    task.setEstimatedEfforts(estimatedEfforts);
                    task.setStartDate(startDate);
                    task.setLabel(label);
                    task.setIsClosed(isClosed);
                    return dataManager.save(task);
                });
    }

    private void seedTimeEntriesIfMissing(Map<Task, User> taskOwners) {
        LocalDate today = LocalDate.now();
        LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate periodStart = currentWeekStart.minusWeeks(3);
        LocalDate periodEnd = currentWeekStart.minusDays(1);
        List<String> projectNames = taskOwners.keySet().stream()
                .map(task -> task.getProject().getName())
                .distinct()
                .toList();

        long existingEntries = dataManager.loadValue(
                        "select count(e) from TimeEntry e " +
                                "where e.task.project.name in :projectNames " +
                                "and e.entryDate >= :periodStart and e.entryDate <= :periodEnd",
                        Long.class)
                .parameter("projectNames", projectNames)
                .parameter("periodStart", periodStart.atStartOfDay())
                .parameter("periodEnd", periodEnd.atTime(LocalTime.MAX))
                .one();

        if (existingEntries > 0) {
            log.info("Skipped demo time entries seeding, {} entries already exist in the target period", existingEntries);
            return;
        }

        Map<LocalDate, List<EntryTemplate>> templatesByDate = buildEntryTemplates(periodStart);
        taskOwners.forEach((task, user) -> templatesByDate.forEach((entryDate, templates) ->
                persistEntries(task, user, entryDate, templates)));
    }

    private Map<LocalDate, List<EntryTemplate>> buildEntryTemplates(LocalDate periodStart) {
        Map<LocalDate, List<EntryTemplate>> templates = new LinkedHashMap<>();
        String[] descriptions = {
                "Reviewed open questions and aligned the next delivery slice.",
                "Updated task notes after implementation and smoke verification.",
                "Prepared follow-up fixes and synced status with the team."
        };
        int[] hoursPattern = {2, 3, 1};

        int businessDayIndex = 0;
        for (LocalDate date = periodStart; date.isBefore(periodStart.plusWeeks(3)); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            List<EntryTemplate> entries = List.of(
                    new EntryTemplate(LocalTime.of(10, 0), hoursPattern[businessDayIndex % hoursPattern.length],
                            descriptions[businessDayIndex % descriptions.length]),
                    new EntryTemplate(LocalTime.of(15, 0), hoursPattern[(businessDayIndex + 1) % hoursPattern.length],
                            descriptions[(businessDayIndex + 1) % descriptions.length])
            );
            templates.put(date, entries);
            businessDayIndex++;
        }
        return templates;
    }

    private void persistEntries(Task task, User user, LocalDate entryDate, List<EntryTemplate> templates) {
        for (int index = 0; index < templates.size(); index++) {
            EntryTemplate template = templates.get(index);
            TimeEntry timeEntry = dataManager.create(TimeEntry.class);
            timeEntry.setTask(task);
            timeEntry.setUser(user);
            timeEntry.setEntryDate(entryDate.atTime(template.time()));
            timeEntry.setTimeSpent(template.hours());
            timeEntry.setDescription(task.getName() + ": " + template.description() + " Entry " + (index + 1) + ".");
            dataManager.save(timeEntry);
        }
    }

    private record EntryTemplate(LocalTime time, int hours, String description) {
    }
}
