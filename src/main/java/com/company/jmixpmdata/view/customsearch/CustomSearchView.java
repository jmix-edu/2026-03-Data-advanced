package com.company.jmixpmdata.view.customsearch;


import com.company.jmixpmdata.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "custom-search-view", layout = MainView.class)
@ViewController(id = "CustomSearchView")
@ViewDescriptor(path = "custom-search-view.xml")
public class CustomSearchView extends StandardView {
}