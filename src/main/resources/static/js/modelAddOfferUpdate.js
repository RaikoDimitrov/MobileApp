document.addEventListener("DOMContentLoaded", function() {
    document.getElementById('brandName').addEventListener('change', function() {
        var brandName = this.value;  // Get the selected brand name
        console.log("Brand selected:", brandName);  // Log to ensure it's triggered

        // If a brand is selected
        if (brandName) {
            console.log("Making fetch request...");
            fetch(`/offers/models/${brandName}`)
                .then(response => response.json())
                .then(models => {
                    console.log("Models received:", models);
                    var modelSelect = document.getElementById('modelName');
                    modelSelect.innerHTML = '';  // Clear the current model options

                    var defaultOption = document.createElement('option');
                    defaultOption.value = "";
                    defaultOption.textContent = "- Select a model -";
                    modelSelect.appendChild(defaultOption);

                    models.forEach(function(model) {
                        var option = document.createElement('option');
                        option.value = model;
                        option.textContent = model;
                        modelSelect.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Error fetching models:', error);
                });
        } else {
            document.getElementById('modelName').innerHTML = '<option value="">- Select a model -</option>';
        }
    });
});
