grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://oss.sonatype.org/content/repositories/releases"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.20'
        runtime 'mysql:mysql-connector-java:5.1.21'
        runtime 'com.gmongo:gmongo:0.9.5'
        runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.1.0'
        runtime 'org.elasticsearch:elasticsearch:0.19.8'
		compile 'org.apache.httpcomponents:httpmime:4.1.2'
		compile 'org.apache.httpcomponents:httpclient:4.0'
		compile 'org.codehaus.groovy.modules.http-builder:http-builder:0.5.0'
        compile('org.springframework.security:spring-security-core:3.0.7.RELEASE') {
            excludes 'com.springsource.org.aopalliance',
                'com.springsource.org.apache.commons.logging',
                'org.springframework.beans',
                'org.springframework.context',
                'org.springframework.core'
        }

    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.0"
        runtime ":jquery-ui:1.8.15"
        runtime ":resources:1.2-RC1"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"

    }
}
