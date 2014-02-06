<%@ include file="/common/taglibs.jsp"%>

<html>
<head>
<title>Migration Utility</title>


<script type="text/javascript" src="scripts/jquery/jquery-1.4.4.js"></script>
<script type="text/javascript" src="scripts/jquery/jquery-blockUI.js"></script>


<script><!--
$(document).ready(function()
		{
	$.ajax ({	 
		type: "GET",
		url: "rest/teachers",
		dataType:"json",
		success: function(data)	{
		var options = "";
		for (var i = 0; i < data.users.length; i++) {
			options +=	'<option value="'+data.users[i].id +'">'+data.users[i].firstName+'</option>';
		}
		$("#owner").html(options);
		},
		error:function(request,status,error){
			if(request.status == 403){
				jAlert("You don't have permission to edit this classplan. But if you make a copy of it, you can edit the copied version to your heart's content.","Not so fast!");
			}else{
				jAlert("We're not perfect, but we're fixing this mistake!","Oops!");
			}
		}
	});

	$("input[name='mode']").change(
			function()
			{

				var var_mode = $("input[@name='mode']:checked").val();
				if(var_mode == "create") {
					$("#replaceDiv").css("display","none");
					$("#createDiv").css("display","block");
				} else if(var_mode ="replace") {
					$("#createDiv").css("display","none");
					$("#replaceDiv").css("display","block");
				}
			}
			);

	
				
		})


function importClassplan() {
	
	var classplanId = $("#classplanIdInput").val();
	var importURL = $("#importURL").val();
	var classplanLesson = $("#classplanLesson").val();

	
	if(classplanId == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}
	if(importURL == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}

	
	$.blockUI({ message: '<img src="images/blue-loading.gif"/>' });
	var owner = $("#owner").val();
	$.ajax ({	 
		type: "GET",
		url: "rest/importClassplan/"+classplanId+"?ownerId="+owner+"&importURL="+importURL+"&classplanLesson="+classplanLesson,
		dataType:"json",
		success: function(data)	{
			$.unblockUI();
			jAlert("We're not perfect, but we're fixing this mistake!","Oops!");
			$("#classplanLesson").val("");
			$("#classplanIdInput").val("");
			document.location.reload();
		}
	});
}

function replaceClassplan() {

	var classplanId = $("#classplanIdInput").val();
	var prevClassplanId = $("#prevClassplanIdInput").val();
	var importURL = $("#importURL").val();
	
	if(classplanId == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}
	if(prevClassplanId == "") {
		jAlert("We're not perfect, but we're fixing this mistake!","Oops!");
		return;
	}
	if(importURL == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}
	
	$.blockUI({ message: '<img src="images/blue-loading.gif"/>' });
	
	$.ajax ({	 
		type: "GET",
		url: "rest/replaceClassplan/"+prevClassplanId+"/"+classplanId+"?importURL="+importURL,
		dataType:"json",
		success: function(data)	{
			$.unblockUI();
			jAlert("Feels good, doesn't it?","Success!");
			$("#classplanIdInput").val("");
			document.location.reload();
		}
	});
}


function getClassplanZip()
{
	var classplanId = $("#classplanIdInput").val();
	var importURL = $("#importURL").val();
	
	if(classplanId == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}
	if(importURL == "") {
		jAlert("You forgot to provide a Classplan ID. How will you find it next time?","Hold on!");
		return;
	}

	$.get(importURL + "getClassplanZip.g", {
		method: "getClassplanZip", 
		classPlanId: classplanId,
		ref: "8dHjKh9K4keR"													
		}, function(data)
		{																	
			jAlert("We're not perfect, but we're fixing this mistake!","Oops!");
		}
	);

	
}
--></script>

</head>

<body>
	<div style="margin-top:10px;">Import URL : <input id= "importURL" size = "40" type="text" value="http://www.goorudemo.org"/></div>
	<div style="margin-top:10px;">Classplan Id: <input type="text" value="" id="classplanIdInput" /></div>
	<input style="margin-top:10px;" type="button" value="Generate" onclick="getClassplanZip()"/>
	
	<div style="margin-top:10px;">	
 		<input type="radio" name="mode" value="create" />Create New<br />
 		<input type="radio" name="mode" value="replace" />Replace Existing<br />
 	</div>
 	<div id="createDiv" style="display:none;margin-top:10px;">
		<div style="margin-top:10px;" >Title: <input type="text" value="" id="classplanLesson" /></div>
		<div style="margin-top:10px;" >Owner: 
			<select id="owner">
		 	</select>
		</div>
		<input style="margin-top:10px;" type="button" value="Import" onclick="importClassplan()"/>
	</div>
	
	<div id="replaceDiv" style="display:none;margin-top:10px;">
		<div style="margin-top:10px;">Existing Classplan Id: <input type="text" value="" id="prevClassplanIdInput" /></div>
		<input style="margin-top:10px;" type="button" value="Replace" onclick="replaceClassplan()"/>
	</div>

</body>
</html>