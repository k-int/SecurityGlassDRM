<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>404 - Page not found</title>
  </head>

  <body>
    <div class="row-fluid">

      <section id="main" class="span12">

        <div class="hero-unit">
          <h1>404 - Page not found</h1>
          
          <g:if test="${flash.message}">
            <p>${flash.message}</p>
          </g:if>
          <g:else>
            <p>The requested resource could not be found within the system</p>
          </g:else>

          <p>Click <g:link controller="/" action="">here</g:link> to view the system front page</p>
        </div>
          
      </section>

    </div>
  </body>
</html>
