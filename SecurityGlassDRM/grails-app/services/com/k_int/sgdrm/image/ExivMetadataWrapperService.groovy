package com.k_int.sgdrm.image

import groovy.xml.MarkupBuilder

class ExivMetadataWrapperService {

	/**
	 * Embed the identified properties as XMP attributes in the given file.
	 */
	def embed(identifier, owner, license, target) {

		log.debug("ExivMetadataWrapperService::embed called");
		def xmpfile_name = "${target}.xmp"
		makeXMPFile(identifier,owner, license, xmpfile_name);

		def cmd = "exiv2 -v -i X insert ${xmpfile_name} ${target}"
		def process = cmd.execute()
		process.in.eachLine { line ->
			log.debug("Output from embedding data in image: " + line);
		}

		// May try process.withWriter { writer -> <cr> writer << 'test text' }
	}

	/**
	 * Extract any xmp metadata from the given file
	 */
	def extract(file) {
		log.debug("extract XMP from ${file}");
		def process = "exiv2 -PXkv ${file}".execute()
		process.in.eachLine { line ->
			log.debug("Existing XMP metadata in file: " + line);
		}
	}

	def makeXMPFile(identifier,owner, license,xmpfile_name) {
		log.debug("ExivMetadataWrapperService::makeXMPFile called");
		def writer = new StringWriter()
		
		writer.append("<?xpacket begin=\"?\" id=\"${identifier}\"?>");
		
		writer.append("<x:xmpmeta xmlns:x=\"adobe:ns:meta/\" x:xmptk=\"XMP Core 4.4.0-Exiv2\">");
		writer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
		writer.append("<rdf:Description rdf:about=\"uri:media:${identifier}\" xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\" xmlns:media=\"http://k-int.com/ns/media\" ");
		writer.append("xmp:CreatorTool=\"");
		writer.append("Media Project XMP Metadata Embedding Agent");
		writer.append("\" media:owner=\"");
		writer.append("${owner}")
		writer.append("\" media:license=\"");
		writer.append("${license}");
		writer.append("\">")
		writer.append("</rdf:Description>")
		writer.append("</rdf:RDF>");
		writer.append("</x:xmpmeta>");
		
		writer.append("<?xpacket end=\"w\"?>");
		
//		
//		def xml = new MarkupBuilder(writer)
//
//		xml.'x:xmpmeta'('xmlns:x': 'adobe:ns:meta/',
//				'xmlns:rdf' : 'http://www.w3.org/1999/02/22-rdf-syntax-ns#',
//				'xmlns:xmp' : 'http://ns.adobe.com/xap/1.0/',
//				'xmlns:media' : 'http://k-int.com/ns/media') {
//					xml.'rdf:RDF'() {
//						xml.'rdf:Description'('rdf:about':"uri:media:${identifier}") {
//							xml.'xmp:CreatorTool'() {
//								// TODO - change this to be configurable..
//								mkp.yield('MEDIA Project XMP Metadata Embedding Agent')
//							}
//							xml.'media:owner'() {
//								mkp.yield("http://media/owner/${owner}")
//							}
//							xml.'media:license'() {
//								mkp.yield("http://media/license/${identifier}")
//							}
//						}
//					}
//				}
		new File(xmpfile_name) << writer.toString();
	}

}