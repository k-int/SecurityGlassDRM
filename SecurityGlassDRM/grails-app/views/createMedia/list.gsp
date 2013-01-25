<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap" />
<title>Create Media home</title>
</head>

<body>
	<div class="row-fluid">

		<section id="main">

			<div class="hero-unit row">
				<div class="page-header span12">
					<h1>Create Media</h1>
				</div>
			</div>

			<div class="row">
				<div class="span10">
					<h2>Created media</h2>
					
					<table class="table table-striped table-hover">
					   <thead>
					       <g:sortableColumn property="id" title="Id"/>
					       <g:sortableColumn property="recordId" title="Record ID"/>
					       <g:sortableColumn property="name" title="Name"/>
					       <g:sortableColumn property="email" title="Email"/>
					       <g:sortableColumn property="encryptionKey" title="Encryption key"/>
					       <g:sortableColumn property="creationDate" title="Creation date"/>
					   </thead>
					   <tbody>
					       <g:each in="${mediaList}" status="i" var="mediaInstance">
					           <tr>
					               <td>${mediaInstance.id}</td>
					               <td>${mediaInstance.recordId}</td>
					               <td>${mediaInstance.name}</td>
					               <td>${mediaInstance.email}</td>
					               <td>${mediaInstance.encryptionKey}</td>
					               <td>${mediaInstance.creationDate}</td>
					           </tr>
					       </g:each>
					   </tbody>
					</table>
					<div class="pagination pagination-small">
                        <bootstrap:paginate action="list" total="${mediaNum}" />
                    </div>
				</div>
				<div class="span2">
				    <h2>Create new</h2>
                    <a href="#createNewModal" role="button" class="btn btn-primary" data-toggle="modal">Create new</a>
				    
				</div>
			</div>

		</section>

	</div>

    <div class="modal hide fade" id="createNewModal" name="createNewModal" tabIndex="-1"  role="dialog" aria-labelledby="createNewModalLabel" aria-hidden="true">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h3>Create new media</h3>
        </div>
        <div class="modal-body">
         <form id="newModalForm" name="newModalForm" action="save" method="POST">
              <dl>
                <dt><label for="recordId">Record id (UUID):</label></dt>
                <dd><input type="text" name="recordId" id="recordId" class="required"/></dd>

                <dt><label for="userName">Name:</label></dt>
                <dd><input type="text" name="userName" id="userName" class="required"/></dd>

                <dt><label for="email">Email:</label></dt>
                <dd><input type="text" name="email" id="email" class="required"/></dd>
                
                <dt><label for="encryptionKey">Encryption key:</label></dt>
                <dd><input type="text" name="encryptionKey" id="encryptionKey" class="required"/></dd>
              </dl>
          </form>
        </div>
        <div class="modal-footer">
          <div class="btn-group pull-right">
              <input type="button" name="createNewSubmit" id="createNewSubmit" value="Create" class="btn btn-primary"/>
              <button type="button" class="btn btn-danger" data-dismiss="modal" aria-hidden="true">Cancel</button>
          </div>
        </div>
      </div>
    
	<script type="text/javascript">
		$(document).ready(function() {
	          $('#createNewSubmit').click(function() {
	              $('#newModalForm').submit();
	          });
				
		});

	</script>
</body>
</html>
