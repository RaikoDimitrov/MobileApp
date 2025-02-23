function previewImages() {
    let input = document.getElementById("images");
    let imagePreview = document.getElementById("imagePreview");

    // Clear previous previews
    imagePreview.innerHTML = "";

    // Loop through selected files
    for (let i = 0; i < input.files.length; i++) {
        let file = input.files[i];
        let reader = new FileReader();

        reader.onload = function (e) {
            // Create new image container
            let imageContainer = document.createElement("div");
            imageContainer.classList.add("preview-image-container");
            imageContainer.setAttribute("data-index", i); // Set the index for each image

            // Create remove button
            let removeBtn = document.createElement("button");
            removeBtn.classList.add("remove-btn");
            removeBtn.innerHTML = "−";

            // Add event listener for removing images
            removeBtn.addEventListener("click", function (event) {
                event.stopPropagation();  // Prevent event propagation to container click
                removeImage(event); // Call the remove image function
            });

            // Create image element
            let img = document.createElement("img");
            img.src = e.target.result;
            img.classList.add("preview-img");

            // Add event listener to select the main image (on container click)
            imageContainer.addEventListener("click", function () {
                selectMainImage(imageContainer); // Pass the container to select it as the main image
            });

            // Append elements
            imageContainer.appendChild(removeBtn);
            imageContainer.appendChild(img);
            imagePreview.appendChild(imageContainer);
        };

        reader.readAsDataURL(file);
    }
}

// Handle removing images
function removeImage(event) {
    // Prevent bubbling up to image container (which might trigger selection)
    event.stopPropagation();

    // Get the image container and remove it
    const imageContainer = event.target.closest('.preview-image-container');
    imageContainer.remove();

    // Optionally update the hidden field for removed images
    // If necessary, you can maintain removed images tracking here
}

// Select main image
function selectMainImage(element) {
    // Remove 'selected' class from all image containers
    document.querySelectorAll(".preview-image-container").forEach(function (imgContainer) {
        imgContainer.classList.remove("selected");
    });

    // Add 'selected' class to the clicked image container
    element.classList.add("selected");

    // Update the hidden input field with the selected image index
    var index = element.getAttribute("data-index");
    document.getElementById("mainImageIndex").value = index;
}
