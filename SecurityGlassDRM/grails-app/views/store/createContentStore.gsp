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
      <section id="main" class="span12">

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
              <dd>
                <g:select name="storeOwner" id="storeOwner" from="${possibleContexts}" value="${storeOwner}" optionKey="name" optionValue="name"/>
              </dd>
              <dt><label for="storeName">Content store name</label></dt>
              <dd><input id="storeName" name="storeName" type="text" value="${storeName}" /></dd>
              <dt><label for="storeType">Store type</label></dt>
              <dd>
                <g:select name="storeType" id="storeType" from="${possibleStoreTypes}" value="${storeType}" optionKey="name" optionValue="name"/>
              </dd>
            </dl>
            
            
            
            
            <input type="submit" value="Create" class="btn"/>
          </form>
        </div>
          
      </section>

    </div>
    
    
        <script type="text/javascript">


       $(document).ready(function (){
         
                $("#storeName").change(function () {
                   $("#storeName").removeData("previousValue");
                });

		$("#createContentStoreForm").validate({
			rules: {
				'storeName': {
					remote: {
						url: "${createLink(controller:'store', action:'checkNewContentStoreName')}",
						type: "post",
						data: {
                                                        storeOwner: function() {
                                                            return $("#storeOwner").val();
                                                        },
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
