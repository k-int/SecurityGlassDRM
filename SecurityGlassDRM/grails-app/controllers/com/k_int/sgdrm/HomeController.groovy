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
      render(view:'loggedIn')
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
    def result = [:]
    result
  }


}
