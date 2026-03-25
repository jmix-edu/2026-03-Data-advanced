package com.company.jmixpmdata.view.userprojects;


import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.entity.User;
import com.company.jmixpmdata.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;

@Route(value = "user-projects-view", layout = MainView.class)
@ViewController(id = "UserProjectsView")
@ViewDescriptor(path = "user-projects-view.xml")
@DialogMode(width = "50em", height = "40em")
public class UserProjectsView extends StandardView {

    private User user;

    public User getUser() {
        return user;
    }

    @ViewComponent
    private CollectionLoader<Project> projectsDl;

    public UserProjectsView withUser(User user) {
        this.user = user;

        projectsDl.setParameter("user", user);
        projectsDl.load();

        return this;
    }
}