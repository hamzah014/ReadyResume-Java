package jpa.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Certificate;
import jpa.entities.Education;
import jpa.entities.Experience;
import jpa.entities.ProfileDesc;
import jpa.entities.ProfilePicture;
import jpa.entities.Skills;
import jpa.entities.UserProfile;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-11-30T00:45:14")
@StaticMetamodel(Users.class)
public class Users_ { 

    public static volatile CollectionAttribute<Users, Skills> skillsCollection;
    public static volatile SingularAttribute<Users, String> password;
    public static volatile CollectionAttribute<Users, Education> educationCollection;
    public static volatile CollectionAttribute<Users, ProfilePicture> profilePictureCollection;
    public static volatile SingularAttribute<Users, Integer> id;
    public static volatile CollectionAttribute<Users, Certificate> certificateCollection;
    public static volatile CollectionAttribute<Users, UserProfile> userProfileCollection;
    public static volatile CollectionAttribute<Users, Experience> experienceCollection;
    public static volatile CollectionAttribute<Users, ProfileDesc> profileDescCollection;
    public static volatile SingularAttribute<Users, String> email;

}