<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Context home</title>
    <r:use module="jquery-validate"/>
    <jqval:resources/>

  </head>

  <body>
    <div class="row-fluid">
<!--      
      <aside id="application-status" class="span3">
        <div class="well sidebar-nav">
        </div>
      </aside>-->

      <section id="main">

        <div class="hero-unit row">
            <div class="span8">
	          <div class="page-header">
	            <h1>New organisation</h1>
	          </div>
            </div>
            <div class="span4">
               <g:render template="/contextChooser"/>
            </div>
        </div>


          <div class="row span12">
          <g:if test='${flash.error}'>
            <div class="errors">
              <ul>
                <li class='error'>${flash.error} </li>
              </ul>
            </div>
          </g:if>
          
          <form action="createContext" id="createContextForm" name="createContextForm" method="POST">
            
            <dl>
              <dt><label for="contextName">Organisation name</label></dt>
              <dd><input id="contextName" name="contextName" type="text" value="${contextName}" /></dd>
            </dl>
            
            <div class="btn-group">
                <input type="submit" value="Create" class="btn btn-primary"/>
                <g:link class="btn btn-danger" controller="home">Cancel</g:link>
            </div>
          </form>

          </div>
          
      </section>

    </div>
    
    <script type="text/javascript">


       $(document).ready(function (){
         
                $("#contextName").change(function () {
                   $("#contextName").removeData("previousValue");
                });

		$("#createContextForm").validate({
			rules: {
				'contextName': {
					remote: {
						url: "${createLink(controller:'context', action:'checkNewContextName')}",
						type: "post",
						data: {
							contextName: function() {
								return $("#contextName").val();
                                                        }
						}
					  }
				}
			},
			messages: {
				'contextName': {
					remote: "The specified organisation name is already in use or is invalid. Please try again. Good names are short and memorable."
				}
			}
		});
	});
        
    </script>

  </body>
</html>
