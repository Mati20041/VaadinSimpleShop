package com.mati.vaadin.shop.ui.subwindows;

import com.mati.vaadin.shop.models.Brand;
import com.mati.vaadin.shop.models.Car;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;

public class NewCarWindow extends Window implements Window.CloseListener{

    private BeanItem<Car> car;
    private FieldGroup binder;


    public NewCarWindow(final JPAContainer<Brand> brands, final JPAContainer<Car> cars){
        super("Nowe auto");
        setModal(true);
        setWidth("50%");
        setHeight("50%");
        addCloseListener(this);

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(vl);

        car = new BeanItem<Car>(new Car());

        binder = new FieldGroup(car);

        FormLayout form = new FormLayout();
        form.setImmediate(true);
        form.setSizeUndefined();
        final NativeSelect brandSelector = new NativeSelect("Marka",brands);
        brandSelector.setItemCaptionPropertyId("name");
        brandSelector.setNullSelectionAllowed(false);
        brandSelector.setValue(brands.getItemIds().iterator().next());

        form.addComponent(brandSelector);
        form.addComponent(binder.buildAndBind("Nazwa","name"));
        DateField yearOfProduction = binder.buildAndBind("Rocznik", "yearOfProduction",DateField.class);
        yearOfProduction.setDateFormat("yyyy");
        form.addComponent(yearOfProduction);
        form.addComponent(binder.buildAndBind("Cena","price"));

        Button accept = new Button("Zapisz");
        accept.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    binder.commit();
                } catch (FieldGroup.CommitException e) {
                    e.printStackTrace();
                }
                car.getBean().setBrand(brands.getItem(brandSelector.getValue()).getEntity());
                cars.addEntity(car.getBean());
                cars.commit();
                car = new BeanItem<Car>(new Car());
                binder.setItemDataSource(car);
                getUI().showNotification("Dodano nowe auto!", Notification.Type.HUMANIZED_MESSAGE);
                close();
            }
        });

        form.addComponent(accept);

        vl.addComponent(form);

    }


    @Override
    public void windowClose(CloseEvent closeEvent) {
        car = new BeanItem<Car>(new Car());
        binder.setItemDataSource(car);
    }
}
