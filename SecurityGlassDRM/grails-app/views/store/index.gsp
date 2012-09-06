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
          <a href="#editConnectorModal" role="button" id="hiddenEditConnectorLink" name="hiddenEditConnectorLink" class="btn" data-toggle="modal" style="display:none">Hidden edit link</a>
          
          
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
         <form id="connectorForm" name="connectorForm" action="${specifiedStore}/connectors/admin/add" method="POST">
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
              <input type="button" name="addConnectorSubmit" id="addConnectorSubmit" value="Add" class="btn btn-primary"/>
              <button type="button" class="btn btn-danger" data-dismiss="modal" aria-hidden="true">Cancel</button>
          </div>
        </div>
      </div>

      <div class="modal hide fade" id="editConnectorModal" name="editConnectorModal" tabIndex="-1"  role="dialog" aria-labelledby="modalEditConnectorLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h3>Edit connector</h3>
        </div>
        <div class="modal-body">
         <form id="editConnectorForm" name="editConnectorForm" action="${specifiedStore}/connectors/admin/edit/-ID-" method="POST">
              <dl>
                <dt><label for="editOaiName">Id:</label></dt>
                <dd><input type="text" name="editOaiId" id="editOaiId" value="" class="required" readonly="readonly"/></dd>

                <dt><label for="editOaiName">Name:</label></dt>
                <dd><input type="text" name="editOaiName" id="editOaiName" value="" class="required"/></dd>

                <dt><label for="editOaiUrl">OAI URL:</label></dt>
                <dd><input type="text" name="editOaiUrl" id="editOaiUrl" value="" class="required url"/></dd>

                <dt><label for="editOaiSet">Set:</label></dt>
                <dd><input type="text" name="editOaiSet" id="editOaiSet" value="" /></dd>

                <dt><label for="editOaiPrefix">Metadata Prefix:</label></dt>
                <dd><input type="text" name="editOaiPrefix" id="editOaiPrefix" value="" class="required"/></dd>

                <dt><label for="editOaiEncoding">Encoding:</label></dt>
                <dd><input type="text" name="editOaiEncoding" id="editOaiEncoding" value="UTF-8" class="required" /></dd>

              </dl>
          </form>
        </div>
        <div class="modal-footer">
          <div class="btn-group pull-right">
              <input type="button" name="editConnectorSubmit" id="editConnectorSubmit" value="Save" class="btn btn-primary"/>
              <button type="button" class="btn btn-danger" data-dismiss="modal" aria-hidden="true">Cancel</button>
          </div>
        </div>
      </div>

      <script type="text/javascript">
        
        $(document).ready(function() {
        
        
          $('#uploadForm').validate();
          $('#connectorForm').validate({            
               rules: {
              'oaiName': {
                remote: {
                  url: "${specifiedStore}/connectors/admin/checkConnectorName",
                  type: "post",
                  data: {
                    name: function() {
                      return $('#oaiName').val();
                    }
                  }
                }
              },
              'oaiUrl': {required: true, url: true},
              'oaiPrefix': {required: true},
              'oaiEncoding':{required: true}
            },
            messages: {
              'oaiName': {
                remote: "The specified name is already in use for this store. Please use another name that is not already in use."
              }
            }
          });
          var editValidator = $('#editConnectorForm').validate({
            rules: {
              'editOaiName': {
                remote: {
                  url: "${specifiedStore}/connectors/admin/checkConnectorName",
                  type: "post",
                  data: {
                    name: function() {
                      return $('#editOaiName').val();
                    },
                    connectorId: function() {
                      return $('#editOaiId').val();
                    }
                  }
                }
              },
              'editOaiUrl': {required: true, url: true},
              'editOaiPrefix': {required: true},
              'editOaiEncoding':{required: true}
              
            },
            messages: {
              'editOaiName': {
                remote: "The specified name is already in use for this store. Please use another name that is not already in use."
              }
            }
          });
                    
          $('#uploadSubmit').click(function() {
              $('#uploadForm').submit();
          });
          $('#addConnectorSubmit').click(function() {
              $('#connectorForm').submit();
          });
          $('#editConnectorSubmit').click(function() {
              $('#editConnectorForm').submit();
          });

          setupConnectors();
          
        });


        function setupConnectors() {
          $.getJSON("${specifiedStore}/connectors/list", function() {
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
        }
        
        function processConnectors(json) {
          // Takes json of format: ["connectors": allConnectors, "isOwner": isOwner]
          
          var isOwner = json.isOwner;

          var connectorContainer = $('#connectorsDiv');
          connectorContainer.html('');

          // Add in the information about the different connectors
          var connectorList = $("<dl></dl>");
          
          if ( json.connectors != null && json.connectors.length > 0 ) {
            // There are some connectors..
            $.each(json.connectors, function() {
              var dt = $("<dt></dt>");
              dt.append(this.name);
              if ( isOwner ) {
                dt.append("<a href='#' onclick='editConnector("+this.id+");'><i class='icon-edit pull-right'></i></a>");
                dt.append("<a href='#' onclick='deleteConnector("+this.id+",\""+this.name+"\");'><i class='icon-trash pull-right'></i></a>");
              }
              connectorList.append(dt);
              connectorList.append("<dd>" + this.url + "</dd>");
              connectorList.append("<dd>" + this.setSpec + "</dd>");
              connectorList.append("<dd>" + this.metadataPrefix + "</dd>");
              connectorList.append("<hr class='minimalMargin'/>");
            });
          } else {
            // No connectors yet - just add a message
            var dt = $("<dt class='noContentMessage'></dt>");
            dt.append("<i>No connectors currently added to this store</i>");
            
            connectorList.append(dt);
          }
          connectorContainer.append(connectorList);

          if ( isOwner ) {
            
            // Add a button to create a new repo connector
            var addButton = $('<a class="btn" id="addRepoButton" name="addRepoButton" data-toggle="modal" href="#addConnectorModal">Add Connector</a>');
            
            connectorContainer.append(addButton);
            
          } else {
            // Don't enable editing / creating of new connectors
          }


        }
        
        function editConnector(connId) {
          
          // Go and get the connector information from the server and pass it onto the method to 
          // populate the modal window
          $.getJSON("${specifiedStore}/connectors/show/" + connId, function() {
          })
          .success(function(json) { 
            processEditConnector(connId, json);
            
          })
          .error(function(jqXHR, textStatus, errorThrown) {
            alert("Error getting back relevant repository connectors");
            console.log("error " + textStatus);
            console.log("incoming Text " + jqXHR.responseText);
          })
          .complete(function() {});
        }

        function processEditConnector(connId, json) {
          // Populate the modal window with the relevant connector information
          
          // Set up the form action so that it will post to the correct place
          $('#editConnectorForm').attr('action', '${specifiedStore}/connectors/admin/edit/' + connId);
          
          // Now populate the form inputs
          $("#editOaiId").removeData("previousValue");
          $('#editOaiId').val(json.id);
          $("#editOaiName").removeData("previousValue");
          $('#editOaiName').val(json.name);
          $("#editOaiUrl").removeData("previousValue");
          $('#editOaiUrl').val(json.url);
          $("#editOaiSet").removeData("previousValue");
          $('#editOaiSet').val(json.setSpec);
          $("#editOaiPrefix").removeData("previousValue");
          $('#editOaiPrefix').val(json.metadataPrefix);
          $("#editOaiEncoding").removeData("previousValue");
          $('#editOaiEncoding').val(json.encoding);
          
          $('#editConnectorForm').validate().resetForm();
          
          // Simulate the click on the reveal button
          $('#hiddenEditConnectorLink').click();
        }

        function deleteConnector(connId,connName) {
          
          var continueDel = confirm("Are you sure you want to delete the connector with name: " + connName + "? This cannot be undone.");
          
          if ( continueDel ) {
            // User wants to perform the deletion..
            
            $.getJSON("${specifiedStore}/connectors/admin/delete/" + connId, function() {})
            .success(function(json) { 
              // Reload the connector information on the page
              setupConnectors();

            })
            .error(function(jqXHR, textStatus, errorThrown) {
              alert("Error deleting the repository connectors");
              console.log("error " + textStatus);
              console.log("incoming Text " + jqXHR.responseText);
            })
            .complete(function() {});
          } else {
            // Deletion cancelled - don't do anything
          }
        }
        
      </script>
    </div>
  </body>
</html>
