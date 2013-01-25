class UrlMappings {

	static mappings = {
            
                
                "/login/$action"(controller:"login")
                "/logout/$action"(controller:"logout")
                "/register/$action"(controller:"register")
				"/sysadmin/$action/$id?"(controller:"systemAdmin")
				"/sysadmin"(controller:"systemAdmin", action:"index")
                "/context/$action"(controller:"context")
                "/store/$action"(controller:"store")
				"/create/$action?"(controller:"createMedia")
				"/image/$action?"(controller:"image")
                "/$context" (controller:"context", action:"index")
                "/$context/$store"(controller:"store", action:"index")
                "/$context/$store/connectors/admin/$subAction/$connectorId?"(controller:"store", action:"adminConnector")
                "/$context/$store/connectors/$subAction/$connectorId?"(controller:"store",action:"viewConnector")
                "/$context/$store/$action"(controller:"store")
				"/"(controller:"home",action:"index")
                "/home/$action"(controller:"home")
        
		"500"(view:'/error')
		"404"(view:'/notFound')
		"403"(view:'/unauthorised')
	}
}
