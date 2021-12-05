package jsf;

import java.io.IOException;
import jpa.entities.Users;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.UsersFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import jpa.entities.ProfileDesc;

@Named("usersController")
@SessionScoped
public class UsersController implements Serializable {

    private Users current;
    private DataModel items = null;
    @EJB
    private jpa.session.UsersFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public UsersController() {
    }

    public Users getSelected() {
        if (current == null) {
            current = new Users();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UsersFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() throws IOException {
        recreateModel();

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        System.out.println("path - " + context.getRequestContextPath());
        context.redirect(context.getRequestContextPath() + "/faces/users/List.xhtml");
        return "";
    }

    public String prepareView() {
        current = (Users) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String loginView() {
        current = new Users();
        selectedItemIndex = -1;;
        return "Login";
    }

    public String login() {
        System.out.println("test -" + current.getEmail());
        try {
            DataModel md = new ListDataModel(getFacade().findLogin(current.getEmail(), current.getPassword()));

            if (md.getRowCount() > 0) {
                System.out.println("ada oi");
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("LoginSuccess"));

                System.out.println("data - " + md);

                current = (Users) md.getRowData();

                System.out.println("id user - " + current.getId());

                int userid = current.getId();
                String userrole = "user";

                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

                session.setAttribute("userid", userid);
                session.setAttribute("userrole", userrole);

                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/myprofile.xhtml");
                return "";

            } else {
                System.out.println("takada oi");
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/resources/Bundle").getString("LoginFailed"));

                return loginView();
            }

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }

    }

    public String logout() throws IOException {

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        session.removeAttribute("userid");
        session.removeAttribute("userrole");

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        System.out.println("path - " + context.getRequestContextPath());
        context.redirect(context.getRequestContextPath() + "/faces/users/Login.xhtml");
        return "";
    }

    public String prepareCreate() {
        current = new Users();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        System.out.println("ni create");
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UsersCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() throws IOException {

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int userid = Integer.parseInt(session.getAttribute("userid").toString());

        System.out.println("userid = " + userid);

        try {

            DataModel user = new ListDataModel(getFacade().findById(userid));

            current = (Users) user.getRowData();
            selectedItemIndex = -1;

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            System.out.println("path - " + context.getRequestContextPath());
            context.redirect(context.getRequestContextPath() + "/faces/users/Edit.xhtml");
            return "";

        } catch (Exception e) {
            System.out.println("err - " + e.getMessage());
            return null;
        }

    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UsersUpdated"));
            return prepareEdit();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Users) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UsersDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Users getUsers(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Users.class)
    public static class UsersControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsersController controller = (UsersController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usersController");
            return controller.getUsers(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Users) {
                Users o = (Users) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Users.class.getName());
            }
        }

    }

}
