package jpa.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import jpa.entities.Users;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-11-30T00:45:14")
@StaticMetamodel(Certificate.class)
public class Certificate_ { 

    public static volatile SingularAttribute<Certificate, Integer> date;
    public static volatile SingularAttribute<Certificate, String> name;
    public static volatile SingularAttribute<Certificate, String> details;
    public static volatile SingularAttribute<Certificate, Integer> id;
    public static volatile SingularAttribute<Certificate, Users> userid;

}