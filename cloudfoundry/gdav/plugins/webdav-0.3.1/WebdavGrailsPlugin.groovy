import org.codehaus.groovy.grails.webdav.ContextWebdavServlet

class WebdavGrailsPlugin {
    String version = '0.3.1'
    String grailsVersion = '1.1 > *'

    def author = "Stefan Armbruster"
    def authorEmail = "stefan@armbruster-it.de"
    def title = "Webdav plugin for grails"
    def description = '''\\
This plugin provides a WEBDAV interface for grails. It's core functionalitiy is to
 provide support for exposing your domain model to a webdav interface.

For the underlying webdav stuff, the webdav-servlet project (http://webdav-servlet.sourceforge.net/index.html)
is used. Thanks to those folks for providing a open source webdav servlet implementation.
So the plugin code itself is nothing more than a small integration layer.

To use webdav in your app you need to take the following steps:

1) add a webdav definition part to your Config.groovy

Example 1:
    grails.webdav =  [
        webdav: [
                url: '/webdav/*',
                init: [
                        rootpath: System.properties.'java.io.tmpdir',
                        storeDebug: 1,
                        'no-content-length-headers': 0,
                        lazyFolderCreationOnPut: 0
                ]
        ]
    A webdav share with the internal name 'webdav' is accessible under http://localhost:8080/<app>/webdav

Example 2:

    grails.webdav =  [
        webdav1: [ url: '/webdav_folder1/*' ],
        webdav2: [ url: '/webdav_folder2/*' ],
        webdav3: [
            url: '/filesystemview/*,
            init: [
                rootpath: System.properties.'java.io.tmpdir',
                storeDebug: 1,
                'no-content-length-headers': 0,
                lazyFolderCreationOnPut: 0
            ]

    ]

    Provides three webdav shares: webdav1 maps to http://localhost:8080/<app>/webdav_folder1
     and webdav2 maps to http://localhost:8080/<app>/webdav_folder2. The third one provides
     some servlet init parameters.

Buy default no webdav folder is enabled.

2) implement a service class implementing WebdavMapper for each entry in your webdav configuration
This class defines the contents of your webdav shares.

There's a special naming convention for these service classes:
 WebdavMapper_<internalname>Service.groovy

So for the above example 1 you have to implement WebdavMappper_webdavService,
for example 2 you need WebdavMapper_webdav1Service and WebdavMapper_webdav2Service.
These service should be in the root package (aka no package declaration)

If the plugin does not find your service class for a specific webdav share, it falls
 back and uses the net.sf.webdav.LocalFileSystemStore to expose a local directory to webdav.

3) prevent Grails UrlMapping acting on Webdav-URLs: add an exlcude setting in your UrlMappings.groovy
Example:
     class UrlMappings {
    static mappings = {
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
	  "500"(view:'/error')
      static excludes = ["/webdav_folder1/*"]  // repeat this one for each of your webdav shares
	}


'''

    // TODO: support onChange

    def documentation = "http://grails.org/Webdav+Plugin"
    def watchedResources = ["file:./grails-app/conf/*Config.groovy"]
    def FILTER_NAME = "webdavFilter"

    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->
    }

    /**
    * modify web.xml
    * <ol>
    * <li>add a servlet for each webdav specification </li>
    * <li>add a {@link org.codehaus.groovy.grails.orm.hibernate.support.GrailsOpenSessionInViewFilter} for the webdav servlets</li>
    * </ol
    */
    def doWithWebDescriptor = { xml ->

        def cfg = application.config.grails.webdav
        if (cfg) {
            appendToWebDescriptor( xml, 'filter', ['filter-name':FILTER_NAME, 'filter-class': org.codehaus.groovy.grails.orm.hibernate.support.GrailsOpenSessionInViewFilter.name])
            cfg.each { servletName, config ->
                log.debug "config: $servletName -> $config"

                appendToWebDescriptor( xml, 'servlet', {
                    servlet {
                        'servlet-name'(servletName)
                        'servlet-class'(ContextWebdavServlet.name)
                        'load-on-startup'(0)
                        config.init.each { name, value ->
                            'init-param' {
                                'param-name'(name)
                                'param-value'(value)
                            }
                        }
                    }
                })
                appendToWebDescriptor( xml, 'servlet-mapping', [ 'servlet-name':servletName, 'url-pattern': config.url])
                appendToWebDescriptor( xml, 'filter-mapping', ['filter-name':FILTER_NAME, 'url-pattern': config.url])
            }
        } else {
            log.error("webdav disabled. Add 'grails.webdav' section to Config.groovy.")
        }
    }

    private lastChild(def node, def tag) {
        def children = node[tag]
        children[children.size()-1]
    }

    private appendToWebDescriptor(def node, def tag, Closure append) {
        lastChild(node,tag) + append
    }

    private appendToWebDescriptor(def node, def tag, Map append) {
        lastChild(node, tag) + {
                "$tag" {
                    append.each { k,v ->
                        "$k"(v)
                    }                    
                }
        }
    }

    def doWithDynamicMethods = { ctx ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }
}
