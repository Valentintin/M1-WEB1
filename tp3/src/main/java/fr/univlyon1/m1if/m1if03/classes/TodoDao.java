package fr.univlyon1.m1if.m1if03.classes;

import fr.univlyon1.m1if.m1if03.daos.AbstractListDao;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.Serializable;

public class TodoDao<T extends Todo> extends AbstractListDao<T> {
    public Serializable add(T element) {
       return super.add(element);
    }

    public Serializable getId(T element) {
        return element.hashCode();
    }

    public T findOne(T element) throws NameNotFoundException, InvalidNameException{
        return super.findOne(element.hashCode());
    }
}
