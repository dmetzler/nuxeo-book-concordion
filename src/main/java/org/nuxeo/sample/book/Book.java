package org.nuxeo.sample.book;

import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.CoreSession;

public interface Book {

    void setTitle(String title);
    String getTitle();

    void setAuthor(String author);
    String getAuthor();

    void setPublicationYear(String pubYear);
    String getPublicationYear();

    void setDescription(String description);
    String getDescription();

    void setIsbn(String isbn);
    String getIsbn();

    DateTime getDateCreation();

    DateTime getDateModification();

    Book create(CoreSession session);
}
