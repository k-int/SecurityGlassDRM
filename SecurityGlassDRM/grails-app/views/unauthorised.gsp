<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>403 - Unauthorised request</title>
  </head>

  <body>
    <div class="row-fluid">

      <section id="main" class="span12">

        <div class="hero-unit">
          <h1>403 - Unauthorised request</h1>
          
          <g:if test="${flash.message}">
            <p>${flash.message }</p>
          </g:if>
          
          <p>Click <g:link controller="/" action="">here</g:link> to view the system front page</p>
        </div>
          
      </section>

    </div>
  </body>
</html>
