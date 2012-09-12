package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

class ContextController {

    def springSecurityService;

    def index() { 
        log.error("In the context controller - specified context = " + params.context);
        
        def specifiedContext = params.context;
        
        def result = [:];
        
        // Go and find the specified context in the database
        def actualContext = Context.findByName(specifiedContext);
        
		if ( actualContext != null ) {
	        // Go and find all of the stores within this context
	        def stores = ContentStore.findAllByStoreContext(actualContext);
	        
	        // Go and get all contexts that this user is associated with
	        def principal;
	        if ( ( springSecurityService.principal ) ) {
				if ( springSecurityService.principal instanceof String ) 
	                principal = null;
				else if ( springSecurityService.principal.id ) 
	            	principal = User.get(springSecurityService.principal.id)
	        }
	        if ( principal ) {
	            // We have a user - get their contexts..
	            def userContexts = Context.findAllByOwner(principal);
	            
	            log.debug("userContexts.size = " + userContexts.size);
	            result.userContexts = userContexts;
	            
	        } else {
	            // No user - can't get contexts..
	            log.debug("No user when in the context index method so can't look for contexts");
	        }
	
	        
	        result.specifiedContext = specifiedContext;
	        result.actualContext = actualContext;
	        result.contextType = actualContext.contextType;
	        result.stores = stores;
	
	        return result;
		} else {
			// No context found - complain
			flash.message = "The specified context could not be found within the system";
			response.sendError(404);
		}
    }
    
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def createContext() {
        
        log.debug("In the context controller ready to create a new context.");
        
        def result = [:];
        
        if ( request.post ) {
            log.debug("POST method received. Params: name: " + params.contextName);
            
            // Convert the given context name into something that is valid (if required)
            def newContextName = ContentStoreUtils.createValidStoreName(params.contextName);
            log.debug("newContextName worked out to be:" + newContextName);
            
            // Set up the new context with the specified value
            def existingContext = Context.findByName(newContextName);
            if ( existingContext ) {
                // Already a context with the chosen name - complain
                log.debug("Attempt to create a context with a name that is already in use...");
                
                flash.error = "Specified organisation name already in use. Please try another";
                
                result.contextName = newContextName;
            } else {
                def organisationType = ContextType.findByName("Organisation");
                def owner;
                if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
                    owner = User.get(springSecurityService.principal.id)
                }

                def newContext = new Context(name: newContextName, contextType: organisationType, owner: owner);
                
                if ( !newContext.save(flush:true) ) {
                    newContext.errors.each {
                        println it;
                    }
                }
                
                // Context created - send the user to it's index page
                flash.message = newContextName + " organisation successfully created";
                redirect(uri:"/" + newContextName);
            }
            
        } else {
            log.debug("GET method received");
            // Don't need to do anything..
        }
        
        return result; 
    }
 
    def checkNewContextName() {
        
        log.debug("In the checkNewContextName method with name: " + params.contextName);
        def retval = true;
        
        def newName = params.contextName
        
        def validNewName = ContentStoreUtils.createValidStoreName(newName)
        
        log.debug("About to check for existing context with name: " + validNewName);
    
        def existingContext = Context.findByName(validNewName);
        
        if ( existingContext ) {
            // There's already a context with that name
            retval = false;
        }
    
        log.debug("retval = " + retval);
        
        render retval;
    }
    
}
