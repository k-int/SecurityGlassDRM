class UrlMappings {

	static mappings = {
            
                
                "/login/$action"(controller:"login")
                "/logout/$action"(controller:"logout")
                "/register/$action"(controller:"register")
                "/$uploadContext"(controller:"context", action:"index")
		"/"(controller:"home",action:"index")
                "/home/$action"(controller:"home")
        
		"500"(view:'/error')
	}
}
