package com.k_int.sgdrm

import org.elasticsearch.groovy.node.GNode
import org.elasticsearch.groovy.node.GNodeBuilder
import static org.elasticsearch.groovy.node.GNodeBuilder.*


class ElasticSearchWrapperService {

  def gNode = null;

  public ElasticSearchWrapperService() {
    println("Construct ElasticSearchWrapperService");
  }

  @javax.annotation.PostConstruct
  def init() {
    println("init - post construct");
    // log.debug("Init");

    // System.setProperty("java.net.preferIPv4Stack","true");
    // log.debug("Attempting to create a transport client...");
    // Map<String,String> m = new HashMap<String,String>();
    // m.put("cluster.name","aggr");
    // Settings s = ImmutableSettings.settingsBuilder() .put(m).build();
    // TransportClient client = new TransportClient(s);

    // If there is a aggr.dev.es.cluster=iidevaggr setm us it, otherwise cluster name is aggr

    def clus_nm = "aggr"

    // log.info("Using ${clus_nm} as cluster name...");
    println("Connect to cluster ${clus_nm}");

    //org.elasticsearch.groovy.common.xcontent.GXContentBuilder.rootResolveStrategy = Closure.DELEGATE_FIRST;
    def nodeBuilder = new org.elasticsearch.groovy.node.GNodeBuilder()

    // log.debug("Construct node settings");


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
    //nodeBuilder.settings {
      //node {
        //client = true
      //}
      //cluster {
        //name = clus_nm
      //}
      //http {
        //enabled = false
      //}
    //}

    // log.debug("Constructing node...");
    gNode = nodeBuilder.node()

    println("Completed ElasticSearchWrapperService::init");
    // log.debug("Init completed");
  }

  @javax.annotation.PreDestroy
  def destroy() {
    // log.debug("Destroy");
    gNode.close()
    // log.debug("Destroy completed");
  }

  def getNode() {
    // log.debug("getNode()");
    gNode
  }

}