/* Copyright 2009-2009 Stefan Armbruster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.webdav

import java.security.Principal
import net.sf.webdav.ITransaction
import net.sf.webdav.IWebdavStore
import net.sf.webdav.StoredObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.sf.webdav.exceptions.WebdavException

/**
 * Implement a {@link IWebdavStore} for a Grails application.
 * Delegates all the dirty work to a {@link WebdavMapper} and the methods provided in
 * {@link WebdavFolderish} and {@link WebdavLeafish}
 *
 * @author Stefan Armbruster
 */

// TODO: support authentication

public class GrailsWebdavStore implements IWebdavStore {

    WebdavMapper mapper

    static Logger log = LoggerFactory.getLogger(GrailsWebdavStore.class);

    public ITransaction begin(Principal principal) {
        log.debug "begin"
        return new Transaction(principal:principal)
    }

    public void checkAuthentication(ITransaction transaction) {
        log.debug "checkAuth"
    }

    public void commit(ITransaction transaction) {
        log.debug "commit"
    }

    public void rollback(ITransaction transaction) {
        log.debug "rollback"
    }

    public void createFolder(ITransaction transaction, String folderUri) {
        log.debug "createFolder ${folderUri}"
        childOperation("webdavCreateFolder", transaction.cache, folderUri)
    }

    public void createResource(ITransaction transaction, String resourceUri) {
        log.debug "createResource ${resourceUri}"
        childOperation("webdavCreateResource", transaction.cache, resourceUri)
    }

    private void childOperation(String methodName, def cache, String uri) {
        log.error "uri: $uri"
        def path = uri.tokenize("/")
        if (path.size() > 1) {
            def parentPath = path[0..-2].join("/")  // first n-1 elements of folderUri
            //if (!parentPath) parentPath="/"
            def childName = path[-1]  // last element of folderUri
            WebdavObject obj = findObject(cache, parentPath)
            obj."$methodName"(childName)
        } else {
            throw new WebdavException("could not perform child operation $methodName on $uri")
        }
    }


    public InputStream getResourceContent(ITransaction transaction, String resourceUri) {
        log.debug "createResourceContent " + resourceUri
        WebdavLeafish obj = findObject(transaction.cache, resourceUri)
        obj.webdavReadData()
    }

    public long setResourceContent(ITransaction transaction, String resourceUri, InputStream content, String contentType, String characterEncoding) {
        log.debug "setResouceContent $resourceUri $contentType $characterEncoding"
        WebdavLeafish obj = findObject(transaction.cache, resourceUri)
        obj.webdavWriteData(content)
    }

    public String[] getChildrenNames(ITransaction transaction, String uri) {
        log.debug "getChildrenNames $uri"
        WebdavFolderish obj = findObject(transaction.cache, uri)
        obj.webdavChildren().collect { it.webdavName() }
    }

    public long getResourceLength(ITransaction transaction, String path) {
        log.debug "getResourceLength $path"
        WebdavLeafish leaf = findObject(transaction.cache, path)
        return leaf.webdavLength()
    }

    public void removeObject(ITransaction transaction, String uri) {
        log.debug "removeObject $uri"
        childOperation('webdavRemove', transaction.cache, uri)
    }

    public StoredObject getStoredObject(ITransaction transaction, String uri) {
        log.debug "getStoredObject $uri"
        WebdavObject obj = findObject(transaction.cache, uri)
        log.debug "getStoredObject $uri ${obj}"
        if (obj) {
            def folderish = obj instanceof WebdavFolderish
            int length = folderish ? 0 : obj.webdavLength()
            return new StoredObject(isFolder: folderish, lastModified: obj.webdavLastModified(), creationDate: obj.webdavCreated(), resourceLength: length)
        } else {
            log.debug "returning null"
            return null
        }
    }

    /**
     * wraps the mapper's result in a cache
     */
    protected WebdavObject findObject(def cache, String path ) {
        WebdavObject result = cache[path]
        if (!result) {
            result = mapper.getWebdavObject(path)
            if (result) cache[path] = result
        }
        return result
    }

    void destroy() {
    }
}
