package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.converters.*
import groovy.xml.MarkupBuilder

class ImageController {

    def springSecurityService;
	def mongoWrapperService;
	def elasticSearchWrapperService;
    
  
    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def purchase() {
        log.debug("In the purchase method with username: " + params.username + " and email: " + params.email + " and encKey: " + params.key + " and image id: " + params.image_id );
        
        
		// Get back the information from Mongo about the image based on it's ID..
		def db = mongoWrapperService.getDatabase("frbr"); // TODO - change this to come from config..
		def original_record = db.item.findOne(workId:params.image_id, workflowType:'original');
		
		if ( original_record != null ) {
			// Image data found

			// Get the path to the image
			def imagePath = original_record.pathInStore;
			log.debug("Image path for original image: " + imagePath);	
			
			// Now get the image and encode the relevant information into it
			
			// TODO
			
			// Encrypt the image to return
			// TODO
			
			// Return the image
			// TODO
	
		} else {
			// No image data found - need to return a 404..
		
			// TODO
		
		}
		
		
        // TODO - put some processing in place..

		def result = [:];
		
		return result;
    }
}
