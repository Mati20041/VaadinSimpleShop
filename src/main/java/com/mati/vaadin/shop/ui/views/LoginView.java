package com.mati.vaadin.shop.ui.views;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

public class LoginView extends VerticalLayout implements View {

    private TextField u;
    private PasswordField p;
    private Button b;


    public LoginView() {
        super();
        buildGUI();
    }

    private void buildGUI() {
        setSizeFull();
        setMargin(true);

        Panel panel = new Panel("Logowanie");
        panel.setSizeUndefined();
        addComponent(panel);

        FormLayout form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        form.setSizeUndefined();
        panel.setContent(form);
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);


        PropertysetItem user = new PropertysetItem();
        user.addItemProperty("username", new ObjectProperty<String>(""));
        user.addItemProperty("password", new ObjectProperty<String>(""));

        FieldGroup binder = new FieldGroup(user);

        form.addComponent(binder.buildAndBind("Username", "username"));
        form.addComponent(binder.buildAndBind("Password", "password", PasswordField.class));

        u = (TextField) binder.getField("username");
        u.setRequired(true);
        u.setImmediate(true);
        u.setWidth("200px");

        p = (PasswordField) binder.getField("password");
        p.setRequired(true);
        p.setImmediate(true);
        p.setWidth("200px");

        b = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                login();
            }
        });

        form.addComponent(b);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        p.setValue("");
        u.setValue("");
    }

    private void login() {
        UsernamePasswordToken token = new UsernamePasswordToken(u.getValue(), p.getValue());
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            getUI().showNotification("Błąd logowania!");
            b.setComponentError(new UserError("Błąd logowania!"));
            return;
        }
        if (u.isValid() && p.isValid()) {
            getUI().getNavigator().navigateTo("main");
            b.setComponentError(null);
        } else {
            b.setComponentError(new UserError("Błąd wprowadzonych danych"));
        }
    }
}
