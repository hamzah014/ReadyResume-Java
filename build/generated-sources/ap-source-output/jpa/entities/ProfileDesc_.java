package jpa.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-12-05T21:34:59")
@StaticMetamodel(ProfileDesc.class)
public class ProfileDesc_ { 

    public static volatile SingularAttribute<ProfileDesc, String> description;
    public static volatile SingularAttribute<ProfileDesc, Integer> id;
    public static volatile SingularAttribute<ProfileDesc, Users> userid;

}