document.addEventListener("DOMContentLoaded", function () {
    let fileInput = document.getElementById("addImages") || document.getElementById("updateImages");
    let removeImagesInput = document.getElementById("removeImagesId");
    let mainImageIndexInput = document.getElementById("mainImageIndex");
    let chooseMainLabel = document.getElementById("chooseMainLabel");
    let imagePreview = document.getElementById("imagePreview");
    let form = document.querySelector("form"); // Select the first form on the page

    // ✅ First, check if all required elements exist
/*    if (!fileInput || !removeImagesInput || !mainImageIndexInput || !imagePreview || !chooseMainLabel) {
        console.error("❌ Required DOM elements are missing.");
        return;
    }*/

    let imageFiles = []; // Stores new image files
    let existingImages = []; // Stores existing images (for offer updates)
    let removedImages = [];

    console.log("✅ DOM fully loaded. Waiting for image selection...");

    /** ✅ Load existing images (for updating offers) **/
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

    /** ✅ Handle file input change **/
    fileInput.addEventListener("change", function () {
        let newFiles = Array.from(fileInput.files);
        console.log(`📸 Selected ${newFiles.length} new images.`);

        let dataTransfer = new DataTransfer();
        imageFiles.forEach(file => dataTransfer.items.add(file));

        newFiles.forEach(file => {
            if (!imageFiles.some(existingFile => existingFile.name === file.name)) {
                imageFiles.push(file);
                dataTransfer.items.add(file);
                console.log(`🖼️ File selected: ${file.name} (${file.size} bytes)`);
            }
        });

        fileInput.files = dataTransfer.files;

        renderImagePreviews();
        updateMainImageSelectionAfterUpload();
        console.log("📌 Updated imageFiles array:", imageFiles);
        //fileInput.value = ""; // Reset file input after selection
    });

    /** ✅ Render images (both existing and new) **/
    function renderImagePreviews() {
        imagePreview.innerHTML = "";
        console.log("🔄 Rendering image previews...");

        // ✅ Render existing images
        existingImages.forEach((url, index) => renderImage(url, index, true));

        // ✅ Render new images
        imageFiles.forEach((file, index) => {
            let reader = new FileReader();
            reader.onload = (e) => renderImage(e.target.result, existingImages.length + index, false);
            reader.readAsDataURL(file);
        });

        toggleChooseMainLabel();
    }

    /** ✅ Function to render each image **/
    function renderImage(src, index, isExisting) {
        console.log(`🖼️ Rendering image at index ${index}: ${isExisting ? "Existing" : "New"}`);

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

        if (!document.querySelector(".preview-img.selected")) {
            selectMainImage(imageContainer);
        }
    }

    /** ✅ Remove new image **/
    function removeNewImage(index) {
        let actualIndex = index - existingImages.length;
        if (actualIndex >= 0 && actualIndex < imageFiles.length) {
            console.log(`🗑️ Removing new image at index ${actualIndex}:`, imageFiles[actualIndex].name);
            imageFiles.splice(actualIndex, 1);
        }

        let dataTransfer = new DataTransfer();
        imageFiles.forEach(file => dataTransfer.items.add(file));
        fileInput.files = dataTransfer.files; // ✅ Update input files

        console.log("📌 Updated file input after removal:", fileInput.files);

        renderImagePreviews();
    }

    /** ✅ Remove existing image **/
    function removeExistingImage(imageUrl, container) {
        console.log(`🗑️ Removing existing image: ${imageUrl}`);

        existingImages = existingImages.filter(img => img !== imageUrl);

        // ✅ Add to removed images array only if not already present
        if (!removedImages.includes(imageUrl)) {
            removedImages.push(imageUrl);
        }

        // ✅ Convert array to a comma-separated string for backend
        removeImagesInput.value = removedImages.join(",");

        container.remove();
        updateMainImageAfterRemoval();
    }

    /** ✅ Update main image after removal **/
    function updateMainImageAfterRemoval() {
        let remainingImages = document.querySelectorAll(".preview-image-container");
        if (remainingImages.length > 0) {
            // Ensure the first remaining image is selected as the main image
            selectMainImage(remainingImages[0]);
        } else {
            // No images left, clear main image selection
            mainImageIndexInput.value = "";
        }
        toggleChooseMainLabel();
    }

    /** ✅ Select main image **/
    function selectMainImage(element) {
        document.querySelectorAll(".preview-img").forEach((img) => img.classList.remove("selected"));

        let selectedImg = element.querySelector(".preview-img");
        if (selectedImg) {
            selectedImg.classList.add("selected");
        }

        let index = [...document.querySelectorAll(".preview-image-container")].indexOf(element);
        let selectedImageUrl = element.getAttribute("data-image-url");

        // If it's an existing image, we use the index from existingImages
        if (selectedImageUrl) {
            mainImageIndexInput.value = existingImages.indexOf(selectedImageUrl);
        } else {
            // If it's a new image, adjust index based on how many existing images are left
            let newImageIndex = index - existingImages.length;
            mainImageIndexInput.value = newImageIndex >= 0 ? existingImages.length + newImageIndex : "";
        }

        console.log(`✅ Main image selected: Index ${mainImageIndexInput.value}`);
    }

    /** ✅ Select main image from the first available image after upload or removal **/
    function updateMainImageSelectionAfterUpload() {
        let allImageContainers = document.querySelectorAll(".preview-image-container");

        // If no images exist, clear the main image input
        if (allImageContainers.length === 0) {
            mainImageIndexInput.value = "";
        } else {
            // Automatically select the first image after upload (or after removing images)
            selectMainImage(allImageContainers[0]);
        }
    }

    /** ✅ Show/Hide "Choose main image" label **/
    function toggleChooseMainLabel() {
        chooseMainLabel.style.display = (imageFiles.length > 0 || existingImages.length > 0) ? "block" : "none";
    }

    /** ✅ Add all images (existing + new) to the form before submitting **/
    form.addEventListener("submit", function (event) {
            event.preventDefault();

            // Combine all images (existing + new)
            let allImages = [...imageFiles];
            console.log("📤 New images before submit:", allImages);
            console.log("🌐 Existing images (URLs):", existingImages);

            // Check if we have images to submit
            if (allImages.length === 0 && existingImages.length === 0) {
                console.warn("⚠️ No images selected for upload!");
            } else {
                console.log("📤 Submitting files:");
                allImages.forEach(file => {
                    console.log(` - ${file.name || file} (${file.size || ''} bytes)`);
                });
            }

            // Prepare FormData
            let formData = new FormData(form);

            // Append all image files to the FormData object
            allImages.forEach(image => {
                formData.append("images[]", image);  // Ensure all images are added to the FormData
            });

            // Add existing images as urls separately
            if (existingImages.length > 0) {
                formData.append("existingImages", existingImages.join(","));
            }

            // Add the removed images (if any)
            if (removedImages.length > 0) {
                formData.append("removedImages", removedImages.join(","));
            }

            // Add main image index (if selected)
            if (mainImageIndexInput.value) {
                formData.append("mainImageIndex", mainImageIndexInput.value);
            }

            // Debugging
            console.log("📤 Submitting form with:");
            console.log("✅ New images:", imageFiles);
            console.log("✅ Existing images:", existingImages);
            console.log("✅ Removed images:", removedImages);
            console.log("✅ Main image index:", mainImageIndexInput.value);

        // Submit form via AJAX or default submit if needed
        console.log("Submitting form...");

        // You can use `fetch` to submit the form data if needed, or let the browser handle the submit.
        form.submit();
    });


});