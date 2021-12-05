package jpa.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-12-05T21:34:59")
@StaticMetamodel(Skills.class)
public class Skills_ { 

    public static volatile SingularAttribute<Skills, String> name;
    public static volatile SingularAttribute<Skills, String> details;
    public static volatile SingularAttribute<Skills, Integer> id;
    public static volatile SingularAttribute<Skills, Users> userid;

}