package com.k_int.sgdrm

class RepositoryMonitorService {

	def iterateLatest(db, collection, max_iterations, processing_closure) throws RepositoryMonitorException {

		log.debug("RepositoryMonitorService::iterateLatest called");

		// Lookup a monitor record for the identified collection
		// Create one if it doesn't exist.
		def monitor_info = null
		def mq = db.monitors.find(coll:collection);
		if ( mq.size() == 0 ) {
			log.debug("Create new monitor info for ${collection}");
			monitor_info = [:]
			monitor_info.coll = collection
			monitor_info.maxts = 0;
			monitor_info.maxid = null;
			def sr = db.monitors.save(monitor_info);
			log.debug("Result of save: ${sr}");
			// Look up the new monitor, so we have one with an _id set.
			monitor_info = db.monitors.findOne(coll:collection);
		}
		else if ( mq.size() == 1 ) {
			monitor_info = mq[0];
		}
		else {
			log.error("Multiple monitor entries found for this collection - can't continue");
			throw(new RepositoryMonitorException("Multiple monitor entries found for this collection - unable to process new records"));
		}

		def next=true;
		def batch_size = 10;
		def iteration_count = 0;

		while( ( ( max_iterations == -1 ) || ( iteration_count < max_iterations ) ) && next) {

			next=false;

			log.debug("${next} Finding all entries from ${collection} where lastModified > ${monitor_info.maxts}");
			def batch

			log.debug("Process all items since ts:${monitor_info.maxts}");
			batch = db."${collection}".find( [ lastModified : [ $gt : monitor_info.maxts ] ] ).sort(lastModified:1).limit(batch_size+1);

			log.debug("Query completed, batchsize = ${batch.size()}");

			int counter = 0;

			batch.each { r ->
				if ( counter < batch_size ) {
					counter++;
					processing_closure.call(r)
					monitor_info.maxid = r._id;
					monitor_info.maxts = r.lastModified;
					log.debug("* ${iteration_count}/${counter}/${batch_size} : ${monitor_info.maxts}, ${monitor_info.maxid}");
				}
				else {
					// We've reached record batch_size+1, which means there is at least 1 more record to process. We should loop,
					// assuming we haven't passed max_iterations
					log.debug("Counter has reached ${batch_size+1}, reset maxid");
					log.debug("First record of next batch should be ${r._id}");
					next=true
				}
			}
			log.debug("Saving monitor info ${monitor_info}");
			db.monitors.save(monitor_info);
			iteration_count++;
		}

		log.debug("Monitor completed iterating through ${collection} entries");
	}
}