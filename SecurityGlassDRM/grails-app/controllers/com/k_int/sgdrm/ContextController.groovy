package com.k_int.sgdrm

class ContextController {

    def index() { 
        // TODO - what do we want here?
        log.error("In the context controller - specified context = " + params.uploadContext);
        
        def specifiedContext = params.uploadContext;
        
        return [specifiedContext: specifiedContext];
    }
    
    
}
