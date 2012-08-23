<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Context home</title>
  </head>

  <body>
    <div class="row-fluid">
      <section id="main" class="span12">

        <div class="hero-unit">
          <h1>Welcome to Context index page..</h1>

          <g:link controller="context" action="createContext" class="btn">Create new context</g:link>
          
          <h2>Stores within this context:</h2>
          <ul>
            <g:each in="${stores}" var="store">
              <li><a href="${specifiedContext}/${store.name}">${store.name}</li>
            </g:each>
          </ul>
          
          <g:link controller="store" action="createContentStore" params="[storeOwner:specifiedContext]" class="btn">Create new content store</g:link>
          
          
          
        </div>
          
      </section>

    </div>
  </body>
</html>
