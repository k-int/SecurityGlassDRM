package com.k_int.sgdrm.image



class StegHideStegService {


	@javax.annotation.PostConstruct
	def init() {
		log.debug("StegHideStegService::init called");
	}

	/**
	 * Embed the identified properties as XMP attributes in the given file.
	 */
	def hide(identifier, filepath) {
		log.debug("StegHideStegService::hide called");
		
		// Create a file containing the identifier
		def identifier_filename = "${filepath}.stegid"
		def identifier_file = new File(identifier_filename);
		identifier_file << "${identifier}\n";

		// Create a work directory
		log.debug("embed ${identifier_filename} in ${filepath}");

		// TODO - change this to get the passphrase from configuration..
		def steg_cmd = "steghide embed -v -ef ${identifier_filename} -p media -cf ${filepath}"

		// println("run ${steg_cmd}");
		def process = steg_cmd.execute()

		process.err.eachLine { line ->
			log.error("Error output when calling steghide: " + line);
		}

		process.in.eachLine { line ->
			log.debug("Output when calling steghide: " + line);
		}
	}

}