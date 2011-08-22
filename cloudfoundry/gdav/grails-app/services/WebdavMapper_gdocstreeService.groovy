import org.codehaus.groovy.grails.webdav.WebdavObject
import org.codehaus.groovy.grails.webdav.WebdavLeafish
import org.codehaus.groovy.grails.webdav.WebdavFolderish
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.webdav.AbstractWebdavMapperService

import javax.tools.FileObject

import org.apache.commons.io.IOUtils

import com.google.gdata.client.docs.DocsService
import com.google.gdata.data.docs.DocumentListFeed

/**
 * maps the Folder-FileObject part of the domain model to webdav
 */
class WebdavMapper_gdocstreeService extends AbstractWebdavMapperService {

    boolean transactional = true

    protected List<WebdavObject> getRootWebdavObjects() {

		// Create a new Documents service
		DocsService client = new DocsService("com.freesundance-gdav-0.1");
		client.setUserCredentials("pjskyboy@gmail.com", "skyboy125");
		
		// Get a list of all entries
		URL metafeedUrl = new URL("http://docs.google.com/feeds/default/private/full");
		log.info("Getting Documents entries...");
		DocumentListFeed resultFeed = client.getFeed(metafeedUrl, DocumentListFeed.class);
		
		def entries = resultFeed.getEntries()

		
        Folder.findAllByParentIsNull().collect {
            ProxyGenerator.instantiateDelegate([WebdavFolderish], it)
        }
    }

    /**
     * implement webdavXXXX methods for domain classes
     */
    protected setupWebdavMethods() {

        def folder = Folder.metaClass
        folder.webdavName << { name }
        folder.webdavCreated << { dateCreated }
        folder.webdavLastModified << { lastUpdated }
        folder.webdavChildren << {
            def allChilds = []
            allChilds += folders.collect { ProxyGenerator.instantiateDelegate([WebdavFolderish], it) }
            allChilds += files.collect { ProxyGenerator.instantiateDelegate([WebdavLeafish], it) }
            return allChilds.flatten()
            images.collect {

            }
        }
        folder.webdavCreateResource << { String name ->
            addToFiles(name:name).save()
        }
        folder.webdavCreateFolder << { String name ->
            addToFolders(name:name).save()
        }
        folder.webdavRemove << { String name ->
            def obj = folders.findByName(name)
            removeFromFolders(obj)
            obj = files.findByName(name)
            removeFromFiles(obj)
        }

        def file = FileObject.metaClass
        file.webdavName << { name }
        file.webdavCreated << { dateCreated }
        file.webdavLastModified << { lastUpdated }
        file.webdavLength << { data?.size() }
        file.webdavReadData << {
            new ByteArrayInputStream(data)
        }
        file.webdavWriteData << {InputStream is ->
            data = IOUtils.toByteArray(is)
            return webdavLength()
        }
   }
}
