package org.codehaus.groovy.grails.webdav
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

import org.codehaus.groovy.grails.webdav.WebdavMapper
import org.codehaus.groovy.grails.webdav.WebdavObject
import org.codehaus.groovy.grails.webdav.WebdavFolderish
import org.springframework.beans.factory.InitializingBean

/**
 * abstract service to be extended by all WebdavMapper implementations
 *
 * @author Stefan Armbruster
 */
abstract class AbstractWebdavMapperService implements WebdavMapper, InitializingBean {

    boolean transactional = true
    static ROOT_NAME = "WEBDAV ROOT OBJECT"
    Date rootCreated = new Date()
    Date rootModified = new Date()

    /**
     * @see WebdavMapper#getWebdavObject(String)
     */
    public WebdavObject getWebdavObject(String path) {

        def parts = path?.tokenize("/")
        log.debug "retrieving webdav object for $path: $parts, ${parts?.size()}"

        // walk down the tree
        def result = parts.inject(getRoot()) {WebdavFolderish res, String name ->
            res?.webdavChildren()?.find { it.webdavName() == name }
        }
        log.info "result for path $path is $result"
        return result
    }

    /**
     * @return a dummy object implementing  {@link WebdavFolderish}  that acts a root folder
     * containing all objects returned from  {@link #getRootWebdavObjects()}
     */
    protected WebdavObject getRoot() {
        log.debug "root object requsted"
        [
                webdavChildren: { getRootWebdavObjects() },
                webdavCreateFolder: {String n -> createRootFolder(n) },
                webdavCreateResource: {String n -> createRootFile(n) },
                webdavRemove: {String n -> removeObject(n) },
                webdavName: { -> ROOT_NAME },
                toString: { -> ROOT_NAME },
                webdavLastModified: { -> rootModified },
                webdavCreated: { -> rootCreated},

        ] as WebdavFolderish
    }

    /**
     * @return a List of domain objects that should appear in the top-level webdav folder
     * if the objects do not implement  {@link WebdavFolderish}  or  {@link org.codehaus.groovy.grails.webdav.WebdavLeafish}
     * you might us a ProxyGenerator to mimic that behaviour
     */
    protected abstract List<WebdavObject> getRootWebdavObjects()

    protected void createRootFolder(String name) {
        throw new UnsupportedOperationException()
    }

    protected void createRootFile(String name) {
        throw new UnsupportedOperationException()
    }

    protected void removeObject(String name) {
        throw new UnsupportedOperationException()
    }

    /**
     * use this placeholder method for implementing the webdavXXXXX methods for your domain
     * classes 
     */
    protected abstract setupWebdavMethods()

    public void afterPropertiesSet() {
        setupWebdavMethods()
    }


}