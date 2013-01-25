package com.k_int.sgdrm

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

import grails.converters.JSON

class CreateMediaController {

    def springSecurityService;
	def mongoWrapperService;
	def stegHideStegService;
	def imageEncryptionService;
	def imageWatermarkService;
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def index() {
		redirect(action:"list", params: params);
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def list() { 
        log.debug("In the create media controller index method");
        
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		
		def mediaList = CreatedMedia.list(params);
		def mediaNum = CreatedMedia.count(); 
		
        def result = ["mediaList":mediaList, "mediaNum":mediaNum];
    }
    
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def genKey() {
		log.debug("In the create media controller gen key method");
		
		def generatedKey = imageEncryptionService.generateKey();
		
		def keyString = imageEncryptionService.convertSecretKeyToString(generatedKey);
		
		def result = ["key":keyString];
		
		render result as JSON;
	}
	
	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def createLargeWatermarked() {
		log.debug("In the create media controller createLargeWatermarked method");
		
		def result = [:];
		
			log.debug("Params: recordId: " + params.recordId + " name: " + params.userName + " email: " + params.email + " encryptionKey: " + params.encryptionKey );
			
			
			// Get back the information from Mongo about the image based on it's ID..
			def db = mongoWrapperService.getDatabase("frbr"); // TODO - change this to come from config..
			def original_record = db.item.findOne(workId:params.recordId, workflowType:'original');
			
			if ( original_record != null ) {
				// Image data found
	
				// Store the preliminary details in the database..
				params.name = params.userName;
				def newCreatedMedia = new CreatedMedia(params).save(flush: true);

				// Get the path to the image
				def imagePath = original_record.pathInStore;
				log.debug("Image path for original image: " + imagePath);
				
				// Now get the image and encode the relevant information into it
				
				def image_repo_dir = grailsApplication.config.com.k_int.sgdrm.localRepoPath;
//				def image_repo_dir = "${System.getProperty('user.home')}/media"
				def encrypted_dir = "${image_repo_dir}/encrypted"
				
				// Create the target directory if required
				File encrypted_dir_file = new File(encrypted_dir);
				encrypted_dir_file.mkdirs();
				
				// Copy the image to the new location ready to be modified
				def full_image_path = "${encrypted_dir}/${newCreatedMedia.id}";
				log.debug("Full image path to store the encrypted, etc. image worked out to be: " + full_image_path);
				
				def out_file = new FileOutputStream(full_image_path)
				def out_stream = new BufferedOutputStream(out_file)
				out_stream << new File(original_record.pathInStore).newDataInputStream()
				out_stream.close()
		
				// Encode the relevant information in the image
				log.debug("Encoding username in the image itself");
				stegHideStegService.hide("${params.userName} - ${params.email}",  full_image_path);
				
				// Watermark the image for return
				log.debug("Watermarking the image to encourage purchase");
				imageWatermarkService.watermark(full_image_path, "MEDIA");
				
				// Update the database with the completion details
				newCreatedMedia.watermarkedFilePath = full_image_path;
				newCreatedMedia.watermarkedRelativeFilePath = "/encrypted/${newCreatedMedia.id}";
				newCreatedMedia.save(flush:true);
						
				// Return the created image
				//response.setContentType("application/octet-stream");
				//response.setHeader("Content-disposition", "attachment;filename=encryptedImage-${newCreatedMedia.id}");
				response.outputStream << new File(full_image_path).newDataInputStream();
//				response.outputStream << new File(full_image_path).newDataInputStream(); // Alternative response that returns the unencrypted version for testing
				return;
				
			} else {
				// Image data not found - return a 404(?)
				log.debug("Image purchase request for an image that couldn't be found. Image id: " + params.recordId)
				response.sendError(404)
			}		
		
		return result;
	}

	@Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
	def createLargeSecured() {
		log.debug("In the create media controller createLargeSecured method");
		
		def result = [:];
		
			log.debug("Params: recordId: " + params.recordId + " name: " + params.userName + " email: " + params.email + " encryptionKey: " + params.encryptionKey );
			
			
			// Get back the information from Mongo about the image based on it's ID..
			def db = mongoWrapperService.getDatabase("frbr"); // TODO - change this to come from config..
			def original_record = db.item.findOne(workId:params.recordId, workflowType:'original');
			
			if ( original_record != null ) {
				// Image data found
	
				// Store the preliminary details in the database..
				params.name = params.userName;
				def newCreatedMedia = new CreatedMedia(params).save(flush: true);

				// Get the path to the image
				def imagePath = original_record.pathInStore;
				log.debug("Image path for original image: " + imagePath);
				
				// Now get the image and encode the relevant information into it
				
				def image_repo_dir = grailsApplication.config.com.k_int.sgdrm.localRepoPath;
//				def image_repo_dir = "${System.getProperty('user.home')}/media"
				def encrypted_dir = "${image_repo_dir}/encrypted"
				
				// Create the target directory if required
				File encrypted_dir_file = new File(encrypted_dir);
				encrypted_dir_file.mkdirs();
				
				// Copy the image to the new location ready to be modified
				def full_image_path = "${encrypted_dir}/${newCreatedMedia.id}";
				log.debug("Full image path to store the encrypted, etc. image worked out to be: " + full_image_path);
				
				def out_file = new FileOutputStream(full_image_path)
				def out_stream = new BufferedOutputStream(out_file)
				out_stream << new File(original_record.pathInStore).newDataInputStream()
				out_stream.close()
		
				// Encode the relevant information in the image
				log.debug("Encoding username in the image itself");
				stegHideStegService.hide("${params.userName} - ${params.email}",  full_image_path);
				
				// Update the database with the completion details
				newCreatedMedia.securedFilePath = full_image_path;
				newCreatedMedia.securedRelativeFilePath = "/encrypted/${newCreatedMedia.id}";
				newCreatedMedia.save(flush:true);
						
				// Return the created image
				//response.setContentType("application/octet-stream");
				//response.setHeader("Content-disposition", "attachment;filename=encryptedImage-${newCreatedMedia.id}");
				response.outputStream << new File(full_image_path).newDataInputStream();
//				response.outputStream << new File(full_image_path).newDataInputStream(); // Alternative response that returns the unencrypted version for testing
				return;
				
			} else {
				// Image data not found - return a 404(?)
				log.debug("Image purchase request for an image that couldn't be found. Image id: " + params.recordId)
				response.sendError(404)
			}		
		
		return result;
	}
}
