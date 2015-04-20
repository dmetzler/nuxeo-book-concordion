package org.nuxeo.sample.book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.types.QName;

public class BookAdapter implements Book {

    private static final String DC_PREFIX = "dc";

    private static final String SCHEMA_PREFIX = "bk";

    public static final String TYPE = "Book";

    private static final QName DC_TITLE = new QName("title", DC_PREFIX);

    private static final QName DC_DESCRIPTION = new QName("description", DC_PREFIX);

    private static final QName BO_AUTHOR = new QName("author", SCHEMA_PREFIX);

    private static final QName BO_PUBYEAR = new QName("publicationYear", SCHEMA_PREFIX);

    private static final QName BO_ISBN = new QName("isbn", SCHEMA_PREFIX);

    private static final QName DC_CREATED = new QName("created", DC_PREFIX);;

    private static final QName DC_MODIFIED = new QName("modified", DC_PREFIX);;

    private DocumentModel doc;

    public BookAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    public static void importCSV(CoreSession session, File file) throws IOException {
        Reader in = new BufferedReader(new FileReader(file));
        CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(in);

        // isbn;title;author;pubdate;description
        for (CSVRecord record : parser) {
            String isbn = record.get("isbn");
            String title = record.get("title");
            String author = record.get("author");
            String pubYear = record.get("pubdate");
            String description = record.get("description");

            Book book = create(session, "/", title);
            book.setIsbn(isbn);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublicationYear(pubYear);
            book.setDescription(description);
            book = book.create(session);

        }
    }

    private static Book create(CoreSession session, String parentPath, String name) {
        return session.createDocumentModel(parentPath, name, TYPE).getAdapter(Book.class);
    }

    public static Optional<Book> findByISBN(CoreSession session, String isbn) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", TYPE, BO_ISBN.getPrefixedName(), isbn);
        return session.query(query).stream().map(doc -> doc.getAdapter(Book.class)).findFirst();
    }

    @Override
    public void setTitle(String title) {
        safeSetString(DC_TITLE, title);
    }

    @Override
    public String getTitle() {
        return doc.getProperty(DC_TITLE.getPrefixedName()).getValue(String.class);
    }

    @Override
    public void setAuthor(String author) {
        safeSetString(BO_AUTHOR, author);
    }

    @Override
    public String getAuthor() {
        return safeGetString(BO_AUTHOR);
    }

    @Override
    public void setPublicationYear(String pubYear) {
        safeSetString(BO_PUBYEAR, pubYear);
    }

    @Override
    public String getPublicationYear() {
        return safeGetString(BO_PUBYEAR);
    }

    @Override
    public void setDescription(String description) {
        safeSetString(DC_DESCRIPTION, description);
    }

    @Override
    public String getDescription() {
        return safeGetString(DC_DESCRIPTION);
    }

    @Override
    public void setIsbn(String isbn) {
        safeSetString(BO_ISBN, isbn);
    }

    @Override
    public String getIsbn() {
        return safeGetString(BO_ISBN);
    }

    @Override
    public Book create(CoreSession session) {
        return session.createDocument(this.doc).getAdapter(Book.class);
    }

    @Override
    public DateTime getDateCreation() {
        Calendar date = doc.getProperty(DC_CREATED.getPrefixedName()).getValue(Calendar.class);
        return date != null ? new DateTime(date) : null;
    }

    @Override
    public DateTime getDateModification() {
        Calendar date = doc.getProperty(DC_MODIFIED.getPrefixedName()).getValue(Calendar.class);
        return date != null ? new DateTime(date) : null;
    }

    private void safeSetString(QName prop, String value) {
        doc.setPropertyValue(prop.getPrefixedName(), value.replace("the Ring", "my Precious"));
    }

    private String safeGetString(QName prop) {
        return doc.getProperty(prop.getPrefixedName()).getValue(String.class);
    }

    public static List<DocumentModel> findAll(CoreSession session) {
        String query = "SELECT * FROM " + TYPE;
        return session.query(query);
    }

}
