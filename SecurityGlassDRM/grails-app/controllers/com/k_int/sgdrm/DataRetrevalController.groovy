package com.k_int.sgdrm

import grails.plugins.springsecurity.Secured

class DataRetrevalController {
    
     def springSecurityService;
     def stegShowStegService;
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() { 
     if(chainModel){
         println "chainModel: " + chainModel.info
         [info:chainModel.info]
         
     }
   
    }
    
//    The next thing to do is to make a new service that gets the steg information.
//    Remember the email that ibbo sent you and use the command from that.
//    And use the urresn StegHideStegService to help you create the new one.
    def pictureUploader(){
        println "in pictureUploader"
        String info
         def f = request.getFile('myFile')
           if (request.getFile('myFile').getOriginalFilename()) {
                if (f == null | f.empty) {
            flash.message = "file cannot be empty"
                }
                else{
                    String fName = request.getFile('myFile').getOriginalFilename()
                    println fName
                    f.transferTo(new File(fName))
                    
                    info = stegShowStegService.show(fName,'test')
                    
                new File(fName).delete()
                    
                }
           }
           

        chain action: "index", model:[info:info]
        
    }
}


