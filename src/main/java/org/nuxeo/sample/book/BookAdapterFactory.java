package org.nuxeo.sample.book;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public class BookAdapterFactory implements DocumentAdapterFactory{

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if(Book.class.equals(itf) && BookAdapter.TYPE.equals(doc.getType())) {
            return new BookAdapter(doc);
        }
        return null;
    }

}
