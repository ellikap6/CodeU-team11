function setUpFeed () {
	console.log("I hope it works");

	fetch('/post')
	.then((response) => {
	return response.json()
	}
	)

  }