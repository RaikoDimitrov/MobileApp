document.addEventListener("DOMContentLoaded", function () {
    let imageFiles = []; // Stores new images
    let existingImages = []; // Stores existing images in offer-update
    const fileInput = document.getElementById("images");
    const imagePreview = document.getElementById("imagePreview");
    const mainImageIndexInput = document.getElementById("mainImageIndex"); // ✅ Corrected input reference
    const chooseMainLabel = document.getElementById("chooseMainLabel");
    const removeImagesInput = document.getElementById("removeImagesId"); // Hidden field for removed images

    // ✅ Load existing images (offer-update only)
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

    function previewImages() {
        if (!fileInput.files.length) return;

        // ✅ Preserve previously selected images & append new ones
        imageFiles = [...imageFiles, ...Array.from(fileInput.files)];

        renderImagePreviews();
    }

    function renderImagePreviews() {
        imagePreview.innerHTML = ""; // Clear existing preview area

        // ✅ Render existing images first (offer-update)
        existingImages.forEach((url, index) => {
            renderImage(url, index, true);
        });

        // ✅ Render new images (offer-add & offer-update)
        imageFiles.forEach((file, index) => {
            let reader = new FileReader();
            reader.onload = function (e) {
                renderImage(e.target.result, existingImages.length + index, false);
            };
            reader.readAsDataURL(file);
        });

        toggleChooseMainLabel();
    }

    function renderImage(src, index, isExisting) {
        let imageContainer = document.createElement("div");
        imageContainer.classList.add("preview-image-container");
        imageContainer.setAttribute("data-index", index);

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

        if (index === 0 && !document.querySelector(".preview-img.selected")) {
            selectMainImage(imageContainer);
        }
    }

    function removeNewImage(index) {
        imageFiles.splice(index - existingImages.length, 1);
        renderImagePreviews();
    }

    function removeExistingImage(imageUrl, container) {
        existingImages = existingImages.filter(img => img !== imageUrl);

        // Track removed images for backend processing
        if (!removeImagesInput.value.includes(imageUrl)) {
            removeImagesInput.value += removeImagesInput.value ? `,${imageUrl}` : imageUrl;
         }

        container.remove();
        updateMainImageAfterRemoval();
    }

    function updateMainImageAfterRemoval() {
        let remainingImages = document.querySelectorAll(".preview-image-container");
        if (remainingImages.length > 0) {
            selectMainImage(remainingImages[0]);
        } else {
            mainImageIndexInput.value = ""; // ✅ Correctly updating mainImageIndex input
        }
        toggleChooseMainLabel();
    }

    function selectMainImage(element) {
        document.querySelectorAll(".preview-img").forEach((img) => {
            img.classList.remove("selected");
        });

        let selectedImg = element.querySelector(".preview-img");
        if (selectedImg) {
            selectedImg.classList.add("selected");
        }

        let index = [...document.querySelectorAll(".preview-image-container")].indexOf(element);
        let selectedImageUrl = element.getAttribute("data-image-url") || selectedImg.src; // Get from existing images or new ones

        mainImageIndexInput.value = index; // ✅ Corrected input reference
    }

    function toggleChooseMainLabel() {
        chooseMainLabel.style.display = (imageFiles.length > 0 || existingImages.length > 0) ? "block" : "none";
    }

    fileInput.addEventListener("change", previewImages);
});
