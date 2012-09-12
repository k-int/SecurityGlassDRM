<!doctype html>
<html>

<head>
      <meta name="layout" content="bootstrap"/>

	
	<title><g:message code='spring.security.ui.register.title'/></title>
    <r:use module="jquery-validate"/>
    <jqval:resources/>
</head>

<body>

<p/>

    
    <div class="row-fluid">

      <section id="main" class="span12">

        <div class="hero-unit">
          <div class="page-header">
            <h1>Create an account</h1>
          </div>
        </div>

          <div class="row">
            <div class="span12">
<g:form action='register' name='registerForm'>

	<g:if test='${emailSent}'>
	<br/>
	<g:message code='spring.security.ui.register.sent'/>
	</g:if>
	<g:else>

	<br/>

	<table>
	<tbody>

		<s2ui:textFieldRow name='username' labelCode='user.username.label' bean="${command}"
                         size='40' labelCodeDefault='Username' value="${command.username}" autocomplete="off"/>

		<s2ui:textFieldRow name='email' bean="${command}" value="${command.email}"
		                   size='40' labelCode='user.email.label' labelCodeDefault='E-mail' autocomplete="off"/>

		<s2ui:passwordFieldRow name='password' labelCode='user.password.label' bean="${command}"
                             size='40' labelCodeDefault='Password' value="${command.password}" autocomplete="off"/>

		<s2ui:passwordFieldRow name='password2' labelCode='user.password2.label' bean="${command}"
                             size='40' labelCodeDefault='Password (again)' value="${command.password2}" autocomplete="off"/>

	</tbody>
	</table>

        <input type="submit" value="Create your account" class="btn btn-primary"/>

	</g:else>

</g:form>

            </div>
          </div>
          
          
      </section>

    </div>

<script>
$(document).ready(function() {
	$('#username').focus();
});
</script>

</body>
</html>
