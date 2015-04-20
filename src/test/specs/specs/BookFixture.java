package specs;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.sample.book.Book;
import org.nuxeo.sample.book.BookAdapter;

import com.google.inject.Inject;

@RunWith(ConcordionFeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({"org.nuxeo.sample.book.sbe","org.nuxeo.ecm.platform.dublincore"})
public class BookFixture {

    @Inject
    CoreSession session;

    public void importCSV(String fileName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource != null) {
            File file = new File(resource.toURI());
            BookAdapter.importCSV(session,file);
            session.save();
        } else {
            throw new RuntimeException(String.format("File %s does not exists", fileName));
        }
    }

    public int getDocCount(String docType) {
        return BookAdapter.findAll(session).size();

    }

    public Book findBookByISBN(String isbn) {
        return BookAdapter.findByISBN(session,isbn).orElse(null);
    }

    public boolean isCreatedNow(Book book) {
        return isNow(book.getDateCreation());
    }

    public boolean isModifiedNow(Book book) {
        return isNow(book.getDateModification());
    }

    protected boolean isNow(DateTime date) {
        return date != null ? new DateTime().isBefore(date.plusSeconds(10)) : false;
    }

}
