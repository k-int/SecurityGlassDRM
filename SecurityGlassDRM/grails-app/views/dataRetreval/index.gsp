<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="bootstrap"/>
    <title>Sample title</title>
  </head>
  <body>
    
    <section id="main">
       <h1>Upload a picture to find its buyer</h1>
      <g:uploadForm id="update" url="[action: 'pictureUploader']">
        <input id="inputField" type="file" name="myFile"  enctype="multipart/form-data" />
        <br>

                  <g:submitButton name="Submit"/>
            </g:uploadForm>

       
       <br><br>
       <g:if test="${info}">
         <h2>The original buyer is: </h2>
         <h3>${info}</h3>
       
       </g:if>
       <div>
       
       
       </div>
       
    </section>
    
    
   
  </body>
</html>
