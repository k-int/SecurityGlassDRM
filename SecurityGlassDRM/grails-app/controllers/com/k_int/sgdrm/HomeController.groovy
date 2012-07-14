package com.k_int.sgdrm

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
class HomeController {

  def index() { 
    log.debug("HomeIndex");
  }
}
