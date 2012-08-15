<!doctype html>
<html>
  <head>
    <meta name="layout" content="bootstrap"/>
    <title>Create Content Store</title>
    <r:use module="jquery-validate"/>
     <jqval:resources/>
  </head>

  <body>
    <div class="row-fluid">
      <aside id="application-status" class="span3">
        <div class="well sidebar-nav">
          <h5>Application Status</h5>
          <ul>
            <li>App version: <g:meta name="app.version"/></li>
            <li>Grails version: <g:meta name="app.grails.version"/></li>
          </ul>
        </div>
      </aside>

      <section id="main" class="span9">

        <div class="hero-unit">
          <h1>New content store</h1>
          
          <g:if test='${flash.error}'>
            <div class="errors">
              <ul>
                <li class='error'>${flash.error} </li>
              </ul>
            </div>
          </g:if>
          
          <form action="createContentStore" id="createContentStoreForm" name="createContentStoreForm" method="POST">
            
            <dl>
              <dt><label for="owner">Owner</label></dt>
              <dd><input id="storeOwner" name="storeOwner" type="text" value="${storeOwner}"/></dd>
              <dt><label for="storeName">Content store name</label></dt>
              <dd><input id="storeName" name="storeName" type="text" value="${storeName}" /></dd>
              <dt><label for="storeType">Store type</label></dt>
              <dd><input id="storeType" name="storeType" type="text" value="${storeType}"/></dd>
              
            </dl>
            
            
            
            
            <input type="submit" value="Create"/>
          </form>
          
          
          Owner -- Content Store Name -- type [free|clean]<br/>
Good content store names are short and memorable
        </div>
          
      </section>

    </div>
    
    
        <script type="text/javascript">


       $(document).ready(function (){
         
         alert("In the document ready method...");
         
                $("#storeName").change(function () {
                   $("#storeName").removeData("previousValue");
                });
         
//                $("#storeName").blur(function() {
//                      alert("In the blur method..");
//                      var vaildRet = $("#storeName").valid();
//                      
//                      alert("Back from calling valid.. vaildRet = " + vaildRet);
//                })

		$("#createContentStoreForm").validate({
			rules: {
				'storeName': {
					remote: {
						url: "${createLink(controller:'home', action:'checkNewContentStoreName')}",
						type: "post",
						data: {
							storeName: function() {
								return $("#storeName").val();
						  }
						}
					  }
				}
			},
			messages: {
				'storeName': {
					remote: "The store name is already in use or is invalid. Please try again. Good names are short and memorable."
				}
			}
		});
	});
        
    </script>

  </body>
</html>
