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

import net.sf.webdav.ITransaction
import java.security.Principal

/**
 * simple implementation of a {@link ITransaction} holding a cache for queried objects
 *
 * @author Stefan Armbruster
 */

public class Transaction implements ITransaction {
    static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Transaction.class);

    Principal principal

    /**
     * maps from path (String) -> webdavObject
     */
    def cache = [:]

}