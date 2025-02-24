document.addEventListener("DOMContentLoaded", function () {
    let fileInput = document.getElementById("addImages");
    let uploadForm = document.getElementById("uploadForm");
    let removeImagesInput = document.getElementById("removeImagesId");
    let mainImageIndexInput = document.getElementById("mainImageIndex");
    let chooseMainLabel = document.getElementById("chooseMainLabel");
    let imagePreview = document.getElementById("imagePreview");

    // Ensure the form elements exist before proceeding
    if (!fileInput || !uploadForm || !removeImagesInput || !mainImageIndexInput || !imagePreview || !chooseMainLabel) {
        console.error("Required DOM elements are missing.");
        return;
    }

    let imageFiles = []; // Stores new image files
    let existingImages = []; // Stores existing images (offer-update)

    // ✅ Load existing images (for offer-update)
    document.querySelectorAll(".preview-image-container").forEach((container) => {
        let imageUrl = container.getAttribute("data-image-url");
        if (imageUrl) existingImages.push(imageUrl);

        container.addEventListener("click", function (event) {
            if (!event.target.classList.contains("remove-btn")) {
                selectMainImage(container);
            }
        });

        container.querySelector(".remove-btn")?.addEventListener("click", function (event) {
            event.stopPropagation();
            removeExistingImage(imageUrl, container);
        });
    });

    // Handle file input change
    fileInput.addEventListener("change", function () {
        let newFiles = Array.from(fileInput.files);
        console.log("Selected files:", newFiles); // Log to check if the files are selected

        newFiles.forEach(file => {
            // Prevent duplicates
            if (!imageFiles.some(existingFile => existingFile.name === file.name)) {
                imageFiles.push(file);
            }
        });

        // Log the length of imageFiles after selection to verify files are added
        console.log("imageFiles length after file selection:", imageFiles.length);

        renderImagePreviews();  // Call function to render image previews after selection

        // Reset the file input value to allow re-upload of the same file if needed
        fileInput.value = "";
    });

    // Render image previews (both new and existing)
    function renderImagePreviews() {
        imagePreview.innerHTML = ""; // Clear previous previews

        // ✅ Render existing images first
        existingImages.forEach((url, index) => renderImage(url, index, true));

        // ✅ Render new images
        imageFiles.forEach((file, index) => {
            let reader = new FileReader();
            reader.onload = (e) => renderImage(e.target.result, existingImages.length + index, false);
            reader.readAsDataURL(file);
        });

        toggleChooseMainLabel();
    }

   // Function to render each image (updated for better index handling)
   function renderImage(src, index, isExisting) {
       let imageContainer = document.createElement("div");
       imageContainer.classList.add("preview-image-container");
       imageContainer.setAttribute("data-index", index);

       if (isExisting) {
           imageContainer.setAttribute("data-image-url", src);
       }

       let removeBtn = document.createElement("button");
       removeBtn.classList.add("remove-btn");
       removeBtn.innerHTML = "−";

       let img = document.createElement("img");
       img.src = src;
       img.classList.add("preview-img");

       imageContainer.addEventListener("click", function (event) {
           if (!event.target.classList.contains("remove-btn")) {
               selectMainImage(imageContainer);
           }
       });

       removeBtn.addEventListener("click", function (event) {
           event.stopPropagation();
           if (isExisting) {
               removeExistingImage(src, imageContainer);
           } else {
               removeNewImage(index);
           }
       });

       imageContainer.appendChild(removeBtn);
       imageContainer.appendChild(img);
       imagePreview.appendChild(imageContainer);

       // Set the first image as the main image if none selected
       if (!document.querySelector(".preview-img.selected")) {
           selectMainImage(imageContainer);
       }
   }


    // Function to remove new image from the preview
    function removeNewImage(index) {
        let actualIndex = index - existingImages.length;
        if (actualIndex >= 0 && actualIndex < imageFiles.length) {
            imageFiles.splice(actualIndex, 1);
        }
        renderImagePreviews();
    }

    // Function to remove existing image
    function removeExistingImage(imageUrl, container) {
        existingImages = existingImages.filter(img => img !== imageUrl);

        // Append to hidden input field the removed image URL for the backend
        if (!removeImagesInput.value.includes(imageUrl)) {
            removeImagesInput.value += removeImagesInput.value ? `,${imageUrl}` : imageUrl;
        }

        container.remove();
        updateMainImageAfterRemoval();
    }

    // Update main image index after removing an image
    function updateMainImageAfterRemoval() {
        let remainingImages = document.querySelectorAll(".preview-image-container");
        if (remainingImages.length > 0) {
            selectMainImage(remainingImages[0]);
        } else {
            mainImageIndexInput.value = ""; // Reset main image index if no images
        }
        toggleChooseMainLabel();
    }

  // Fix for selectMainImage function to correctly update index
  function selectMainImage(element) {
      // Remove selected class from all images
      document.querySelectorAll(".preview-img").forEach((img) => {
          img.classList.remove("selected");
      });

      let selectedImg = element.querySelector(".preview-img");
      if (selectedImg) {
          selectedImg.classList.add("selected");
      }

      let index = [...document.querySelectorAll(".preview-image-container")].indexOf(element);
      let selectedImageUrl = element.getAttribute("data-image-url");

      // Set main image index based on whether it's an existing image or a new one
      if (selectedImageUrl) {
          // This is an existing image
          mainImageIndexInput.value = existingImages.indexOf(selectedImageUrl);
      } else {
          // This is a new image, adjust the index accordingly
          let newImageIndex = index - existingImages.length;
          mainImageIndexInput.value = newImageIndex >= 0 ? existingImages.length + newImageIndex : "";
      }
  }

    // Toggle visibility of the "Choose main image" label
    function toggleChooseMainLabel() {
        chooseMainLabel.style.display = (imageFiles.length > 0 || existingImages.length > 0) ? "block" : "none";
    }

    // ✅ Handle form submission with FormData (no AJAX)
    uploadForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent the default form submission

        let formData = new FormData(this);

        // Append the selected files to the formData with the name 'images[]'
        imageFiles.forEach((file, index) => {
            formData.append("images[]", file);  // Append each file under 'images[]'
        });

        // Also append any other necessary data (like removed images, etc.)
        formData.append("removeImagesId", removeImagesInput.value);
        formData.append("mainImageIndex", mainImageIndexInput.value);

        // Perform the form submission
        fetch(uploadForm.action, {
            method: "POST",
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Upload failed");
            }
            return response.text(); // Expecting an HTML response (text)
        })
        .then(html => {
            // If the response is an HTML page, you can replace the current page with the response
            document.documentElement.innerHTML = html; // Replaces the whole document content
            // Or alternatively, you can redirect to a new URL
            // window.location.href = response.url;
        })
        .catch(error => {
            console.error("Error uploading images", error);
            // Optionally display an error message to the user
        });
    });
});
