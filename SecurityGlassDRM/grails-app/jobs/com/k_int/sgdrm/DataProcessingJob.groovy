package com.k_int.sgdrm



class DataProcessingJob {
	static triggers = {
		cron name: 'dataProcessingScheduleTrigger', cronExpression: "*/5 * * * * ?", startDelay:60000l // Run every minute on the minute after a delay of 2 minutes at start up
	}

	def concurrent = false;

	// Set up the various services we are going to use for the processing
	def repositoryMonitorService;
	def stegHideStegService;
	def tripleStoreService;
	def elasticSearchWrapperService;
	def mongoWrapperService;
	def exivMetadataWrapperService;
	def imageWatermarkService;
	def imageResizeService;

	def execute() {

		log.debug("In the execute method of the data processing scheduling job... " + new Date());

		// Just check that everything has been set up correctly..
		//log.debug("repositoryMonitorService = " + repositoryMonitorService);
		//log.debug("stegHideStegService = " + stegHideStegService);
		//log.debug("tripleStoreService = " + tripleStoreService);
		//log.debug("elasticSearchWrapperService = " + elasticSearchWrapperService);
		//log.debug("mongWrapperService = " + mongoWrapperService);
		//log.debug("exivMetadataWrapperService = " + exivMetadataWrapperService);
		log.debug("imageWatermarkService = " + imageWatermarkService);
		log.debug("imageResizeService = " + imageResizeService);

		def starttime = System.currentTimeMillis();
		println("MEDIA Data processing.. Initialising at ${starttime}");

		def reccount = 0;

		// Set up the elastic search client..
		def esclient = init(); // TODO - make this bit work!

		println("Connect to mongo");
		def mongo = mongoWrapperService.mongo;
		def db = mongo.getDB("frbr")

		def image_repo_dir = "${System.getProperty('user.home')}/media/images"
		println("WOrking with image repo dir ${image_repo_dir}");

		File image_repo_dir_file = new File(image_repo_dir);
		image_repo_dir_file.mkdirs();

		println("Monitor starting after ${System.currentTimeMillis() - starttime}");

		repositoryMonitorService.iterateLatest(db,'work', -1) { jsonobj ->

			println("Process [${reccount++}] ${jsonobj._id}");
			def remote_image_url = jsonobj.expressions[0].manifestations[0].uri
			println("Fetch image from : ${remote_image_url}");


			println("***owner = ${jsonobj.owner}");

			// def writer = new StringWriter()
			// def xml = new groovy.xml.MarkupBuilder(writer)
			// xml.setOmitEmptyAttributes(true);
			// xml.setOmitNullAttributes(true);
			// xml.'xcri:provider'('rdf:about':"urn:xcri:provider:${jsonobj._id}",
			//                     'xmlns:rdf':'http://www.w3.org/1999/02/22-rdf-syntax-ns#',
			//                     'xmlns:dcterms':'http://purl.org/dc/terms/',
			//                     'xmlns:dc':'http://purl.org/dc/elements/1.1/',
			//                     'xmlns:xcri':'http://xcri.org/profiles/catalog/1.2/') {
			//   'dc:title'(jsonobj.label)
			// }


			println("Checking for any existing items where workId matches");
			def item_record = null;
			item_record = db.item.findOne(workId:jsonobj._id, workflowType:'original');
			if ( item_record == null ) {
				println("Create new item record");
				item_record = [:]
				item_record._id = new org.bson.types.ObjectId();
			}
			else {
				println("update existing item record");
			}

			println("***2owner = ${jsonobj.owner}");

			def output_filename = "${image_repo_dir}/${item_record._id.toString()}"

			item_record.originalSource = [type:'external',uri:remote_image_url]
			item_record.workId = jsonobj._id;
			item_record.workflowType = 'original'
			item_record.mimeType = 'application/jpg'
			item_record.pathInStore = output_filename
			item_record.createDate = System.currentTimeMillis();

			// Read the remote image file into the local file
			def out_file = new FileOutputStream(output_filename)
			def out_stream = new BufferedOutputStream(out_file)
			out_stream << new URL(jsonobj.expressions[0].manifestations[0].uri).openStream()
			out_stream.close()
			db.item.save(item_record);

			println("***3owner = ${jsonobj.owner}");
			def esent = [:]
			esent._id = jsonobj._id;
			esent.title = jsonobj.title;
			esent.description = jsonobj.description;
			esent.identifier = jsonobj.identifier;
			esent.lastModified = jsonobj.lastModified;
			esent.owner = jsonobj.owner;
			createESEntry(esclient, esent);

			println("***4owner = ${jsonobj.owner}");
			println("****esent.owner = ${esent.owner}");
			println("Create public secure copy");
			createSecureCopy(db,image_repo_dir, jsonobj, item_record, 'SecureCopy', 'MediaProjectOwner');

			println("Create thumbnail");
			createSecureCopy(db,image_repo_dir, jsonobj, item_record, 'Thumbnail', 'MediaProjectOwner','140x140>');

			println("New item has id ${item_record._id} and saved in ${output_filename}");

			println("*complete*");

			// def result = writer.toString();
			// tse.removeGraph("urn:xcri:course:${jsonobj._id}");
			// tse.update(result, "urn:xcri:course:${jsonobj._id}", 'application/rdf');
			// println("Updated provider ( ${System.currentTimeMillis() - starttime} )");
		}

		println("Completed after ${reccount} records in ${System.currentTimeMillis() - starttime}ms");


		//TODO
	}


	/**
	 *  Create a copy of the original item.
	 *  Steg hide the ID of the new item in the image itself
	 *  Add the id of the image to the exif and the XMP metadata
	 *  Create the new item
	 */
	def createSecureCopy(db,image_repo_dir, work, original_item, workflowType, owner, resize=null) {

		def new_item_id = new org.bson.types.ObjectId();
		def new_file_name = "${image_repo_dir}/${new_item_id}"

		def new_item = [:]
		new_item._id = new_item_id
		new_item.workId = work._id;
		new_item.workflowType = workflowType
		new_item.createDate = System.currentTimeMillis();
		new_item.pathInStore = new_file_name

		println("Create secure copy from original... New item id is ${new_item_id}, store location will be ${new_file_name}");

		// Copy....
		def copy_cmd = "cp ${original_item.pathInStore} ${new_file_name}"
		println("copy: ${copy_cmd}");
		def process = copy_cmd.execute()

		// Augment metadata

		// Resize
		if ( resize ) {
			println("Resize...");
			imageResizeService.resize(new_file_name, resize);
		}


		// Watermark
		println("watermark...");
		imageWatermarkService.watermark(new_file_name, 'MEDIA');

		// steg hide item identifier
		stegHideStegService.hide(new_item_id,  new_file_name);

		// Embed metadata
		embedXMP(new_item_id,owner,new_file_name);

		// save
		db.item.save(new_item);
	}

	def embedXMP(identifier,owner,target) {
		exivMetadataWrapperService.embed(identifier,owner,target);
	}

	def createESEntry(esclient, entry) {

		entry.indexTime = new Date();

		try {
			def future = esclient.index {
				index "media"
				type "work"
				id entry._id
				source entry
			}

			println("==============After indexing future = " + future)
			println("future.response: " + future.getResponse());
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		finally {
		}

	}
	
	def init() {
		
		org.elasticsearch.groovy.common.xcontent.GXContentBuilder.rootResolveStrategy = Closure.DELEGATE_FIRST
		
		  org.elasticsearch.groovy.node.GNode esnode = elasticSearchWrapperService.getNode()
		  org.elasticsearch.groovy.client.GClient esclient = esnode.getClient()
		
		  // Get hold of an index admin client
		  org.elasticsearch.groovy.client.GIndicesAdminClient index_admin_client = new org.elasticsearch.groovy.client.GIndicesAdminClient(esclient);
		
		
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
			println("Installed course mapping ${future}");
		
		  //// Check to see if the index already exists and create it if not
		  //try {
			 //index_admin_client.status(new org.elasticsearch.action.admin.indices.status.IndicesStatusRequest('media'));
			//System.err.println("No exception thrown, so index must exist....");
			//    getClient().admin().indices().status(indicesStatusRequest(index)).actionGet();
			//} catch(e) {
				//// Index does not exist yet.
		//System.err.println("Index doesn't exist so want to create it...");
				//getClient().admin().indices().create(createIndexRequest(index)).actionGet();
				//establishedIndicies.push(index);
			//}
		
		
		  // Create an index if none exists
		  //def future = index_admin_client.create {
			//index 'media'
		  //}
		
			//println("future.response: " + future.getResponse());
		
		  esclient
		}
}
