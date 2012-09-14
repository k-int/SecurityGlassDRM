<!doctype html>
<html>
<head>
	<meta name='layout' content='bootstrap'/>
	<title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
  
  <p/>
  
  <div class="row-fluid">
    <section id="main">
    	<div class='hero-unit row'>
          <div class="page-header span12">
            <h1><g:message code="springSecurity.login.header"/></h1>
          </div>

          <g:if test='${flash.message}'>
                  <div class='login_message span12'>${flash.message}</div>
          </g:if>
        </div>

      <div id='login' class="row">
            <div class="span12">

		<form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>
                  <table>
                    <tr>
                      <td>
                      	<label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
                      </td>
                      <td>
                      	<input type='text' name='j_username' id='username'/>
                      </td>
                    </tr>
                    <tr>
                      <td>
                      	<label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
                      </td>
                      <td>
                        <input type='password' name='j_password' id='password'/>
                      </td>
                    </tr>
                    <tr>
                      <td id="remember_me_holder">
                        <input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                      </td>
                      <td>
                        <label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
                      </td>
                    </tr>
                    <tr>
                      <td rowspan="2">
                      	<input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}' class="btn btn-primary"/>
                      </td>
                    </tr>
                  </table>
		</form>
	</div>
      </div>
    </section>
  </div>
<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>
</body>
</html>
