package com.k_int.sgdrm

import org.elasticsearch.groovy.client.GIndicesAdminClient

class DataProcessingJob {
	static triggers = {
		cron name: 'dataProcessingScheduleTrigger', cronExpression: "*/5 * * * * ?", startDelay:60000l // Run every minute on the minute after a delay of 2 minutes at start up
	}

	def concurrent = false;

	// Set up the various services we are going to use for the processing
	def repositoryMonitorService;
	def tripleStoreService;
	def elasticSearchWrapperService;
	def mongoWrapperService;
	def imageProcessingService;

	def execute() {

		log.debug("In the execute method of the data processing scheduling job... " + new Date());

		// Just check that everything has been set up correctly..
		//log.debug("repositoryMonitorService = " + repositoryMonitorService);
		//log.debug("stegHideStegService = " + stegHideStegService);
		//log.debug("tripleStoreService = " + tripleStoreService);
		//log.debug("elasticSearchWrapperService = " + elasticSearchWrapperService);
		//log.debug("mongWrapperService = " + mongoWrapperService);
		//log.debug("exivMetadataWrapperService = " + exivMetadataWrapperService);
		//log.debug("imageWatermarkService = " + imageWatermarkService);
		//log.debug("imageResizeService = " + imageResizeService);

		log.debug("SecurityGlass Data processing.. Initialising at ${new Date()}");

		def reccount = 0;
		def starttime = System.currentTimeMillis();

		// Set up the elastic search client..
		def esclient = setupES();

		// Set up the connection to mongo
		def db = mongoWrapperService.getDatabase("frbr"); // TODO - change this to come from config..

		// Work out where to put the images that are created and make the
		// directory if it doesn't exist already
		// TODO - change this to come from config
		def image_repo_dir = "${System.getProperty('user.home')}/media/images"
		log.debug("Working with image repo dir ${image_repo_dir}");
		File image_repo_dir_file = new File(image_repo_dir);
		image_repo_dir_file.mkdirs();

		// Iterate through all of the new records in the database and process them
		try {
			repositoryMonitorService.iterateLatest(db,'work', -1) { jsonobj ->

				log.debug("Process [${reccount++}] ${jsonobj._id}");
				def remote_image_url = jsonobj.expressions[0].manifestations[0].uri
				log.debug("Fetch image from : ${remote_image_url}");

				log.debug("Checking for any existing items where workId matches");
				def item_record = null;
				item_record = db.item.findOne(workId:jsonobj._id, workflowType:'original');
				if ( item_record == null ) {
					log.debug("Create new item record");
					item_record = [:]
					item_record._id = new org.bson.types.ObjectId();
				}

				def output_filename = "${image_repo_dir}/${item_record._id.toString()}"

				// Populate the item record with the relevant data
				item_record.originalSource = [type:'external',uri:remote_image_url]
				item_record.workId = jsonobj._id;
				item_record.workflowType = 'original'
				item_record.mimeType = 'application/jpg'
				item_record.pathInStore = output_filename
				item_record.createDate = System.currentTimeMillis();

				// Copy the remote image file into the local file
				log.debug("Downloading remote image");
				def out_file = new FileOutputStream(output_filename)
				def out_stream = new BufferedOutputStream(out_file)
				out_stream << new URL(jsonobj.expressions[0].manifestations[0].uri).openStream()
				out_stream.close()
				db.item.save(item_record);

				// Set up the Elastic search entry
				log.debug("Setting up ES entry");
				def esent = [:]
				esent._id = jsonobj._id;
				esent.title = jsonobj.title;
				esent.description = jsonobj.description;
				esent.identifier = jsonobj.identifier;
				esent.lastModified = jsonobj.lastModified;
				esent.owner = jsonobj.owner;
				indexESEntry(esclient, esent);

				log.debug("Creating copies of original image");
				log.debug("Creating public secure copy");
				imageProcessingService.createSecureCopy(db,image_repo_dir, jsonobj, item_record, 'SecureCopy', 'MediaProjectOwner', 'MEDIA');
				log.debug("Creating thumbnail");
				imageProcessingService.createSecureCopy(db,image_repo_dir, jsonobj, item_record, 'Thumbnail', 'MediaProjectOwner','MEDIA','140x140>');

				log.debug("Item has id ${item_record._id} and saved in ${output_filename}");
			}
		} catch ( RepositoryMonitorException rme ) {
			log.error("RespositoryMonitorException thrown when attempting to iterate through new records: " + rme.getMessage());
		}

		log.debug("Completed after ${reccount} records in ${System.currentTimeMillis() - starttime}ms");
	}


	def indexESEntry(esclient, entry) {

		entry.indexTime = new Date();

		try {
			def future = esclient.index {
				index "media"
				type "work"
				id entry._id
				source entry
			}
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		finally {
		}

	}

	def setupES() {

		// TODO - change the following to come from config...

		def index_admin_client = new GIndicesAdminClient(elasticSearchWrapperService.getESClient());
		def future = index_admin_client.putMapping {
			indices 'media'
			type 'work'
			source {
				media {       // Think this is the name of the mapping within the type
					properties {
						owner {
							type = 'string'
							store = 'yes'
							index = 'not_analyzed'
						}
					}
				}
			}
		}
		println("Installed media mapping ${future}");

		elasticSearchWrapperService.getESClient();
	}
}
