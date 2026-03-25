package com.company.jmixpmdata.view.project;

import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.view.main.MainView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "projects", layout = MainView.class)
@ViewController(id = "Project.list")
@ViewDescriptor(path = "project-list-view.xml")
@LookupComponent("projectsDataGrid")
@DialogMode(width = "64em")
public class ProjectListView extends StandardListView<Project> {
//
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        int newProjectsCount = dataManager.loadValue("select count(proj) from Project proj " +
                        "where :session_isAdmin = TRUE and proj.status = @enum(com.company.jmixpmdata.entity.ProjectStatus.NEW) " +
                        "and proj.manager.id = :current_user_id", Integer.class)
                .one();

        if (newProjectsCount != 0) {
            notifications.create("New projects", "Projects in NEW status: " + newProjectsCount)
                    .withType(Notifications.Type.SUCCESS)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
        }
    }
}