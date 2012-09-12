<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Context home</title>
  </head>

  <body>
    <div class="row-fluid">
      <section id="main" class="span12">

        <div class="hero-unit row">
            <div class="span8">
                <h1>${specifiedContext} Context</h1>
            </div>
            <div class="span4">

          <g:if test="${userContexts}">

              <h3>Your contexts:</h3>

              <form id="contextForm" name="contextForm" action="/" method="POST">
                <g:select id="contextChoice" name="contextChoice" from="${userContexts}" value="${specifiedContext}" optionKey="name" optionValue="name"/>

                <div class="btn-group pull-right">
                    <input type="button" class="btn btn-primary" value="Go" name="contextChoiceButton" id="contextChoiceButton"/>
                    <g:link controller="context" action="createContext" class="btn">New</g:link>
                </div>
              </form>


          </g:if>
          <g:else>
            <p class="unimportant">
                Log in to see your available contexts
            </p>
          </g:else>

            </div>
            
        </div>

        <div class="row">

            <div class="span12">
                    
          <h2>Stores within this context:</h2>
          <g:if test="${stores}" >
	          <ul>
	            <g:each in="${stores}" var="store">
	              <li><a href="${specifiedContext}/${store.name}">${store.name}</li>
	            </g:each>
	          </ul>
          </g:if>
          <g:else>
            <p class="unimportant">
                There are currently no stores in this context. Create one to start importing content.
            </p>
          </g:else>
          
          <g:link controller="store" action="createContentStore" params="[storeOwner:specifiedContext]" class="btn">Create new content store</g:link>
          
          
            </div>          

        </div>
          
      </section>

      
      <script type="text/javascript">
        
         $(document).ready(function (){
           

            $('#contextChoiceButton').click( function() {
              var chosenContext = $('#contextChoice').val();

              if ( chosenContext != "" ) {
                $('#contextForm').prop("action",""+chosenContext);
                
                $('#contextForm').submit();
              } else {
                return false;
              }
            });

         });
        
      </script>
    </div>
  </body>
</html>
