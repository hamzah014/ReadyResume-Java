package jpa.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-11-30T00:45:14")
@StaticMetamodel(Experience.class)
public class Experience_ { 

    public static volatile SingularAttribute<Experience, Date> endDate;
    public static volatile SingularAttribute<Experience, String> company;
    public static volatile SingularAttribute<Experience, String> details;
    public static volatile SingularAttribute<Experience, Integer> id;
    public static volatile SingularAttribute<Experience, String> position;
    public static volatile SingularAttribute<Experience, Users> userid;
    public static volatile SingularAttribute<Experience, Date> startDate;

}