package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
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
        // The user is logged in - send them to their homespace
        
        // Check that there exists a context for the user and create one if not..
        def userContext = Context.findByName(result.user.username);
        if ( !userContext ) {
            // No context exists - create one
            log.debug("The user doesn't have a context yet so create one for them");
            
            def userContextType = ContextType.findByName("User");
            def newUserContext = new Context(name:result.user.username, owner: result.user, contextType: userContextType).save(flush:true);
        }
        
        // Now redirect them to their actual homespace
        redirect(uri:"/" + result.user.username);
        
//        //     - get the list of their content stores
//        def userContentStores = ContentStore.findAllByStoreOwner(result.user);
//        
//      render(view:'loggedIn', model: [contentStores:userContentStores])
    }
    else {
      render(view:'index')
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def securedIndex() {
  }
    
}
