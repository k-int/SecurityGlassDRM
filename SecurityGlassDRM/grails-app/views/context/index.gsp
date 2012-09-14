<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap" />
<title>Context home</title>
</head>

<body>
	<div class="row-fluid">
		<section id="main" class="span12">

			<div class="hero-unit row">
				<div class="span8">
					<h1>
						${specifiedContext}
						Context
					</h1>
				</div>
				<div class="span4">

                    <g:render template="contextChooser" contextPath="/"/>
				</div>

			</div>

			<div class="row">

				<div class="span12">

					<h2>Stores within this context:</h2>
					<g:if test="${stores}">
						<ul>
							<g:each in="${stores}" var="store">
								<li>
								    <a href="${specifiedContext}/${store.name}">${store.name}</a>
								</li>
							</g:each>
						</ul>
					</g:if>
					<g:else>
						<p class="unimportant">There are currently no stores in this
							context. Create one to start importing content.</p>
					</g:else>

					<g:link controller="store" action="createContentStore"
						params="[storeOwner:specifiedContext]" class="btn btn-success">Create new content store</g:link>


				</div>

			</div>

		</section>


	</div>
</body>
</html>
