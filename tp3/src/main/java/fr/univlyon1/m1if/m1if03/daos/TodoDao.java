package fr.univlyon1.m1if.m1if03.daos;

import fr.univlyon1.m1if.m1if03.classes.Todo;
import fr.univlyon1.m1if.m1if03.daos.AbstractListDao;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TodoDao extends AbstractListDao<Todo> {
    /*
    public TodoDao(){
        super.collection =
    }

    @Override
    public Serializable add(Todo element) {
       return super.add(element);
    }

    @Override
    public Serializable getId(Todo element) {
        return element.hashCode();
    }

    @Override
    public Todo findOne(Serializable element) throws NameNotFoundException, InvalidNameException{
        return super.findOne(element.hashCode());
    }

    @Override
    public Collection<Todo> findAll() {
        return super.findAll();
    }*/
}
