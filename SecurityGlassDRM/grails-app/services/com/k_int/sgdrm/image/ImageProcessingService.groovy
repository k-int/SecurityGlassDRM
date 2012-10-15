package com.k_int.sgdrm.image

/**
 * A service to handle the general processing of images as required including resizing, securing, etc.
 * @author rpb rich@k-int.com
 * @version 1.0 12.10.12
 */
class ImageProcessingService {

	// Define the various services used here
	def imageResizeService;
	def imageWatermarkService;
	def stegHideStegService;
	def exivMetadataWrapperService;
	
	@javax.annotation.PostConstruct
	def init() {
		log.debug("ImageProcessingService init method called");
	}
	
	
	/**
	 *  Create a copy of the original item.
	 *  Steg hide the ID of the new item in the image itself
	 *  Add the id of the image to the exif and the XMP metadata
	 *  Create the new item
	 *  @param db Mongo database connection to store data about the create image
	 *  @param image_repo_dir Image repository directory
	 *  @param work The object from Mongo originally
	 *  @param original_item The original image to be resized
	 *  @param workflowType The type to assign to the image in the DB
	 *  @param owner The owner of the image
	 *  @param resize Resize information
	 */
	def createSecureCopy(db,image_repo_dir, work, original_item, workflowType, owner, watermark, resize=null) {

		def new_item_id = new org.bson.types.ObjectId();
		def new_file_name = "${image_repo_dir}/${new_item_id}"

		def new_item = [:]
		new_item._id = new_item_id
		new_item.workId = work._id;
		new_item.workflowType = workflowType
		new_item.createDate = System.currentTimeMillis();
		new_item.pathInStore = new_file_name

		log.debug("Creating secure copy from original... New item id is ${new_item_id}, store location will be ${new_file_name}");

		// Copy the original image to the new location
		def out_file = new FileOutputStream(new_file_name)
		def out_stream = new BufferedOutputStream(out_file)
		out_stream << new File(original_item.pathInStore).newDataInputStream()
		out_stream.close()

		// Resize
		if ( resize ) {
			log.debug("Resizing image...");
			imageResizeService.resize(new_file_name, resize);
		}

		// Watermark
		log.debug("Watermarking image...");
		imageWatermarkService.watermark(new_file_name, watermark); // TODO - get the watermark from configuration

		// steg hide item identifier
		log.debug("Encoding image ID in the image itself");
		stegHideStegService.hide(new_item_id,  new_file_name);

		// Embed metadata
		log.debug("Embedding XMP metadata");
		embedXMP(new_item_id,owner,new_file_name);

		// save the information about this new manifestation into the database
		db.item.save(new_item);
	}

	/**
	 * Embed XMP metadata in the specified image
	 * @param identifier The identifier to be embedded
	 * @param owner The owner of the object to be embedded
	 * @param target The target file
	 * @return
	 */
	def embedXMP(identifier,owner,target) {
		exivMetadataWrapperService.embed(identifier,owner,target);
	}


}
