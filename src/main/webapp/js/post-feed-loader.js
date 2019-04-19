
function setUpFeed () { 
	console.log("I hope it works");

	fetch('/post-feed')
	.then((response) => {
	return response.json()
	}
	)

  }



