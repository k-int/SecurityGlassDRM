package com.k_int.sgdrm

import org.elasticsearch.groovy.node.GNode
import org.elasticsearch.groovy.node.GNodeBuilder
import static org.elasticsearch.groovy.node.GNodeBuilder.*
import org.elasticsearch.groovy.client.GClient
import org.elasticsearch.groovy.client.GIndicesAdminClient


class ElasticSearchWrapperService {

	def gNode = null;

	public ElasticSearchWrapperService() {
		log.debug("Construct ElasticSearchWrapperService");
	}

	@javax.annotation.PostConstruct
	def init() {
		
		// TODO - change the following to get values from configuration..
		def clus_nm = "aggr"

		log.info("Using ${clus_nm} as cluster name...");

		// Make sure that ES actually works..
		org.elasticsearch.groovy.common.xcontent.GXContentBuilder.rootResolveStrategy = Closure.DELEGATE_FIRST;

		log.debug("Construct node settings");
		def nodeBuilder = new org.elasticsearch.groovy.node.GNodeBuilder()

		nodeBuilder.settings {
			node {
				client = true
			}
			cluster {
				name = clus_nm
			}
			http {
				enabled = false
			}
			discovery {
				zen {
					minimum_master_nodes=1
					ping {
						unicast {
							hosts = [ "localhost" ]
						}
					}
				}
			}
		}

		gNode = nodeBuilder.node()

		log.debug("Init completed");
	}

	@javax.annotation.PreDestroy
	def destroy() {
		log.debug("Destroy");
		gNode.close()
		log.debug("Destroy completed");
	}

	def getNode() {
		log.debug("getNode()");
		gNode
	}

	def getESClient() {
		gNode.getClient()
	}
	
}