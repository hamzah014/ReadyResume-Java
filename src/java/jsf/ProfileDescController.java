package jsf;

import jpa.entities.ProfileDesc;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.ProfileDescFacade;

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
import jpa.entities.UserProfile;
import jpa.entities.Users;

@Named("profileDescController")
@SessionScoped
public class ProfileDescController implements Serializable {

    private ProfileDesc current;
    private DataModel items = null;
    @EJB
    private jpa.session.ProfileDescFacade ejbFacade;
    @EJB
    private jpa.session.UsersFacade userFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ProfileDescController() {
    }

    public ProfileDesc getSelected() {
        if (current == null) {
            current = new ProfileDesc();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ProfileDescFacade getFacade() {
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

    public String checkProfileDesc() {

        System.out.println("check profile avail");

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int userid = Integer.parseInt(session.getAttribute("userid").toString());

        System.out.println("userid = " + userid);

        try {

            DataModel user = new ListDataModel(userFacade.findById(userid));

            Users userdata = (Users) user.getRowData();

            DataModel md = new ListDataModel(ejbFacade.findByUserId(userdata));

            if (md.getRowCount() > 0) {
                System.out.println("ada data");
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                System.out.println("data - " + md);

                current = (ProfileDesc) md.getRowData();
                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/profileDesc/Edit.xhtml");
                return "";

            } else {
                System.out.println("teda data");
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

                current = new ProfileDesc();
                selectedItemIndex = -1;
                System.out.println("path - " + context.getRequestContextPath());
                context.redirect(context.getRequestContextPath() + "/faces/profileDesc/Create.xhtml");
                return "";

            }

        } catch (Exception e) {
            System.out.println("e - " + e.getMessage());
            return prepareCreate();
        }

    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        System.out.println("this view description");
        current = (ProfileDesc) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";

    }

    public String prepareCreate() {
        System.out.println("ni prepare create desc");
        current = new ProfileDesc();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        System.out.println("ni create desc");
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ProfileDescCreated"));
            return prepareEdit();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        System.out.println("ni edit desc");

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int userid = Integer.parseInt(session.getAttribute("userid").toString());

        System.out.println("userid = " + userid);
        
        DataModel user = new ListDataModel(userFacade.findById(userid));

        Users userdata = (Users) user.getRowData();

        DataModel md = new ListDataModel(getFacade().findByUserId(userdata));
        
        current = (ProfileDesc) md.getRowData();
        selectedItemIndex = -1;
        return "Edit";
    }

    public String update() {
        System.out.println("ni update desc");
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ProfileDescUpdated"));
            return prepareEdit();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ProfileDesc) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ProfileDescDeleted"));
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

    public ProfileDesc getProfileDesc(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = ProfileDesc.class)
    public static class ProfileDescControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ProfileDescController controller = (ProfileDescController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "profileDescController");
            return controller.getProfileDesc(getKey(value));
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
            if (object instanceof ProfileDesc) {
                ProfileDesc o = (ProfileDesc) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ProfileDesc.class.getName());
            }
        }

    }

}
