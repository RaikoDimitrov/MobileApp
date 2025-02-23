document.addEventListener("DOMContentLoaded", function () {
    let currentIndex = 0;
    const mainImage = document.getElementById('mainImage');
    const thumbnails = document.querySelectorAll('.vehicle-thumbnail');
    const prevButton = document.querySelector('.vehicle-arrow-left');
    const nextButton = document.querySelector('.vehicle-arrow-right');

    // Get image URLs directly from Thymeleaf-rendered elements
    const imageUrls = [...thumbnails].map(img => img.getAttribute("data-src"));

    function updateActiveThumbnail() {
        thumbnails.forEach((thumb, index) => {
            thumb.classList.toggle('active', index === currentIndex);
        });
    }

    function changeImage(element) {
        const url = element.getAttribute("data-src");
        if (url) {
            mainImage.src = url;
            currentIndex = imageUrls.indexOf(url); // Update index
            updateActiveThumbnail();
        }
    }

    function prevImage() {
        if (imageUrls.length === 0) return; // Prevent errors if array is empty
        currentIndex = (currentIndex - 1 + imageUrls.length) % imageUrls.length;
        mainImage.src = imageUrls[currentIndex];
        updateActiveThumbnail();
    }

    function nextImage() {
        if (imageUrls.length === 0) return;
        currentIndex = (currentIndex + 1) % imageUrls.length;
        mainImage.src = imageUrls[currentIndex];
        updateActiveThumbnail();
    }

    // Attach event listeners
    thumbnails.forEach(thumb => {
        thumb.addEventListener("click", function () {
            changeImage(this);
        });
    });

    if (prevButton) prevButton.addEventListener("click", prevImage);
    if (nextButton) nextButton.addEventListener("click", nextImage);

    // Initialize first active thumbnail
    updateActiveThumbnail();
});
