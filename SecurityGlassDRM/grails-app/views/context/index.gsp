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
          <h1>${specifiedContext} Context</h1>

          <h2>Your contexts:</h2>
          <form id="contextForm" name="contextForm" action="/" method="POST">
            <g:select id="contextChoice" name="contextChoice" from="${userContexts}" value="${specifiedContext}" optionKey="name" optionValue="name"/>
            
            <input type="button" class="btn btn-primary" value="Go" name="contextChoiceButton" id="contextChoiceButton"/>
          </form>
          
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
