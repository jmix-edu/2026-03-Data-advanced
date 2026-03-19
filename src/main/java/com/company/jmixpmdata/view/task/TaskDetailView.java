package com.company.jmixpmdata.view.task;


import com.company.jmixpmdata.datatype.ProjectLabels;
import com.company.jmixpmdata.entity.Project;
import com.company.jmixpmdata.entity.Task;
import com.company.jmixpmdata.view.main.MainView;
import com.vaadin.flow.component.combobox.dataview.ComboBoxListDataView;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.checkbox.JmixCheckbox;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.view.*;

import java.util.ArrayList;

@Route(value = "tasks/:id", layout = MainView.class)
@ViewController(id = "Task_.detail")
@ViewDescriptor(path = "task-detail-view.xml")
@EditedEntityContainer("taskDc")
public class TaskDetailView extends StandardDetailView<Task> {
    @ViewComponent
    private JmixComboBox<String> labelField;

    private ComboBoxListDataView<String> labelsDataView;
    @ViewComponent
    private JmixCheckbox isClosedField;

    @Subscribe
    public void onInit(final InitEvent event) {
        labelsDataView = labelField.setItems(new ListDataProvider<>(new ArrayList<>()));
        isClosedField.setRequiredIndicatorVisible(false);
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Project project = getEditedEntity().getProject();
        if (project != null && project.getProjectLabels() != null) {
            labelsDataView.addItems(project.getProjectLabels().getLabels());
        }
    }

    

    
}