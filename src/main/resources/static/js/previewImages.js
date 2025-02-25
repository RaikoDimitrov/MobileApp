document.addEventListener("DOMContentLoaded", function () {
    let fileInput = document.getElementById("addImages");
    let removeImagesInput = document.getElementById("removeImagesId");
    let mainImageIndexInput = document.getElementById("mainImageIndex");
    let chooseMainLabel = document.getElementById("chooseMainLabel");
    let imagePreview = document.getElementById("imagePreview");

    if (!fileInput || !removeImagesInput || !mainImageIndexInput || !imagePreview || !chooseMainLabel) {
        console.error("❌ Required DOM elements are missing.");
        return;
    }

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

        newFiles.forEach(file => {
            console.log(`🖼️ File selected: ${file.name} (${file.size} bytes)`);
            if (!imageFiles.some(existingFile => existingFile.name === file.name)) {
                imageFiles.push(file);
            }
        });

        renderImagePreviews();
        console.log("📌 Updated imageFiles array:", imageFiles);
        fileInput.value = ""; // Reset file input after selection
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
            selectMainImage(remainingImages[0]);
        } else {
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

        if (selectedImageUrl) {
            mainImageIndexInput.value = existingImages.indexOf(selectedImageUrl);
        } else {
            let newImageIndex = index - existingImages.length;
            mainImageIndexInput.value = newImageIndex >= 0 ? existingImages.length + newImageIndex : "";
        }

        console.log(`✅ Main image selected: Index ${mainImageIndexInput.value}`);
    }

    /** ✅ Show/Hide "Choose main image" label **/
    function toggleChooseMainLabel() {
        chooseMainLabel.style.display = (imageFiles.length > 0 || existingImages.length > 0) ? "block" : "none";
    }
});
