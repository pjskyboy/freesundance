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
 * the leafs of the webdav tree must implement this interface
 * @author Stefan Armbruster
 */
public interface WebdavLeafish extends WebdavObject {

    /**
     * @return length of the given object in bytes
     */
    int webdavLength()

    /**
     * @return binary data of the current resource
     */
    InputStream webdavReadData()
    
    /**
     * set the value of the current resource
     * @return length of the current resource
     */
    int webdavWriteData(InputStream is)
}