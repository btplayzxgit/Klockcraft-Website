function handleSelectClick() {
    const selectElement = document.getElementById("versions");
    selectElement.addEventListener('change', (event) => {
        // event.preventDefault();
        // window.open('clients/Klockcraft-Classic/index.html')
        if (event.target.value === "0.0.23a_01") {
            window.open('clients/Klockcraft-Classic/index.html')
        }
        if (event.target.value === "0.30") {
            window.open('clients/Klockcraft-Java-Edition-0.30/index.html')
        }
    });
}

window.onload = () => {
    handleSelectClick()
}

