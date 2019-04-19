function setUpFeed () {
	console.log("I hope it works");
  fetch("/post-feed")
      .then((response) => {
        return response.json();
      })
      .then((posts) => {
				const postContainer = document.getElementById('posts');
				console.log(postContainer);

        posts.forEach((post) => {
          const postDiv = buildPostDiv(
						post.coverImageUrl, post.title, post.creator, post.timestamp);
          postContainer.appendChild(postDiv);
        });
      });

	/*
	var postDiv = buildPostDiv(
		"https://images.pexels.com/photos/45201/kitty-cat-kitten-pet-45201.jpeg",
		"A week in the 4th quadrant",
	 	"ossalako@codeustudents",
		new Date());

	const postContainer = document.getElementById('posts');
	console.log(postContainer);
	postContainer.appendChild(postDiv);
	console.log(postContainer);
	*/
}

function buildPostDiv(imageUrl, title, username, timestamp) {
	const postDiv = document.createElement('div');
	postDiv.classList.add('post');

	const image = document.createElement('div');
	image.innerHTML = '<img src="' + imageUrl + '" height="100" width="100"/>';

	const titleP = document.createTextNode(title);
	const descriptionP = document.createTextNode("By " + username + " on " + new Date(timestamp);

	postDiv.appendChild(image);
	postDiv.appendChild(titleP);
	postDiv.appendChild(descriptionP);
	console.log(image);
	console.log(titleP);
	console.log(descriptionP);

	return postDiv;
}
