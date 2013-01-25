<!doctype html>
<html>
<head>
<meta name="layout" content="bootstrap" />
<title>System administration home</title>
</head>

<body>
	<div class="row-fluid">

		<section id="main">

			<div class="hero-unit row">
				<div class="page-header span12">
					<h1>System Administration</h1>
				</div>
			</div>

			<div class="row">
				<div class="span12">
					<h2>Background job status</h2>
					<div id="jobStatusDiv" class="row-fluid">
						<table id="jobStatusTable" class="table table-hover">
							<thead>
							<tr>
								<th>Name</th>
								<th>Description</th>
								<th>Last Run Time</th>
								<th>Status</th>
							</tr>
							</thead>
							<tbody>
								<td colspan="4">
									<p class="unimportant">Loading..</p>
								</td>
							</tbody>
						</table>
					</div>
				</div>
			</div>

		</section>

	</div>

	<script type="text/javascript">
		$(document).ready(function() {
			setupJobStatusData();
		});

		function setupJobStatusData() {
			$.getJSON("sysadmin/getJobStatus", function() {
			}).success(function(json) {
				processJobStatus(json);

			}).error(function(jqXHR, textStatus, errorThrown) {
				alert("Error getting back relevant job status information");
				console.log("error " + textStatus);
				console.log("incoming Text " + jqXHR.responseText);
			}).complete(function() {
			});
		}

		function processJobStatus(json) {
			// Receives JSON of form: [{id: 1,active: false,name: "OAISchedulingJob",lastRunTime: null,description: null},...]

			// Grab the table to accept the data
			var jobStatusTable = $('#jobStatusTable').find("tbody");
			jobStatusTable.html("");

			$.each(json, function() {
				var tableRow = $("<tr></tr>");
				var thisName = this.name;
				var thisDesc = this.description;
				var thisLastRunTime = this.lastRunTime;
				var thisActive = this.active;
				
				tableRow.append("<td>" + this.name + "</td>");
				tableRow.append("<td>" + this.description + "</td>");
				if ( this.lastRunTime != null )
					tableRow.append("<td>" + this.lastRunTime + "</td>");
				else 
					tableRow.append("<td>Not yet run</td>")

				var tableCell = $("<td></td>");

				if ( this.active ) {
					// Job is active - put in a disable button
					tableCell.append("Active&nbsp;");
					tableCell.append("<a href='#' onclick='toggleJobStatus("+this.id+");' title='Disable the task'><i class='icon-stop'></i></a>");
				} else {
					// Job is inactive - put in an activate button
					tableCell.append("Inactive&nbsp;");
					tableCell.append("<a href='#' onclick='toggleJobStatus("+this.id+");' title='Enable the task'><i class='icon-play'></i></a>");
				}

				tableRow.append(tableCell);

				jobStatusTable.append(tableRow);
			});
			
		}

		function toggleJobStatus(jobStatusId) {

			$.getJSON("sysadmin/toggleJobStatus/" + jobStatusId, function() {})
            .success(function(json) { 
              // Reload the connector information on the page
              setupJobStatusData();

            })
            .error(function(jqXHR, textStatus, errorThrown) {
              alert("Error toggling the status of the specified task");
              console.log("error " + textStatus);
              console.log("incoming Text " + jqXHR.responseText);
            })
            .complete(function() {
            });

            return false;
		}
	</script>
</body>
</html>
