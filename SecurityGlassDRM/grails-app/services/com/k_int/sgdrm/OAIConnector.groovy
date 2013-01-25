package com.k_int.sgdrm

import com.gmongo.GMongo
import groovy.xml.MarkupBuilder
import org.apache.commons.logging.LogFactory
import java.util.zip.*;
import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import java.nio.charset.Charset
import org.codehaus.groovy.grails.web.context.ServletContextHolder;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes;
import org.springframework.context.ApplicationContext;

import org.apache.http.conn.HttpHostConnectException


class OAIConnector {
	
	

	def sync(oai_connector_info) {
		log.debug("OAIConnector::sync(${oai_connector_info})");
		
		
		
		// Get a handle to the local mongo service
		// def mongo = new com.gmongo.GMongo();
		// def db = mongo.getDB("media_rcs")

		ApplicationContext context = (ApplicationContext)ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
		log.debug("context = " + context);
		def applicationConfig = context.getBean("grailsApplication");
		def repo_baseurl = applicationConfig.config.com.k_int.sgdrm.repoUploadUrl.toString();
		def repo_identity = applicationConfig.config.com.k_int.sgdrm.repoUploadUsername.toString();
		def repo_credentials = applicationConfig.config.com.k_int.sgdrm.repoUploadPass.toString();
		log.debug("Assemble repository client to ${repo_baseurl} - ${repo_identity}/${repo_credentials}");

		def aggregator_service = new HTTPBuilder( repo_baseurl )
		aggregator_service.auth.basic repo_identity, repo_credentials

		if ( oai_connector_info == null ) {
			println("No connector info... abort");
			return;
		}

		if ( !oai_connector_info.props )
			oai_connector_info.props = [:];
		oai_connector_info.props.reccount = 0;
		oai_connector_info.props.maxts = "";
		oai_connector_info.props.status = "";
		oai_connector_info.props.message = "";

		def oai_endpoint = new RESTClient( oai_connector_info.baseuri )
		def resumptionToken = null;
		
		while(true) {
			
			// Go and get a page of records from the server
			def recordData = listRecords(oai_endpoint, oai_connector_info.prefix, null, null, oai_connector_info.setname, resumptionToken );
			
			log.debug("Got back recordData with size: " + recordData.size() + " and num of records: " + recordData.records.size());
			
			// Loop through each of the returned records and upload them into the repository
			try {
				
				recordData.records.each() { aRecord ->
				
					byte[] db = aRecord.metadata.getBytes('UTF-8')
	
					log.debug("About to make post request [${oai_connector_info.props.reccount} / ${oai_connector_info.props.maxts} / ${oai_connector_info.owner} / ${aRecord.identifier}]");
					uploadStream(db, aggregator_service, oai_connector_info.owner)

					
					oai_connector_info.props.reccount++;
					oai_connector_info.props.maxts = aRecord.datestamp.toString();
	
					try {
						Thread.sleep(500);
					}
						catch ( Exception e ) {
					}						
				}
				resumptionToken = recordData.resumptionToken;
	
				def cont = checkAgainstMaxBatchSize(oai_connector_info.props.reccount, oai_connector_info.maxbatch)
							
				// If we don't have a resumption token then stop trying to get records, otherwise wait for a few 
				// seconds and then continue with the next page
				if ( resumptionToken == null || !cont ) 
					break;
				else
					sleep(5000);
			} catch (HttpHostConnectException hhce) {
				// Failed to upload due to a connection issue - no point trying any more..
				log.debug("Not going to continue trying to upload records as a connection exception was thrown when attempting: " + hhce.getMessage());
				oai_connector_info.props.status = "error";
				oai_connector_info.props.message = "Unable to connect to the repository: " + hhce.getMessage();
				break;
			} catch (ConnectorException ce) {
				// Some other connector exception that is non-recoverable - give up trying..
				log.debug("Not going to continue trying to upload as a connector exception has been thrown: " + ce.getMessage());
				oai_connector_info.props.status = "error";
				oai_connector_info.props.message = ce.getMessage();
				break;
			}

		}
				
	}

	def checkAgainstMaxBatchSize(reccount, maxbatch) {
		def retval = true;

		if (maxbatch != null && maxbatch > 0 ) {
			println("Checking record counter (${reccount} < ${maxbatch}");
			if ( reccount >= maxbatch ) {
				retval = false;
			}
		}

		return retval;
	}

	def identify(oai_endpoint) throws OAIException {

		log.debug("OAIConnector::identify called with oai_endpoint: ${oai_endpoint}");

		def result = [:] // Set up the map for return data

		// Perform the identify request
		oai_endpoint.request(GET) {request ->

			uri.query = [ 'verb':'Identify']

			request.getParams().setParameter("http.socket.timeout", new Integer(5000))
			headers.Accept = 'application/xml'

			response.success = { resp, xml ->
				// log.debug( "Server Response: ${resp.statusLine}" )
				// log.debug( "Server Type: ${resp.getFirstHeader('Server')}" )
				// log.debug( "content type: ${resp.headers.'Content-Type'}" )

				xml?.Identify?.each { ident ->
					log.debug("Identify response:  ${ident.toString()}");

					result.repositoryName = ident.repositoryName?.toString();
					result.baseUrl = ident.baseURL?.toString();
					result.protocolVersion = ident.protocolVersion?.toString();
					result.adminEmail = ident.adminEmail?.toString();
					result.earliestDatestamp = ident.earliestDatestamp?.toString();
					result.deletedRecord = ident.deletedRecord?.toString();
					result.granularity = ident.granularity?.toString();

					def identifiers = [:];
					identifiers.scheme = ident.description?.oai-identifier?.scheme?.toString()
					identifiers.repositoryIdentifier = ident.description?.oai-identifier?.repositoryIdentifier?.toString()
					identifiers.delimiter = ident.description?.oai-identifier?.delimiter?.toString();
					identifiers.sample = ident.description?.oai-identifier?.sampleIdentifier?.toString();

					result.identifiers = identifiers;
				}
			}

			response.failure = { resp ->

				log.error("Failure when performing identify call: " + resp.statusLine);
				throw new OAIException(resp.statusLine);
			}
		}

		log.debug("Identify completed with parsed data: " + result);

		return result;
	}

	def listMetadataFormats(oai_endpoint) throws OAIException {

		log.debug("OAIConnector::listMetadataFormats called with oai_endpoint: ${oai_endpoint}");

		def result = [] // Set up the list for return data

		// Perform the ListMetadataFormats request
		oai_endpoint.request(GET) {request ->

			uri.query = [ 'verb':'ListMetadataFormats']

			request.getParams().setParameter("http.socket.timeout", new Integer(5000))
			headers.Accept = 'application/xml'

			response.success = { resp, xml ->
				// log.debug( "Server Response: ${resp.statusLine}" )
				// log.debug( "Server Type: ${resp.getFirstHeader('Server')}" )
				// log.debug( "content type: ${resp.headers.'Content-Type'}" )

				xml?.ListMetadataFormats?.metadataFormat?.each { mFormat ->
					log.debug("ListMetadataFormats individual metadata format response:  ${mFormat.toString()}");

					def thisFormat = [:];
					thisFormat.metadataPrefix = mFormat.metadataPrefix?.toString();
					thisFormat.schema = mFormat.schema?.toString();
					thisFormat.metadataNamespace = mFormat.metadataNamespace?.toString();

					result.add(thisFormat);
				}
			}

			response.failure = { resp ->

				log.error("Failure when performing list metadata formats call: " + resp.statusLine);
				throw new OAIException(resp.statusLine);
			}
		}

		log.debug("ListMetadataFormats completed with parsed data size: " + result.size());

		return result;
	}

	def listSets() {

		log.debug("OAIConnector::listSets called with oai_endpoint: ${oai_endpoint}");

		def result = [] // Set up the list for return data

		// Perform the ListSets request
		oai_endpoint.request(GET) {request ->

			uri.query = [ 'verb':'ListSets']

			request.getParams().setParameter("http.socket.timeout", new Integer(5000))
			headers.Accept = 'application/xml'

			response.success = { resp, xml ->
				// log.debug( "Server Response: ${resp.statusLine}" )
				// log.debug( "Server Type: ${resp.getFirstHeader('Server')}" )
				// log.debug( "content type: ${resp.headers.'Content-Type'}" )

				xml?.ListSets?.set?.each { aSet ->
					log.debug("ListSets individual set response:  ${aSet.toString()}");

					def thisSet = [:];
					thisSet.setSpec = aSet.setSpec?.toString();
					thisSet.setName = aSet.setName?.toString();
					thisSet.description = aSet.setDescription?.toString();

					result.add(thisSet);
				}
			}

			response.failure = { resp ->

				log.error("Failure when performing list sets call: " + resp.statusLine);
				throw new OAIException(resp.statusLine);
			}
		}

		log.debug("ListSets completed with parsed data size: " + result.size());

		return result;
	}

	def listRecords(oai_endpoint, metadataPrefix, from, until, setSpec, resumptionToken) {

		log.debug("OAIConnector::listRecords called with oai_endpoint: ${oai_endpoint}, metadataPrefix: ${metadataPrefix}, from: ${from}, until: ${until}, set: ${setSpec}, resumptionToken: ${resumptionToken}");

		def result = [:] // Set up the map for return data

		// Perform the ListRecords request
		oai_endpoint.request(GET) {request ->

			def requestParams = ["verb":"ListRecords"];
			
			if ( resumptionToken ) {
				// We have a resumption token
				requestParams.resumptionToken = resumptionToken; 
			} else {
				// Check that we have the required arguments..
				def allRequiredPresent = true;
				def messages = [];
				
				if ( !metadataPrefix ) {
					// No metadata prefix - can't continue
					allRequiredPresent = false;
					messages.add("Metadata prefix required but not specified");
					// TODO - how best to handle this..
				} else {
					requestParams.metadataPrefix = metadataPrefix;
				}
				
				if ( from ) {
					requestParams.from = from;
				}
				
				if ( until ) {
					requestParams.until = until;
				}
				
				if ( setSpec ) {
					requestParams.set = setSpec;
				}				
			}
			uri.query = requestParams;

			request.getParams().setParameter("http.socket.timeout", new Integer(5000))
			headers.Accept = 'application/xml'

			response.success = { resp, xml ->
				// log.debug( "Server Response: ${resp.statusLine}" )
				// log.debug( "Server Type: ${resp.getFirstHeader('Server')}" )
				// log.debug( "content type: ${resp.headers.'Content-Type'}" )

				def records = [];
				
				xml?.ListRecords?.record?.each() { aRecord ->
					
					def thisRecord = [:];
					thisRecord.identifier = aRecord.header?.identifier;
					thisRecord.datestamp = aRecord.header?.datestamp;
					thisRecord.setSpec = aRecord.header?.setSpec;
					
					// Get the actual record as XML again
					def builder = new StreamingMarkupBuilder()
					def new_record = builder.bindNode(aRecord.metadata.children()[0]).toString()
					
					thisRecord.metadata = new_record;
					
					records.add(thisRecord);
				}
				
				result.records = records;
				result.resumptionToken = xml.ListRecords?.resumptionToken?.toString();
			}

			response.failure = { resp ->

				log.error("Failure when performing list records call: " + resp.statusLine);
				throw new OAIException(resp.statusLine);
			}
		}

		log.debug("ListRecords completed with parsed data size: " + result.size());

		return result;
	}

	def listIdentifiers() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	def getRecord() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	def uploadStream(document_bytes,target_service, data_provider) throws HttpHostConnectException, ConnectorException {

		log.debug("About to make post request");

		try {
			byte[] resource_to_deposit = document_bytes

			log.debug("Length of input stream is ${resource_to_deposit.length}");

			target_service.request(POST) {request ->
				requestContentType = 'multipart/form-data'

				// Much help taken from http://evgenyg.wordpress.com/2010/05/01/uploading-files-multipart-post-apache/
				def multipart_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipart_entity.addPart( "owner", new StringBody( data_provider, "text/plain", Charset.forName( "UTF-8" )))  // Owner

				def uploaded_file_body_part = new org.apache.http.entity.mime.content.ByteArrayBody(resource_to_deposit, 'text/xml', 'filename')
				multipart_entity.addPart( "upload", uploaded_file_body_part)

				request.entity = multipart_entity;

				response.success = { resp, data ->
					println("response status: ${resp.statusLine}")
					println("Response data code: ${data?.code}");
					println("Response message: ${data?.message}");
				}

				response.failure = { resp ->
					log.debug("Failure attempting to deposit record - ${resp.statusLine} - data: ${resp.data}");
					throw(new ConnectorException("Failure depositing record: - ${resp.statusLine} - data: ${resp.data}"));
				}
			}
		}
		catch ( HttpHostConnectException hhce ) {
			// Unable to connect to the aggregator - need to not continue trying and so kick the error back on up
			// the call tree
			log.error("Connection exception thrown when trying to upload to the repository:"  + hhce.getMessage());
			throw hhce;
		}
		catch (ConnectorException ce) {
			// Some other error when connecting to the aggregator
			log.error("ConnectorException thrown when attempting to deposit: " + ce.getMessage());
			throw ce;
		}
		catch ( Exception e ) {
			log.error("Unexpected exception trying to read remote stream",e)
		}
		finally {
			log.debug("uploadStream try block completed");
		}
		log.debug("uploadStream completed");
	}

}

//
///*
//
//<culturegrid_item:description xmlns:culturegrid_item="http://www.peoplesnetwork.gov.uk/schema/CultureGrid_Item" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:e20cl="http://www.20thcenturylondon.org.uk" xmlns:pnds_dc="http://purl.org/mla/pnds/pndsdc/" xmlns:pndsterms="http://purl.org/mla/pnds/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.peoplesnetwork.gov.uk/schema/CultureGrid_Item http://www.peoplesnetwork.gov.uk/schema/CultureGrid_Item.xsd">
//<dc:identifier>1977-5750</dc:identifier>
//<dc:title>Painting</dc:title>
//<dc:description>
//Painting. Nairn by S.J. Lamorna Birch. Original painting for LMS poster
//</dc:description>
//<dc:subject>painting</dc:subject>
//<dc:subject>artwork</dc:subject>
//<dc:type encSchemeURI="http://purl.org/dc/terms/DCMIType">PhysicalObject</dc:type>
//<dcterms:license valueURI="http://creativecommons.org/licenses/by-nc-sa/2.5/">Creative Commons Licence</dcterms:license>
//<dcterms:rightsHolder>ScienceMuseum</dcterms:rightsHolder>
//<dcterms:isPartOf>National Railway Museum</dcterms:isPartOf>
//</culturegrid_item:description>
//
//
//<description xmlns="http://purl.org/mla/pnds/pndsdc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:e20cl="http://www.20thcenturylondon.org.uk/" xmlns:pndsterms="http://purl.org/mla/pnds/terms/">
//<dc:identifier>1977-5750</dc:identifier>
//<dc:identifier/>
//<dc:title>Painting</dc:title>
//<dc:description>
//Painting. Nairn by S.J. Lamorna Birch. Original painting for LMS poster
//</dc:description>
//<dc:publisher/>
//<dc:type>PhysicalObject</dc:type>
//<dcterms:rightsHolder/>
//<dcterms:isPartOf>NRM - Pictorial Collection (Railway)</dcterms:isPartOf>
//<dcterms:license valueURI="http://creativecommons.org/licenses/by-nc-sa/2.5/">Creative Commons Licence</dcterms:license>
//<dc:subject>painting</dc:subject>
//<dc:subject>artwork</dc:subject>
//<pndsterms:extension/>
//</description>
//*/
