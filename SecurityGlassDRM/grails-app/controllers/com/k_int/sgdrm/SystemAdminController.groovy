package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.JSON
import groovy.xml.MarkupBuilder

class SystemAdminController {

    def springSecurityService;

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() { 
        log.error("In the system administration controller");
        
        def result = [:];
        // TODO
    }
    
	
	// Get the various job status objects and return the information such as active, etc.
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def getJobStatus() {
		def statuses = JobStatus.list();

		// Extract the information we care about for return
		def retval = [];
		statuses.each() {
			def thisStatus = [:];
			thisStatus.id = it.id;
			thisStatus.active = it.active;
			thisStatus.name = it.name;
			thisStatus.lastRunTime = it.lastRunTime;
			thisStatus.description = it.description;
			
			retval.add(thisStatus);
		}		
		render retval as JSON;
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def toggleJobStatus() {
		log.debug("toggleJobStatus called with job status id: " + params.id);
		
		// Go and get the job status object referenced by the id so that it can be updated
		def jobStatus = JobStatus.findById(params.id);
		if ( jobStatus ) {
			jobStatus.active = !jobStatus.active;
			jobStatus.save(flush:true);
		}
		
		def retval = true;
		
		render retval;
	}
}
