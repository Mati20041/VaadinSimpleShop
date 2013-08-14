package com.mati.vaadin.shop;

import com.mati.vaadin.shop.models.Brand;
import com.mati.vaadin.shop.models.Car;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.mati.vaadin.shop.ui.views.LoginView;
import com.mati.vaadin.shop.ui.views.MainView;
import org.apache.shiro.SecurityUtils;


@Title("Sklep")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class MyVaadinUI extends UI {

    static public String PERSISTENCE_NAME = "my-shop";

    private JPAContainer<Car> cars;
    private JPAContainer<Brand> brands;

    public MyVaadinUI() {
        cars = JPAContainerFactory.make(Car.class, MyVaadinUI.PERSISTENCE_NAME);
        cars.addNestedContainerProperty("brand.name");
        brands = JPAContainerFactory.make(Brand.class, MyVaadinUI.PERSISTENCE_NAME);
    }

    public MyVaadinUI(Component content) {
        super(content);
        cars = JPAContainerFactory.make(Car.class, MyVaadinUI.PERSISTENCE_NAME);
        cars.addNestedContainerProperty("brand.name");
        brands = JPAContainerFactory.make(Brand.class, MyVaadinUI.PERSISTENCE_NAME);;
    }

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout vl = new VerticalLayout();
        this.setContent(vl);
        vl.setSizeFull();

        Panel mainPanel = new Panel("Sklep by Mati");
        mainPanel.setWidth("80%");
        mainPanel.setHeight("80%");
        vl.addComponent(mainPanel);
        vl.setComponentAlignment(mainPanel, Alignment.MIDDLE_CENTER);

        Navigator navigator = new Navigator(this, mainPanel);
        navigator.addView("login", new LoginView());
        navigator.addView("main", new MainView(brands, cars));
        if (SecurityUtils.getSubject().isAuthenticated())
            navigator.navigateTo("main");
        else
            navigator.navigateTo("login");

    }

}
