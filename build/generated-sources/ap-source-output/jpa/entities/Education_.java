package jpa.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-12-05T21:34:59")
@StaticMetamodel(Education.class)
public class Education_ { 

    public static volatile SingularAttribute<Education, String> institution;
    public static volatile SingularAttribute<Education, String> level;
    public static volatile SingularAttribute<Education, Date> endDate;
    public static volatile SingularAttribute<Education, String> details;
    public static volatile SingularAttribute<Education, Integer> id;
    public static volatile SingularAttribute<Education, Users> userid;
    public static volatile SingularAttribute<Education, Date> startDate;

}