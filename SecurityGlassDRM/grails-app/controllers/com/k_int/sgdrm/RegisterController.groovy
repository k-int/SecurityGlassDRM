package com.k_int.sgdrm

import grails.plugins.springsecurity.ui.RegisterCommand
import org.codehaus.groovy.grails.plugins.springsecurity.NullSaltSource
import org.codehaus.groovy.grails.plugins.springsecurity.ui.RegistrationCode
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import com.k_int.sgdrm.Context;
import com.k_int.sgdrm.ContextType;

class RegisterController extends grails.plugins.springsecurity.ui.RegisterController {
    
    
    def mailService
    def messageSource
    def saltSource

    // Overrides the register method
    def register = { RegisterCommand command ->

        // Check to see if there's already an organisation with this name and complain if there is
        
        def existingContext = Context.findByName(command.username);
        if ( existingContext ) {
            log.debug("There's already a context with the specified username - complain");
            command.errors.rejectValue('username', "com.k_int.sgdrm.username.in.use.message");
        } else {
            // No existing context, so safe to create
            log.debug("No existing context with the specified username - OK to continue creating the user");
        }
        
        if (command.hasErrors()) {
                render view: 'index', model: [command: command]
                return
        }

        String salt = saltSource instanceof NullSaltSource ? null : command.username
        def user = lookupUserClass().newInstance(email: command.email, username: command.username,
                        accountLocked: true, enabled: true)

        RegistrationCode registrationCode = springSecurityUiService.register(user, command.password, salt)
        if (registrationCode == null || registrationCode.hasErrors()) {
                // null means problem creating the user
                flash.error = message(code: 'spring.security.ui.register.miscError')
                flash.chainedParams = params
                redirect action: 'index'
                return
        }

        // Create the context for the user
        log.debug("About to create the user's context as part of registering the user");
        def userContextType = ContextType.findByName("User");
        def userContext = new Context(name: command.username, contextType:userContextType, owner: user);
        if ( !userContext.save(flush:true) ) {
            userContext.errors.each() {
                log.error("Error: " + it);
            }
        }
        log.debug("Finished creating the user's context id: " + userContext.id);
        
        String url = generateLink('verifyRegistration', [t: registrationCode.token])

        def conf = SpringSecurityUtils.securityConfig
        def body = conf.ui.register.emailBody
        if (body.contains('$')) {
                body = evaluate(body, [user: user, url: url])
        }
        mailService.sendMail {
                to command.email
                from conf.ui.register.emailFrom
                subject conf.ui.register.emailSubject
                html body.toString()
        }

        render view: 'index', model: [emailSent: true]
    }
}
