package com.k_int.sgdrm

import org.elasticsearch.groovy.client.GIndicesAdminClient
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes;


class DataProcessingJob {
	static triggers = {
		cron name: 'dataProcessingScheduleTrigger', cronExpression: "0 0/1 * * * ?", startDelay:60000l // Run every minute on the minute after a delay of 2 minutes at start up
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

		// Check that this job should be running and only continue if it should
		def jobStatus = JobStatus.findByName("DataProcessingJob");
		if ( !jobStatus ) {
			// No job status found in the database - need to set one up for use later
			jobStatus = new JobStatus(name: "DataProcessingJob", description: "Imported data processing task", active: false, lastRunTime: null);
			
			if ( !jobStatus.save(flush: true) ) {
				log.error("Error thrown when trying to create a new JobStatus object for the DataProcessingJob class");
				jobStatus.errors.each() {
					log.error("Error: " + it);
				}
			}
		} else if ( jobStatus.active ) {
		
			// We have a status object and this job should be active - process
			log.debug("JobStatus object found and this DataProcessingJob job should be active - starting processing");

		log.debug("SecurityGlass Data processing.. Initialising at ${new Date()}");

		def reccount = 0;
		def starttime = System.currentTimeMillis();
		def startDate = new Date(starttime);

		// Set things up so that we can load configuration
		ApplicationContext context = (ApplicationContext)ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
		log.debug("context = " + context);
		def applicationConfig = context.getBean("grailsApplication");
		
		
		// Set up the elastic search client..
		def esclient = setupES();

		// Set up the connection to mongo
		def mongoDBName = applicationConfig.config.com.k_int.sgdrm.mongoDBName.toString();
		if ( !mongoDBName ) {
			log.debug("Mongo DB name not retrieved from config so defaulting to frbr");
			mongoDBName = "frbr";
		}
		def db = mongoWrapperService.getDatabase(mongoDBName); 

		// Work out where to put the images that are created and make the
		// directory if it doesn't exist already
		def image_repo_dir = applicationConfig.config.com.k_int.sgdrm.localRepoPath.toString();
//		def image_repo_dir = "${System.getProperty('user.home')}/media" // TODO - get from config..
		def source_dir = "${image_repo_dir}/source";
		def generated_dir = "${image_repo_dir}/generated";
		 
		log.debug("Working with image repo dir ${image_repo_dir}, source dir ${source_dir} and generated dir ${generated_dir}");
		File image_repo_dir_file = new File(image_repo_dir);
		image_repo_dir_file.mkdirs();
		File source_dir_file = new File(source_dir);
		source_dir_file.mkdirs();
		File generated_dir_file = new File(generated_dir);
		generated_dir_file.mkdirs();

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

				def output_filename = "${image_repo_dir}/source/${item_record._id.toString()}"
				def generated_dir_name = "generated";

				// Populate the item record with the relevant data
				item_record.originalSource = [type:'external',uri:remote_image_url]
				item_record.workId = jsonobj._id;
				item_record.workflowType = 'original'
				item_record.mimeType = 'application/jpg'
				item_record.pathInStore = output_filename
				item_record.createDate = System.currentTimeMillis();

				// Copy the remote image file into the local file
				log.debug("Downloading remote image from " + jsonobj.expressions[0].manifestations[0].uri);
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
				
				def licenseString = jsonobj.licenseString;
				def sourceString = 'Nottingham City Museums and Galleries' // TODO - get this properly..

				log.debug("Creating copies of original image");
				log.debug("Creating public secure copy");
				def secure_db_entry = imageProcessingService.createSecureCopy(db,image_repo_dir, generated_dir_name, jsonobj, item_record, 'SecureCopy', sourceString, licenseString, 'MEDIA');
				log.debug("Creating thumbnail");
				def thumb_db_entry = imageProcessingService.createSecureCopy(db,image_repo_dir, generated_dir_name, jsonobj, item_record, 'Thumbnail', sourceString, licenseString, 'MEDIA','140x140>');

				esent.thumbnail_path = "${thumb_db_entry.relative_pathInStore}";
				esent.xmp_path = "${thumb_db_entry.relative_xmpFile}";
				
				indexESEntry(esclient, esent);
				
				log.debug("Item has id ${item_record._id} and saved in ${output_filename}");
			}
		} catch ( RepositoryMonitorException rme ) {
			log.error("RespositoryMonitorException thrown when attempting to iterate through new records: " + rme.getMessage());
		}

		log.debug("Completed after ${reccount} records in ${System.currentTimeMillis() - starttime}ms");
			jobStatus.lastRunTime = startDate;
			jobStatus.save(flush:true);
		
		} else {
			log.debug("JobStatus object found for the DataProcessingJob, but active is false so not doing any processing");
		}
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
						identifier {
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
