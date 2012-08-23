class UrlMappings {

	static mappings = {
            
                
                "/login/$action"(controller:"login")
                "/logout/$action"(controller:"logout")
                "/register/$action"(controller:"register")
                "/context/$action"(controller:"context")
                "/store/$action"(controller:"store")
                "/$context" (controller:"context", action:"index")
                "/$context/$store"(controller:"store", action:"index")
		"/"(controller:"home",action:"index")
                "/home/$action"(controller:"home")
        
		"500"(view:'/error')
	}
}
