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

/**
 * abstract base interface for all webdavobjects
 * @author Stefan Armbruster
 */
public abstract interface WebdavObject {
    /**
     * the name of the webdav object. It might be useful to use apropiate filename extenstions.
     * e.g. if your objects represents a XML snippets, it's name should end with ".xml".
     * This way the client associates it with an XML Editor
     */
    String webdavName()
    /**
     * Last modified timestamp of the of the webdav object. Default is the current time.
     */
    Date webdavLastModified()
    /**
     * Creation timestamp of the of the webdav object. Default is the current time.
     */
    Date webdavCreated()
}
