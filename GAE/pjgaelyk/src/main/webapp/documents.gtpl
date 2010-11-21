<%
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.extensions.LastModifiedBy;
import com.google.gdata.util.AuthenticationException;   

import com.google.gdata.util.ServiceException;

import Utilities;
%>

<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Documents</h1>

<p>
    <%
        log.info ("outputting the doclist attribute")
        
        for (DocumentListEntry entry : request.getAttribute('documentlist')) {
        	
	    log.info(Utilities.entryToString(entry))
        }
    %>
    
</p>

<% include '/WEB-INF/includes/footer.gtpl' %>

