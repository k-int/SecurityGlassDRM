import com.k_int.sgdrm.*;

import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import grails.converters.JSON

class BootStrap {

  def ESWrapperService
  def grailsApplication

  def init = { servletContext ->

    log.debug("Setting up users and roles...");

    def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)
    def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR').save(failOnError: true)
    def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)

    log.debug("localauth is set.. ensure user accounts present");

    log.debug("Create admin user...");
    def adminUser = User.findByUsername('admin')
    if ( ! adminUser ) {
      def newpass = java.util.UUID.randomUUID().toString()
      log.debug("No admin user found, create with temporary password ${newpass}")
      adminUser = new User(
                      username: 'admin',
                      password: 'admin',
                      email: 'admin@localhost',
                      enabled: true).save(failOnError: true)
    }

    if (!adminUser.authorities.contains(adminRole)) {
      UserRole.create adminUser, adminRole
    }

    if (!adminUser.authorities.contains(userRole)) {
      UserRole.create adminUser, userRole
    }

    def free_plan = Plan.findByName("Free") ?: new Plan(name:"Free").save();
    def basic_plan = Plan.findByName("Basic") ?: new Plan(name:"Basic").save();

    def free_store_type = ContentStoreType.findByName("Free") ?: new ContentStoreType(name:"Free").save();
    def clean_store_type = ContentStoreType.findByName("Clean") ?: new ContentStoreType(name:"Clean").save();
    
    // Create the different context types
    def user_context_type = ContextType.findByName("User") ?: new ContextType(name:"User").save();
    def org_context_type = ContextType.findByName("Organisation") ?: new ContextType(name:"Organisation").save();

	// Create the different connector statuses
	def idle_status = ConnectorStatus.findByName("Idle") ?: new ConnectorStatus(name: "Idle").save();
	def requested_status = ConnectorStatus.findByName("Harvest requested") ?: new ConnectorStatus(name: "Harvest requested").save();
	def queued_status = ConnectorStatus.findByName("Queued") ?: new ConnectorStatus(name: "Queued").save();
	def running_status = ConnectorStatus.findByName("Running") ?: new ConnectorStatus(name: "Running").save();
	def disabled_status = ConnectorStatus.findByName("Disabled") ?: new ConnectorStatus(name: "Disabled").save();
	def completed_status = ConnectorStatus.findByName("Completed") ?: new ConnectorStatus(name: "Completed").save();
	
	
	// Register JSON marshaller for the RepositoryConnector
	JSON.registerObjectMarshaller(RepositoryConnector) {
		def returnArray = [:]
		returnArray['id'] = it.id
		
		def connStatusArray = [:]
		connStatusArray['id'] = it.connectorStatus.id
		connStatusArray['name'] = it.connectorStatus.name
		returnArray['connectorStatus'] = connStatusArray
		
		returnArray['encoding'] = it.encoding
		returnArray['metadataPrefix'] = it.metadataPrefix
		returnArray['name'] = it.name
		returnArray['setSpec'] = it.setSpec
		returnArray['statusChangeTime'] = it.statusChangeTime
		
		def storeArray = [:]
		storeArray['id'] = it.store.id;
		storeArray['class'] = it.store.class
		
		returnArray['store'] = storeArray
		
		returnArray['url'] = it.url

		return returnArray
	}

	// And for the Context itself
	JSON.registerObjectMarshaller(Context) {
		def returnArray = [:]
		
		returnArray['class'] = it.class;
		returnArray['id'] = it.id
		
		def contextTypeArray = [:]
		contextTypeArray['class'] = it.contextType.class
		contextTypeArray['id'] = it.contextType.id
		contextTypeArray['name'] = it.contextType.name
		returnArray['contextType'] = contextTypeArray;
		
		returnArray['name'] = it.name
		
		def ownerArray = [:]
		ownerArray['class'] = it.owner.class
		ownerArray['id'] = it.owner.id
		ownerArray['username'] = it.owner.username
		returnArray['owner'] = ownerArray
		
		return returnArray;
	}
  }

  def destroy = {
  }
}
