import com.k_int.sgdrm.*;

import com.k_int.kbplus.auth.*
import org.codehaus.groovy.grails.plugins.springsecurity.SecurityFilterPosition
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

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

  }

  def destroy = {
  }
}
