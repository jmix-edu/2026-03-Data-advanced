package com.company.jmixpmdata.view.user;

import com.company.jmixpmdata.app.UsersService;
import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.entity.User;
import com.company.jmixpmdata.view.main.MainView;
import com.company.jmixpmdata.view.userprojects.UserProjectsView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.genericfilter.GenericFilter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
public class UserListView extends StandardListView<User> {

    private Project filterProject;
    @Autowired
    private UsersService usersService;
    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private GenericFilter genericFilter;
    @ViewComponent
    private DataGrid<User> usersDataGrid;
    @Autowired
    private DialogWindows dialogWindows;

    @Install(to = "usersDl", target = Target.DATA_LOADER)
    private List<User> usersDlLoadDelegate(final LoadContext<User> loadContext) {
        LoadContext.Query query = loadContext.getQuery();
        if (filterProject != null && query != null) {
            return usersService.getUsersNotInProject(filterProject, query.getFirstResult(), query.getMaxResults());
        }

        return dataManager.loadList(loadContext);
    }


    public void setFilterProject(Project filterProject) {
        this.filterProject = filterProject;
        genericFilter.setVisible(false);
    }

    @Subscribe("usersDataGrid.showUserProjects")
    public void onUsersDataGridShowUserProjects(final ActionPerformedEvent event) {
        User selected = usersDataGrid.getSingleSelectedItem();
        if (selected == null) {
            return;
        }

        dialogWindows.view(this, UserProjectsView.class)
                .withViewConfigurer(userProjectsView -> userProjectsView.withUser(selected))
                .open();
    }
}