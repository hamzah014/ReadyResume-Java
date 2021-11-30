package jsf;

import jpa.entities.UserProfile;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.UserProfileFacade;
import jpa.session.UsersFacade;

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

@Named("userProfileController")
@SessionScoped
public class UserProfileController implements Serializable {

    private UserProfile current;
    private DataModel items = null;
    @EJB
    private jpa.session.UserProfileFacade ejbFacade;
    @EJB
    private jpa.session.UsersFacade userFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public UserProfileController() {
    }

    public UserProfile getSelected() {
        if (current == null) {
            current = new UserProfile();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UserProfileFacade getFacade() {
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

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        System.out.println("ni view prepare");

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int userid = Integer.parseInt(session.getAttribute("userid").toString());

        System.out.println("userid = " + userid);

        try {

            DataModel user = new ListDataModel(userFacade.findById(userid));

            Users userdata = (Users) user.getRowData();

            DataModel md = new ListDataModel(ejbFacade.findByUserId(userdata));

            if (md.getRowCount() > 0) {
                System.out.println("ada oi");
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                System.out.println("data - " + md);

                current = (UserProfile) md.getRowData();
                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/userProfile/View.xhtml");
                return "";

            } else {
                System.out.println("takada oi");
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                current = new UserProfile();
                selectedItemIndex = -1;
                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/userProfile/Create.xhtml");
                return "";

            }

        } catch (Exception e) {
            System.out.println("e - " + e.getMessage());
            return prepareCreate();
        }
    }

    public String prepareCreate() {
        System.out.println("ni create");
        current = new UserProfile();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        System.out.println("create start");
        try {
            getFacade().create(current);
            System.out.println("crt " + current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UserProfileCreated"));
            return prepareView();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (UserProfile) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UserProfileUpdated"));
            return prepareView();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (UserProfile) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("UserProfileDeleted"));
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

    public UserProfile getUserProfile(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = UserProfile.class)
    public static class UserProfileControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserProfileController controller = (UserProfileController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userProfileController");
            return controller.getUserProfile(getKey(value));
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
            if (object instanceof UserProfile) {
                UserProfile o = (UserProfile) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + UserProfile.class.getName());
            }
        }

    }

}
