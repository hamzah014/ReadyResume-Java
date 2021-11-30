package jpa.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-11-30T00:45:14")
@StaticMetamodel(UserProfile.class)
public class UserProfile_ { 

    public static volatile SingularAttribute<UserProfile, String> address;
    public static volatile SingularAttribute<UserProfile, Date> birthdate;
    public static volatile SingularAttribute<UserProfile, String> name;
    public static volatile SingularAttribute<UserProfile, Integer> id;
    public static volatile SingularAttribute<UserProfile, Users> userid;
    public static volatile SingularAttribute<UserProfile, String> email;
    public static volatile SingularAttribute<UserProfile, Integer> age;

}