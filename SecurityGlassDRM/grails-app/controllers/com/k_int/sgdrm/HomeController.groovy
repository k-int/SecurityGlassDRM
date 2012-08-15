package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

// How to access mongo


class HomeController {

  def springSecurityService
  def mongoWrapperService

  def index() { 
    def result = [:]

    // def mdb = mongoWrapperService.getMongo().getDB('mongo_coll_name')


    // Some examples - Find all documents in the tipps collection, and sort them by lastmod asc using a cursor
    // def cursor = mdb.tipps.find().sort(lastmod:1)
    //   cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    //   cursor.each { tipp ->
    // Or the simpler version if you dont need/want the cursor
    // mdb.tipps.find().sort(lastmod:1).each { tipp ->
    //
    // finding by title and type would be something like
    // mdb.tipps.find([title:'thetitle', type:'sometype']).sort(lastmod:1).each { tipp ->


    log.debug("Principal: ${springSecurityService.principal} ${springSecurityService.principal.class.name}");
    if ( springSecurityService.principal instanceof String ) {
      // if ( springSecurityService.principal == 'anonymousUser' ) {
      // }
    }
    else if ( ( springSecurityService.principal ) && ( springSecurityService.principal.id ) ) {
      result.user = User.get(springSecurityService.principal.id)
    }

    if ( result.user ) {
        // The user is logged in - get the list of their content stores
        def userContentStores = ContentStore.findAllByStoreOwner(result.user);
        
      render(view:'loggedIn', model: [contentStores:userContentStores])
    }
    else {
      render(view:'index')
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def securedIndex() {
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
    def userIsAdmin = checkUserIsAdmin(principal);
    
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
            
            def storeOwner;
            if ( userIsAdmin ) {
                // The user is the admin user - get the specified owner and user that
                storeOwner = User.findByUsername(params.storeOwner);
            } else {
                storeOwner = principal;
            }
            
            log.debug("storeOwner = " + storeOwner);
            
            def storeType = ContentStoreType.findByName(params.storeType);
            def newContentStore = new ContentStore(name: newStoreName, storeType: storeType, storeOwner: storeOwner).save(flush:true);
            
            // Content store created - send the user to the created store
            flash.message = newStoreName + " content store successfully created";
            redirect(uri:"/" + newContentStore.name);
        }
        
    } else {
        // A GET request
        log.debug("In the createContentStore method with a GET.");
        
        // No specific processing required
        
    }

    // Remember whether we have an admin user or not for the interface
    result.adminUser = userIsAdmin;
    
    return result
  }
  

    def checkUserIsAdmin(principal) {
        // TODO
        
        def adminUser = false;
        
        def adminRole = Role.findByAuthority('ROLE_ADMIN');
        
        if ( principal ) {
            if ( principal.authorities.contains(adminRole) )
                adminUser = true;
        }

        return adminUser;
    }


    def checkNewContentStoreName() {
        
    
    log.debug("In the checkNewContentStoreName method with name: " + params.storeName);
        def retval = true;
        
        def newName = params.storeName
        def validNewName = ContentStoreUtils.createValidStoreName(newName)
    
        def existingStore = ContentStore.findByName(validNewName);
        
        if ( existingStore ) {
            // There's already a store with that name
            retval = false;
        }
    
        log.debug("retval = " + retval);
        
        render retval;
    }

    
}
