package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

class StoreController {

    def springSecurityService;
    
    static allowedMethods = [uploadFile: 'POST'];

    
    def index() { 
        // TODO - what do we want here?
        log.error("In the context controller - specified context = " + params.context + " and store: " + params.store);
        
        def specifiedContext = params.context;
        def specifiedStore = params.store;
        
        // Go and find the specified context in the database
        def actualContext = Context.findByName(specifiedContext);
        
        return [specifiedContext: specifiedContext, specifiedStore: specifiedStore, actualContext: actualContext, contextType: actualContext.contextType];
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def createContentStore() {
      
    // If we have received a GET request then set up the blank form as required, otherwise (if POST) then
    // validate and save the specified data
    
    def result = [:]
    
    def principal;
    if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
        principal = User.get(springSecurityService.principal.id)
    }
    
    if ( request.post ) {
        // A POST submission
        log.debug("In the createContentStore method with a POST. Params: name:" + params.storeName + " type:" + params.storeType + " owner: " + params.storeOwner)
        
        // Convert the given store name into something that is valid
        def newStoreName = ContentStoreUtils.createValidStoreName(params.storeName);
        log.debug("newStoreName worked out to be: " + newStoreName)
        
        // Set up the new content store with the specified values
        def existingContentStore = ContentStore.findByName(newStoreName);
        if ( existingContentStore ) {
            // Already a store with the chosen name - complain
            log.debug("Attempt to create a content store with a name that is already in use..");
            
            flash.error = "Specified content store name already in use please try another";
            result.storeName = params.storeName;
            result.storeType = params.storeType;
            result.storeOwner = params.storeOwner;
        } else {
            
            def storeContext = Context.findByName(params.storeOwner);
            def storeType = ContentStoreType.findByName(params.storeType);

            log.debug("storeContext = " + storeContext + " and type: " + storeType + " and name: " + newStoreName);
                
            def newContentStore = new ContentStore(name: newStoreName, storeContext: storeContext, storeType: storeType);
//            newContentStore.storeContext = storeContext;
            if ( !newContentStore.save() ) {
                newContentStore.errors.each {
                    println it;
                }
            }
            
                log.debug("newContentStore = " + newContentStore);
            
            // Content store created - send the user to the created store
            flash.message = newStoreName + " content store successfully created";
            redirect(uri:"/" + params.storeOwner + "/" + newContentStore.name);
        }
        
    } else {
        // A GET request
        log.debug("In the createContentStore method with a GET.");
        
        if ( params.storeOwner) {
            result.storeOwner = params.storeOwner;
        }
            

        
        // Get the list of possible owners (contexts)
        def possibleContexts = Context.findAllByOwner(principal);
        def possibleStoreTypes = ContentStoreType.list();
            
        
        result.possibleContexts = possibleContexts;
        result.possibleStoreTypes = possibleStoreTypes;
        
    }
    
    return result
  }
  
  def checkNewContentStoreName() {
    
    log.debug("In the checkNewContentStoreName method with name: " + params.storeName + " and owner: " + params.storeOwner);
        def retval = true;
        
        def newName = params.storeName
        def storeOwner = params.storeOwner
        
        def validNewName = ContentStoreUtils.createValidStoreName(newName)
        def storeContext = Context.findByName(storeOwner);
        
        log.debug("About to check for existing store with name: " + validNewName + " and context: " + storeContext);
    
        def existingStore = ContentStore.findByNameAndStoreContext(validNewName, storeContext);
        
        if ( existingStore ) {
            // There's already a store with that name
            retval = false;
        }
    
        log.debug("retval = " + retval);
        
        render retval;
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def uploadFile() {
        log.debug("In the uploadFile method with store name: " + params.store + " and context: " + params.context);
        
        
        // TODO - put some processing in place..

        flash.message = "Upload received..";
        
        redirect(uri: "/" + params.context + "/" + params.store);
        
    }

}
