
<span id="contextChoiceBox">
    
</span>


<script type="text/javascript">

	$(document).ready(function() {

		   // Go and get the list of available contexts for the user
		   $.getJSON("${createLink(uri: '/')}context/listContextsForUser", function() {
	          })
	          .success(function(json) { 
		          populateContextChoiceSection(json);
	          })
	          .error(function(jqXHR, textStatus, errorThrown) {
	            alert("Error getting back relevant context information");
	            console.log("error " + textStatus);
	            console.log("incoming Text " + jqXHR.responseText);
	          })
	          .complete(function() {});

	});

	function populateContextChoiceSection(json) {

		var container = $('#contextChoiceBox');

		if ( json.userContexts ) {
		    // We have data to display
		    container.append("<h3>Your contexts:</h3>");
		    var form = $('<form id="contextForm" name="contextForm" action="/" method="POST"></form>');
		    
		    var selectInput = $('<select id="contextChoice" name="contextChoice"></select>');
		    jQuery.each(json.userContexts, function() {
                selectInput.append('<option value="' + this.name + '">' + this.name + '</option>');
			});
		    form.append(selectInput);

		    var buttonDiv = $('<div class="btn-group pull-right"></div>');
		    contextChoiceButton = $('<input type="button" class="btn btn-primary" value="Go" name="contextChoiceButton" id="contextChoiceButton"/>');

		    buttonDiv.append(contextChoiceButton)
		    buttonDiv.append('<g:link controller="context" action="createContext" class="btn btn-success">New</g:link>')
		    
		    contextChoiceButton.click( function() {
              var chosenContext = "${createLink(uri: '/')}" + $('#contextChoice').val();

              if ( chosenContext != "" ) {
                $('#contextForm').prop("action",""+chosenContext);
                
                $('#contextForm').submit();
              } else {
                return false;
              }
            });
            
		    form.append(buttonDiv);
		    
		    container.append(form);
	    } else {
	        container.append('<p class="unimportant">Log in to see your available contexts</p>');		    
		}
		
	}
</script>

