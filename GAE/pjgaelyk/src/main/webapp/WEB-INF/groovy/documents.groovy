
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.extensions.LastModifiedBy;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * This is a test template
 */

// Create a new Documents service
DocsService client = new DocsService("com.sundance-hello-v1");
client.setUserCredentials("pjskyboy@gmail.com", "skyboy125");

// Get a list of all entries
URL metafeedUrl = new URL("http://docs.google.com/feeds/default/private/full");
log.info("Getting Documents entries...");
DocumentListFeed resultFeed = client.getFeed(metafeedUrl, DocumentListFeed.class);

def entries = resultFeed.getEntries()

request.setAttribute "documentlist", entries

log.info("Total Entries: "+ entries.size());

forward '/documents.gtpl'



