document.addEventListener("DOMContentLoaded", function () {
    let fileInput = document.getElementById("addImages") || document.getElementById("updateImages");

    if (!fileInput) return; // Prevent errors if the form is missing

    let imageFiles = []; // Stores new images
    let existingImages = []; // Stores existing images in offer-update
    const imagePreview = document.getElementById("imagePreview");
    const mainImageIndexInput = document.getElementById("mainImageIndex");
    const chooseMainLabel = document.getElementById("chooseMainLabel");
    const removeImagesInput = document.getElementById("removeImagesId");

    // ✅ Load existing images (for offer-update)
    document.querySelectorAll(".preview-image-container").forEach((container) => {
        let imageUrl = container.getAttribute("data-image-url");
        if (imageUrl) existingImages.push(imageUrl);

        // Make images selectable
        container.addEventListener("click", function (event) {
            if (!event.target.classList.contains("remove-btn")) {
                selectMainImage(container);
            }
        });

        // Allow removal of existing images
        container.querySelector(".remove-btn")?.addEventListener("click", function (event) {
            event.stopPropagation();
            removeExistingImage(imageUrl, container);
        });
    });

    fileInput.addEventListener("change", previewImages);

    function previewImages() {
        if (!fileInput.files.length) return;

        imageFiles = [...imageFiles, ...Array.from(fileInput.files)];
        renderImagePreviews();
    }

    function renderImagePreviews() {
        imagePreview.innerHTML = "";

        // ✅ Render existing images first (offer-update)
        existingImages.forEach((url, index) => renderImage(url, index, true));

        // ✅ Render new images (offer-add & offer-update)
        imageFiles.forEach((file, index) => {
            let reader = new FileReader();
            reader.onload = (e) => renderImage(e.target.result, existingImages.length + index, false);
            reader.readAsDataURL(file);
        });

        toggleChooseMainLabel();
    }

    function updateExistingImagesInput() {
        let existingImageUrls = [...document.querySelectorAll(".preview-image-container[data-image-url]")]
            .map(container => container.getAttribute("data-image-url"));

        document.getElementById("existingImagesInput").value = existingImageUrls.join(",");
    }


    function renderImage(src, index, isExisting) {
        let imageContainer = document.createElement("div");
        imageContainer.classList.add("preview-image-container");
        imageContainer.setAttribute("data-index", index);

        // ✅ Set data-image-url for existing images
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

        // ✅ Auto-select first image as main
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

        // ✅ Track removed images for backend processing
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
            mainImageIndexInput.value = "";
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
        let selectedImageUrl = element.getAttribute("data-image-url");

        if (selectedImageUrl) {
            // ✅ Existing image (URL-based)
            mainImageIndexInput.value = existingImages.indexOf(selectedImageUrl);
        } else {
            // ✅ New image (File-based)
            let newImageIndex = [...document.querySelectorAll(".preview-image-container")].indexOf(element) - existingImages.length;
            mainImageIndexInput.value = existingImages.length + newImageIndex;
        }
    }

    function toggleChooseMainLabel() {
        chooseMainLabel.style.display = (imageFiles.length > 0 || existingImages.length > 0) ? "block" : "none";
    }
});
