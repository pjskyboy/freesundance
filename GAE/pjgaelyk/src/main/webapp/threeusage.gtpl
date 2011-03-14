<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Three Usage</h1>

<p>
    <%
        log.info "outputting threeusage attribute"
    %>
    ThreeUsage: <%= request.getAttribute('threeusage').getAllowance().toString() %>
</p>

<% include '/WEB-INF/includes/footer.gtpl' %>