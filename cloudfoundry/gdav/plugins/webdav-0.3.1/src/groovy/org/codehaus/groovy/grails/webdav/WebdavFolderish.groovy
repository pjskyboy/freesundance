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
 * provides the methods required for a object to be used als folderish part in webdav
 * @author Stefan Armbruster
 */
public interface WebdavFolderish extends WebdavObject {

    /**
     * @return a list of children of the this object. All children returned must implement {@link WebdavObject}
     */
    List<WebdavObject> webdavChildren()

    /**
     * create a new folderish object
     * @param name the name of the new folderish object
     */
    void webdavCreateFolder(String name)


    /**
     * create a new resource (aka leaf) object
     * @param name the new resource's name in webdav notation
     */
    void webdavCreateResource(String name)


    /**
     * remove on of the childs by name
     * @param name of the child to be removed
     */
    void webdavRemove(String name)
}