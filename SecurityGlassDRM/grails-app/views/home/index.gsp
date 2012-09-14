<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Non signed in home</title>
  </head>

  <body>
    <div class="row-fluid">

      <section id="main">

        <div class="hero-unit row">
          <div class="page-header span12">
            <h1>Welcome to SecurityGlass DRM</h1>
          </div>
          
          <p>Insert some text here about what the system provides, etc.</p>
          
        </div>
        
        <div class="row">
          <div class="span6">
            <h2>Already a member?</h2>
            <p>Welcome back. Log in to see your data</p>
            <g:link controller="login" action="index" class="btn btn-primary">Login</g:link>
          </div>

          <div class="span6">
            <h2>New here?</h2>
            <p>Some text saying why you should sign up</p>
            <g:link controller="register" action="index" class="btn btn-primary">Register</g:link>
          </div>
        </div>
          
      </section>

    </div>
  </body>
</html>
