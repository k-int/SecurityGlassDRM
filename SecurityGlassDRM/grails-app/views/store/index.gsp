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
          <h1>Welcome to Store index page..</h1>
          Specified context: ${specifiedContext}
          <br/>
          Specified username: ${username}

          
          
          <hr/>
          
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
