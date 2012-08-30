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
      <section id="main" class="span12">

        <div class="hero-unit">
          <h1>${specifiedContext}/${specifiedStore}</h1>

          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          
          <a href="#uploadModal" role="button" class="btn btn-primary" data-toggle="modal">Upload file</a>
          
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
          
        </div>
          
      </section>

      
      <script type="text/javascript">
        
        $(document).ready(function() {
        
        
          $('#uploadForm').validate();
          
          $('#uploadSubmit').click(function() {
              $('#uploadForm').submit();
          });
          
        });
        
        
      </script>
    </div>
  </body>
</html>
