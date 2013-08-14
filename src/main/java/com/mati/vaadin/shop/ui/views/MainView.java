package com.mati.vaadin.shop.ui.views;

import com.mati.vaadin.shop.models.Brand;
import com.mati.vaadin.shop.models.Car;
import com.mati.vaadin.shop.ui.subwindows.NewCarWindow;
import com.mati.vaadin.shop.ui.subwindows.SellCarWindow;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainView extends VerticalLayout implements View {

    final public String SUPER_USER = "super_user";

    private JPAContainer<Brand> brands;
    private JPAContainer<Car> cars;
    private NewCarWindow newCarForm;
    private Table table;
    private ListSelect ls;
    private Button but;
    private Button but2;
    private Button but3;


    public MainView(JPAContainer<Brand> brands, JPAContainer<Car> cars) {
        this.brands = brands;
        this.cars = cars;

        if (cars.size() == 0 || brands.size() == 0) {
            generateDummyData(brands, cars);
        }
        newCarForm = new NewCarWindow(brands, cars);
        buildGUI();
        bindListeners();
    }

    private void bindListeners() {
        ls.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                cars.removeAllContainerFilters();
                if (ls.getValue() != null) {
                    cars.addContainerFilter(new Compare.Equal("brand", brands.getItem(ls.getValue()).getEntity()));
                    refreshFooter();
                }
            }
        });

        but.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (table.getValue() != null) {
                    getUI().addWindow(new SellCarWindow(cars.getItem(table.getValue())));
                    refreshFooter();
                }
            }
        });

        but2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getUI().addWindow(newCarForm);
            }
        });

        but3.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (table.getValue() != null) {
                    cars.removeItem(table.getValue());
                    cars.commit();
                    getUI().showNotification("Usunięto!");
                }
            }
        });


    }

    private void generateDummyData(JPAContainer<Brand> brands, JPAContainer<Car> cars) {
        cars.addEntity(new Car("Micra", new Date(), 30000, brands.getItem(brands.addEntity(new Brand("Nissan"))).getEntity()));
        brands.addEntity(new Brand("Audi"));
        brands.addEntity(new Brand("Mercedes"));
        brands.addEntity(new Brand("Fiat"));
        brands.commit();
        cars.commit();
    }

    private Component generateUsersMenu() {
        return null; //TODO
    }

    private Component generateShopMenu() {


        HorizontalSplitPanel hsp = new HorizontalSplitPanel();
        hsp.setSizeFull();

        GridLayout gl = new GridLayout(5, 2);
        gl.setSizeFull();
        gl.setMargin(true);
        hsp.setSecondComponent(gl);
        hsp.setSplitPosition(20f,Unit.PERCENTAGE);

        ls = new ListSelect("Marki");
        ls.setContainerDataSource(brands);
        ls.setImmediate(true);
        ls.setItemCaptionPropertyId("name");
        ls.setHeight("98%");
        ls.setWidth("98%");

        VerticalLayout vl = new VerticalLayout(ls);
        vl.setMargin(true);
        vl.setSizeFull();
        hsp.setFirstComponent(vl);

        table = new Table("Auta") {
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
                if (property.getType() != Date.class)
                    return super.formatPropertyValue(rowId, colId, property);
                else {
                    DateFormat df = new SimpleDateFormat("yyyy");
                    return df.format(property.getValue());
                }
            }
        };
        table.setContainerDataSource(cars);
        table.setSelectable(true);
        table.addGeneratedColumn("PROFIT", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table components, Object o, Object o2) {
                Label l = new Label();
                Integer count = (Integer) table.getItem(o).getItemProperty("sold").getValue();
                Double price = (Double) table.getItem(o).getItemProperty("price").getValue();
                l.setValue(count * price + "");
                return l;
            }
        });
        table.setVisibleColumns(new String[]{"brand.name", "name", "yearOfProduction", "price", "sold", "PROFIT"});
        table.addPropertySetChangeListener(new Container.PropertySetChangeListener() {
            @Override
            public void containerPropertySetChange(Container.PropertySetChangeEvent propertySetChangeEvent) {
                refreshFooter();
            }
        });
        refreshFooter();
        table.setSizeFull();

        gl.addComponent(table, 0, 0, 4, 0);

        but = new Button("Sprzedaj");

        but.setWidth("90%");
        gl.addComponent(but, 0, 1);
        gl.setComponentAlignment(but, Alignment.BOTTOM_CENTER);

        but2 = new Button("Nowy");
        but2.setWidth("90%");
        gl.addComponent(but2, 1, 1);
        gl.setComponentAlignment(but2, Alignment.BOTTOM_CENTER);

        but3 = new Button("Usuń");

        but3.setWidth("90%");
        gl.addComponent(but3, 2, 1);
        gl.setComponentAlignment(but3, Alignment.BOTTOM_CENTER);

        gl.setRowExpandRatio(0, 6f);
        gl.setRowExpandRatio(1, 1f);


        return hsp;
    }

    private void refreshFooter() {

        //Setting footer
        //
        EntityManager manager = cars.getEntityProvider().getEntityManager();

        //Typed Query  łatwiej zbudować z klocków, dlatego nie korzystam
        //table.setColumnFooter("sold",manager.createQuery("SELECT SUM(c.sold) FROM Car c ").getSingleResult().toString());

        //Criteria Query - cool stuff
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root r = cq.from(Car.class);
        cq.select(cb.sum(r.get("sold")));
        if (ls.getValue() != null)
            cq.where(cb.equal(r.get("brand"), brands.getItem(ls.getValue()).getEntity()));
        Query q = manager.createQuery(cq);
        table.setColumnFooter("sold", q.getSingleResult() == null ? "0" : q.getSingleResult().toString());

        cq = cb.createQuery();
        r = cq.from(Car.class);
        cq.select(cb.sum(cb.prod(r.get("sold"), r.get("price"))));
        if (ls.getValue() != null)
            cq.where(cb.equal(r.get("brand"), brands.getItem(ls.getValue()).getEntity()));
        q = manager.createQuery(cq);


        // takie zapytanie nie działa, jpql to gówno
//        table.setColumnFooter("PROFIT",manager.createQuery("SELECT SUM (c.sold * c.price) FROM Car c").getSingleResult().toString());

        table.setColumnFooter("PROFIT", q.getSingleResult() == null ? "0" : q.getSingleResult().toString());
        table.setFooterVisible(true);
        //
        // End of footer
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        Subject user = SecurityUtils.getSubject();

        if(!(user.isAuthenticated()||user.hasRole("normal-user"))){
            getUI().getNavigator().navigateTo("login");
        }
        setComponentsEnabled(user);

    }

    private void setComponentsEnabled(Subject user) {
        boolean enableExtraEditing = user.hasRole(SUPER_USER);
        but2.setEnabled(enableExtraEditing);
        but3.setEnabled(enableExtraEditing);
        table.setEditable(enableExtraEditing);
    }

    private void buildGUI() {
        setSizeFull();
        this.setMargin(true);

        Button logout = new Button("Logout");
        logout.setStyleName("link");
        logout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                logout();
            }
        });
        this.addComponent(logout);
        this.setComponentAlignment(logout, Alignment.TOP_RIGHT);

        TabSheet tabs = new TabSheet();
        tabs.addTab(generateShopMenu(), "Sklep");
        //tabs.addTab(generateUsersMenu(),"Użytkownicy");
        tabs.setSizeFull();
        this.addComponent(tabs);
        setExpandRatio(tabs, 1f);
    }

    private void logout() {
        SecurityUtils.getSubject().logout();
        getUI().getPage().setLocation("/");
    }
}
