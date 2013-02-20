package com.k_int.sgdrm.image

class StegShowStegService {
    
    
    
	@javax.annotation.PostConstruct
	def init() {
		log.debug("StegShowStegService::init called");
	}

	/**
	 * Embed the identified properties as XMP attributes in the given file.
	 */
	def show(filepath, stegpath) {
		log.debug("StegShowStegService::hide called");
		
		// Create a file containing the identifier
		def steg = "${stegpath}.stegid"
		//def identifier_file = new File(identifier_filename);
		//identifier_file << "${identifier}\n";

		// Create a work directory
		//log.debug("embed ${identifier_filename} in ${filepath}");
                println "steghide extract -v -f -xf ${filepath} -p media"
		// TODO - change this to get the passphrase from configuration..
		def steg_cmd = "steghide extract -v -f -sf ${filepath} -p media -xf ${steg}"

		// println("run ${steg_cmd}");
		def process = steg_cmd.execute()
                
           
		process.err.eachLine { line ->
			log.error("Error output when calling steghide: " + line);
		}

		process.in.eachLine { line ->
			debug.log("Output when calling steghide: " + line);
		}
                println "HERE " +steg
                
                File f = new File(steg)
                String out = f.eachLine{
                line-> line }
                f.delete()
                println "OUT: " +out
               return out
	}
    
    

}
