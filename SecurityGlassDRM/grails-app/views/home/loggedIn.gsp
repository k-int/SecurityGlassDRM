<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>signed in home</title>
  </head>

  <body>
    <div class="row-fluid">
      <aside id="application-status" class="span3">
        <div class="well sidebar-nav">
          <h5>Application Status</h5>
          <ul>
            <li>App version: <g:meta name="app.version"/></li>
            <li>Grails version: <g:meta name="app.grails.version"/></li>
          </ul>
        </div>
      </aside>

      <section id="main" class="span9">

        <div class="hero-unit">
          <h1>Welcome to Grails</h1>
          home::signedin
          --Your Content Stores--
          <g:link controller="home" action="createContentStore">Create new content store</g:link>
          
          <ul>
            <g:each in="${contentStores}" var="store">
              <li><a href="${store.name}">${store.name}</a></li>
            </g:each>
          </ul>
        </div>
          
      </section>

    </div>
  </body>
</html>
