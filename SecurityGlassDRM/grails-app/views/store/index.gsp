<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Store home</title>
    <r:require module="jquery-ui"/>
    <r:use module="jquery-validate"/>
    <jqval:resources/>
  </head>

  <body>
    <div class="row-fluid">
      <section id="main" class="span12">

        <div class="hero-unit">
          <h1>${specifiedContext}/${specifiedStore}</h1>

          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>

        </div>
      </section>
      
      <section id="resources" class="row">

        <div class="span8">
          <h2>Resources</h2>
          
          <p>TODO</p>
          
        </div>
          
        <div class="span4">
          
          <h2>Import</h2>
          
            <a href="#uploadModal" role="button" class="btn" data-toggle="modal">Upload file</a>
            
          <h2>Connectors</h2>
          <div id="connectorsDiv"></div>
          
          
        </div>
          
      </section>

      
      <div class="modal hide fade" id="uploadModal" name="uploadModal" tabIndex="-1"  role="dialog" aria-labelledby="modalUploadLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h3>Upload file</h3>
        </div>
        <div class="modal-body">
         <form id="uploadForm" name="uploadForm" action="${specifiedStore}/uploadFile" method="POST">
              <dl>
                <dt><label for="mediaFile">Media file:</label></dt>
                <dd><input type="file" name="mediaFile" id="mediaFile" class="required"/></dd>

                <dt><label for="metadataFile">Metadata file:</label></dt>
                <dd><input type="file" name="metadataFile" id="metadataFile" class="required"/></dd>

              </dl>
          </form>
        </div>
        <div class="modal-footer">
          <div class="btn-group pull-right">
              <input type="button" name="uploadSubmit" id="uploadSubmit" value="Send" class="btn btn-primary"/>
              <button type="button" class="btn btn-danger" data-dismiss="modal" aria-hidden="true">Cancel</button>
          </div>
        </div>
      </div>


      <div class="modal hide fade" id="addConnectorModal" name="addConnectorModal" tabIndex="-1"  role="dialog" aria-labelledby="modalAddConnectorLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h3>Add connector</h3>
        </div>
        <div class="modal-body">
         <form id="connectorForm" name="connectorForm" action="${specifiedStore}/connectors/add" method="POST">
              <dl>
                <dt><label for="oaiName">Name:</label></dt>
                <dd><input type="text" name="oaiName" id="oaiName" value="" class="required"/></dd>

                <dt><label for="oaiUrl">OAI URL:</label></dt>
                <dd><input type="text" name="oaiUrl" id="oaiUrl" value="" class="required url"/></dd>

                <dt><label for="oaiSet">Set:</label></dt>
                <dd><input type="text" name="oaiSet" id="oaiSet" value="" /></dd>

                <dt><label for="oaiPrefix">Metadata Prefix:</label></dt>
                <dd><input type="text" name="oaiPrefix" id="oaiPrefix" value="" class="required"/></dd>

                <dt><label for="oaiEncoding">Encoding:</label></dt>
                <dd><input type="text" name="oaiEncoding" id="oaiSet" value="UTF-8" class="required" /></dd>

              </dl>
          </form>
        </div>
        <div class="modal-footer">
          <div class="btn-group pull-right">
              <input type="button" name="addConnectorSubmit" id="addConnectorSubmit" value="Send" class="btn btn-primary"/>
              <button type="button" class="btn btn-danger" data-dismiss="modal" aria-hidden="true">Cancel</button>
          </div>
        </div>
      </div>

      <script type="text/javascript">
        
        $(document).ready(function() {
        
        
          $('#uploadForm').validate();
          $('#connectorForm').validate();
          
          $('#uploadSubmit').click(function() {
              $('#uploadForm').submit();
          });
          $('#addConnectorSubmit').click(function() {
              $('#connectorForm').submit();
          });


          $.getJSON("newname/connectors", function() {
          })
          .success(function(json) { 
            processConnectors(json);
            
          })
          .error(function(jqXHR, textStatus, errorThrown) {
            alert("Error getting back relevant repository connectors");
            console.log("error " + textStatus);
            console.log("incoming Text " + jqXHR.responseText);
          })
          .complete(function() {});
          
        });
        
        
        function processConnectors(json) {
          // Takes json of format: ["connectors": allConnectors, "isOwner": isOwner]
          
          var isOwner = json.isOwner;
          
          if ( isOwner ) {
            
            // Add a button to create a new repo connector
            var addButton = $('<a class="btn" id="addRepoButton" name="addRepoButton" data-toggle="modal" href="#addConnectorModal">Add Connector</a>');
            var connectorContainer = $('#connectorsDiv');
            
            connectorContainer.append(addButton);
            
            alert("this is the owner");
          } else {
            alert("not the owner");
          }
          
          alert("In the processConnectors method with json" + json.isOwner)
        }
        
      </script>
    </div>
  </body>
</html>
