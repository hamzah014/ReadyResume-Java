package jsf;

import java.io.IOException;
import jpa.entities.Template;
import jsf.util.JsfUtil;
import jsf.util.PaginationHelper;
import jpa.session.TemplateFacade;

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
import jpa.entities.Certificate;
import jpa.entities.Education;
import jpa.entities.Experience;
import jpa.entities.ProfileDesc;
import jpa.entities.Skills;
import jpa.entities.UserProfile;
import jpa.entities.Users;

@Named("templateController")
@SessionScoped
public class TemplateController implements Serializable {

    private Template current;
    private Skills skillcurrent;
    private UserProfile profilecurrent;
    private ProfileDesc desccurrent;
    private Experience expcurrent;
    private Education educurrent;
    private Certificate certcurrent;

    private DataModel items = null;
    private DataModel skillitem = null;
    private DataModel profileitem = null;
    private DataModel descitem = null;
    private DataModel expitem = null;
    private DataModel eduitem = null;
    private DataModel certitem = null;

    @EJB
    private jpa.session.TemplateFacade ejbFacade;
    @EJB
    private jpa.session.CertificateFacade certFacade;
    @EJB
    private jpa.session.EducationFacade eduFacade;
    @EJB
    private jpa.session.ExperienceFacade expFacade;
    @EJB
    private jpa.session.ProfileDescFacade descFacade;
    @EJB
    private jpa.session.SkillsFacade skillFacade;
    @EJB
    private jpa.session.UserProfileFacade profileFacade;
    @EJB
    private jpa.session.UsersFacade userFacade;

    private PaginationHelper pagination;
    private int selectedItemIndex;

    public TemplateController() {
    }

    public Template getSelected() {
        if (current == null) {
            current = new Template();
            selectedItemIndex = -1;
        }
        return current;
    }

    private TemplateFacade getFacade() {
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
        //current = (Template) getItems().getRowData();
        //selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        int userid = Integer.parseInt(session.getAttribute("userid").toString());

        System.out.println("userid = " + userid);

        try {

            DataModel user = new ListDataModel(userFacade.findById(userid));

            Users userdata = (Users) user.getRowData();

            DataModel profile = new ListDataModel(profileFacade.findByUserId(userdata));
            DataModel skill = new ListDataModel(skillFacade.findByUserId(userdata));
            DataModel description = new ListDataModel(descFacade.findByUserId(userdata));
            DataModel experience = new ListDataModel(expFacade.findByUserId(userdata));
            DataModel education = new ListDataModel(eduFacade.findByUserId(userdata));
            DataModel certificate = new ListDataModel(certFacade.findByUserId(userdata));

            System.out.println("skill - " + skill);

            skillitem = skill;
            profileitem = profile;
            descitem = description;
            expitem = experience;
            eduitem = education;
            certitem = certificate;

            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

            System.out.println("path - " + context.getRequestContextPath());
            context.redirect(context.getRequestContextPath() + "/faces/template/View.xhtml");

            return "";

        } catch (Exception e) {
            System.out.println("err - " + e.getMessage());
            return null;

        }

    }

    public String prepareCreate() {
        current = new Template();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TemplateCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Template) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TemplateUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Template) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("TemplateDeleted"));
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

    private void updateCurrentSkillItem() {
        int count = skillFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            skillcurrent = skillFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    private void updateCurrentProfileItem() {
        int count = profileFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            profilecurrent = profileFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    private void updateCurrentDescItem() {
        int count = descFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            desccurrent = descFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    private void updateCurrentExpItem() {
        int count = expFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            expcurrent = expFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    private void updateCurrentEduItem() {
        int count = eduFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            educurrent = eduFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    private void updateCurrentCertItem() {
        int count = certFacade.count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            certcurrent = certFacade.findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    public DataModel getSkillItems() {
        if (skillitem == null) {
            skillitem = getPagination().createPageDataModel();
        }
        return skillitem;
    }

    public DataModel getProfileItems() {
        if (profileitem == null) {
            profileitem = getPagination().createPageDataModel();
        }
        return profileitem;
    }

    public DataModel getDescItems() {
        if (descitem == null) {
            descitem = getPagination().createPageDataModel();
        }
        return descitem;
    }

    public DataModel getExpItems() {
        if (expitem == null) {
            expitem = getPagination().createPageDataModel();
        }
        return expitem;
    }

    public DataModel getEduItems() {
        if (eduitem == null) {
            eduitem = getPagination().createPageDataModel();
        }
        return eduitem;
    }

    public DataModel getCertItems() {
        if (certitem == null) {
            certitem = getPagination().createPageDataModel();
        }
        return certitem;
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

    public Template getTemplate(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Template.class)
    public static class TemplateControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TemplateController controller = (TemplateController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "templateController");
            return controller.getTemplate(getKey(value));
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
            if (object instanceof Template) {
                Template o = (Template) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Template.class.getName());
            }
        }

    }

}
