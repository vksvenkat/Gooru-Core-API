<script>
	var heightMessengerUrl = '<%=request.getAttribute("heightMessenger.URL")%>';

	function sendUnloadMessage(forwardCall) {

		var message = "unload@0";

		var elem = document.getElementById("heightProxy");  
		elem.contentWindow.location.replace(heightMessengerUrl + "#" + message);
		
		var width = parseInt(elem.width);
	   	
		elem.width = width > 100 ? 100 : 200;	

		setTimeout(forwardCall,200);			

	}
	
	//Send the message to the parent for setting the height
	function sendHeightMessage(containerDiv) {
			
		var iframeHeight = document.getElementById(containerDiv).offsetHeight + 200;

		sendHeight(iframeHeight);		
	}

	function sendHeight(height) {
		
		var iframeHeight = height;
		var currentURL = self.location;
		
		var message = iframeHeight + "@" + currentURL;
		//var iframeHeight = document.body.offsetHeight + 100;
		
		var elem = document.getElementById("heightProxy");  

	   	elem.contentWindow.location.replace(heightMessengerUrl + "#" + message);
	   	
	   	var width = parseInt(elem.width);
	   	
		elem.width = width > 100 ? 100 : 200;
		
	}

	function sendTeachMessage(classPlanId){
		
		var currentURL = self.location;
		var message = "teach@" + classPlanId;

		var elem = document.getElementById("heightProxy");  

	   	elem.contentWindow.location.replace(heightMessengerUrl + "#" + message);

		var width = parseInt(elem.width);
	   	
		elem.width = width > 100 ? 100 : 200;
	}

	function sendSearchMessage() {
		
		var currentURL = "www.google.com";
		
		var message = "1300@" + currentURL;
		//var iframeHeight = document.body.offsetHeight + 100;
		
		var elem = document.getElementById("heightProxy");  
	   	elem.contentWindow.location.replace(heightMessengerUrl + "#" + message);
	   	
	   	var width = parseInt(elem.width);
	   	
		elem.width = width > 100 ? 100 : 200;		
	}

			
</script>
<iframe name = "heightProxy" id ="heightProxy" src = "<%=request.getAttribute("heightMessenger.URL")%>" style="position:absolute; top:-940px; left:-450px" width="200" height="100"> </iframe>
