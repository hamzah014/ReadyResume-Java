package jsf;

import java.io.IOException;
import jpa.entities.Admin;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.AdminFacade;

import java.io.Serializable;
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
import jpa.entities.Users;

@Named("adminController")
@SessionScoped
public class AdminController implements Serializable {

    private Admin current;
    private DataModel items = null;
    @EJB
    private jpa.session.AdminFacade ejbFacade;
    @EJB
    private jpa.session.UsersFacade userFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public AdminController() {
    }

    public Admin getSelected() {
        if (current == null) {
            current = new Admin();
            selectedItemIndex = -1;
        }
        return current;
    }

    private AdminFacade getFacade() {
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
    
    public int getUserTotal(){
        
        DataModel users = new ListDataModel(userFacade.findAll());
        int count = userFacade.count();
        
        return count;
    
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Admin) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String loginView() {
        current = new Admin();
        selectedItemIndex = -1;
        return "Login";
    }

    public String login() {
        System.out.println("email admin -" + current.getEmail());
        System.out.println("passw admin -" + current.getPassword());
        try {
            DataModel md = new ListDataModel(getFacade().findLogin(current.getEmail(), current.getPassword()));
            System.out.println("dt = " + md);
            if (md.getRowCount() > 0) {
                System.out.println("ada oi");
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("LoginSuccess"));

                System.out.println("data - " + md);

                current = (Admin) md.getRowData();

                System.out.println("id admin - " + current.getId());

                int userid = current.getId();
                String userrole = "admin";
                
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
                
                session.setAttribute("userid", userid);
                session.setAttribute("userrole", userrole);
                
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/dashboard.xhtml");
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
        
        current = new Admin();
        selectedItemIndex = -1;

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        
        session.removeAttribute("userid");
        session.removeAttribute("userrole");
        
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        System.out.println("path - " + context.getRequestContextPath());
        context.redirect(context.getRequestContextPath() + "/faces/admin/Login.xhtml");
        return "";
    }

    public String prepareCreate() {
        current = new Admin();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AdminCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Admin) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AdminUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Admin) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AdminDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
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

    public Admin getAdmin(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Admin.class)
    public static class AdminControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AdminController controller = (AdminController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "adminController");
            return controller.getAdmin(getKey(value));
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
            if (object instanceof Admin) {
                Admin o = (Admin) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Admin.class.getName());
            }
        }

    }

}
