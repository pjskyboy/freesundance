<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Welcome</h1>

<p>
    Congratulations, you've just created your first 
    <a href="http://gaelyk.appspot.com">Gaelyk</a> application.
</p>

<p>
app.id: <%= app.id %>
app.version: <%= app.version %>
app.env.name: <%= app.env.name %>
app.env.version: <%= app.env.version %>
</p>

<p>
    Click <a href="datetime.groovy">here</a> to view the current date/time.
</p>
<p>
    Click <a href="entity.groovy">here</a> to save entity.
</p>
<p>
	Click <a href="_ah/admin">here</a> to view admin tools for this app.
</p>

<p>
    Click <a href="threeusage.groovy">here</a> to view three usage.
</p>

<% include '/WEB-INF/includes/footer.gtpl' %>

