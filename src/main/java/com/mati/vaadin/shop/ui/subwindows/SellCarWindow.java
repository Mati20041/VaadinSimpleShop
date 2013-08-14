package com.mati.vaadin.shop.ui.subwindows;

import com.mati.vaadin.shop.models.Car;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;

public class SellCarWindow extends Window{
    public SellCarWindow(EntityItem<Car> car) {
        super("Sprzedaż auta");
        setModal(true);
        setWidth("50%");
        setHeight("50%");

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        setContent(vl);

        final FieldGroup binder = new FieldGroup(car);
        binder.setEnabled(false);

        FormLayout form = new FormLayout();
        form.setImmediate(true);
        form.setSizeUndefined();

        form.addComponent(binder.buildAndBind("Nazwa","name"));
        DateField yearOfProduction = binder.buildAndBind("Rocznik", "yearOfProduction", DateField.class);
        yearOfProduction.setDateFormat("yyyy");
        form.addComponent(yearOfProduction);
        form.addComponent(binder.buildAndBind("Cena","price"));
        form.addComponent(binder.buildAndBind("Liczba sprzedanych","sold"));
        final TextField tf = new TextField("Liczba do sprzedania");
        tf.setImmediate(true);
        form.addComponent(tf);

        final ObjectProperty<Integer> i = new ObjectProperty<Integer>(0);
        tf.setPropertyDataSource(i);

        final Button accept = new Button("Sprzedaj");
        final Item finalCar = car;
        accept.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                tf.commit();
                if(i.getValue()<0){
                    accept.setComponentError(new UserError("Wartość nie może być ujemna!"));
                    tf.setValue(-i.getValue()+"");
                    return;
                }
                finalCar.getItemProperty("sold").setValue((Integer)finalCar.getItemProperty("sold").getValue() + i.getValue());
                getUI().showNotification("Sprzedano!", Notification.Type.HUMANIZED_MESSAGE);
                close();
            }
        });

        form.addComponent(accept);

        vl.addComponent(form);
    }
}
