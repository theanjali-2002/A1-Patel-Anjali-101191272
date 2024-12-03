const commandInput = document.getElementById("commandInput");
const outputDiv = document.getElementById("output");

// Automatically initialize the game when the page loads
window.onload = function () {
  fetch("/api/game/initialize")
    .then((response) => response.text())
    .then((data) => appendOutput(data)) // Display the initial game message
    .catch((error) => appendOutput(`Error: ${error.message}`));

  // Start fetching output from the backend periodically
  setInterval(fetchOutput, 1000); // Fetch output every 1 second
};

// Handle user input on pressing "Enter"
commandInput.addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();

        const command = commandInput.value.trim();
        commandInput.value = "";

        appendOutput(`> ${command}`, true); // Display the input

        // Send the command to the backend
        fetch("/api/game/input", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(command),
        }).then(() => fetchOutput()); // Fetch updated output
    }
});


// Fetch output from the backend
function fetchOutput() {
  fetch("/api/game/output")
    .then((response) => response.text())
    .then((data) => {
      if (data) appendOutput(data); // Only append if there's new output
    })
    .catch((error) => appendOutput(`Error: ${error.message}`));
}

// Function to append text to the output area
// Function to append text to the output area
function appendOutput(text, isInput = false) {
  const outputDiv = document.getElementById("output");
  const lines = text.split("\n"); // Split text by newline characters
  lines.forEach((line) => {
    const newLine = document.createElement("div");
    newLine.textContent = line;
    if (isInput) {
      newLine.style.color = "#ff3333"; // Red for user input
    }
    outputDiv.appendChild(newLine);
  });
  outputDiv.scrollTop = outputDiv.scrollHeight;
}


