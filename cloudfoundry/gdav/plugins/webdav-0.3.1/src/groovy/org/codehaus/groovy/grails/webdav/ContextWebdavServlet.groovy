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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import net.sf.webdav.WebdavServlet
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.webdav.GrailsWebdavStore
import org.codehaus.groovy.grails.webdav.WebdavMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.util.UrlPathHelper

/**
 * this servlet implementation is aware of the Spring context and tries to configures itself from the context
 * the convention used here: use a spring bean named "webdavMapper_<servletname>Service". This corresponds to a service
 * named WebdavMapper_<servletName>
 * <servletName> is specified in Config.groovy
 */

public class ContextWebdavServlet extends WebdavServlet {

    static Logger log = LoggerFactory.getLogger(ContextWebdavServlet.class);
    private UrlPathHelper urlHelper = new UrlPathHelper()

    /**
     * tries to configure the IWebdavStore from the application context, fall back to webdav-servlet's default behaviour
     * if no matching bean is found
     */
    void init() {

        def applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext)
        def beanName = "webdavMapper_${servletName}Service"
        try {
            WebdavMapper mapper = applicationContext.getBean(beanName, WebdavMapper.class)
            log.info "successfully found a bean $beanName"
            def store = new GrailsWebdavStore(mapper:mapper)
            init(store, null, null, 0, false)
        } catch (BeansException e) {
            //log.warn e.message
            log.warn "found no bean '$beanName', falling back to default behaviour: use LocalFileSystemStore"
            super.init()
        }
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        def attr = req.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE)
        if (attr) {
            log.error "UrlMappingsFilter was already applied. This should not be the case for webdav servlets."
            log.error "Suggestion: add '${urlHelper.getPathWithinApplication(req)}*' to your 'excludes' list in UrlMappings.groovy"
            log.error "for details: see http://jira.codehaus.org/browse/GRAILS-3450"
        }
        if (log.isDebugEnabled()) {
            req.attributeNames.toList().sort().each {
                log.debug "request attr $it = ${req.getAttribute(it)}"
            }
        }

        if (!req.pathInfo) {
             log.debug "servletPath $req.servletPath"
             log.debug "contextPath $req.contextPath"
             log.debug "queryString $req.queryString"
             log.debug "pathInfo $req.pathInfo"
             log.info "pathTranslated $req.pathTranslated"
             def redirect = "${req.contextPath}/${req.servletPath}/"
             log.info "doing redirect to $redirect"
             resp.sendRedirect(redirect)
        } else {
            super.service(req, resp)
        }
    } 

}