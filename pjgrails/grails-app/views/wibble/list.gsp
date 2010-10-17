<%@ page import="com.freesundance.Wibble" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Wibble List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${resource(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Wibble</g:link></span>
        </div>
        <div class="body">
            <h1>Wibble List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="value" title="Value" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${wibbleInstanceList}" status="i" var="wibbleInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${wibbleInstance.id}">${fieldValue(bean:wibbleInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:wibbleInstance, field:'name')}</td>
                        
                            <td>${fieldValue(bean:wibbleInstance, field:'value')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${wibbleInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
