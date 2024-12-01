// Get references to DOM elements
const commandInput = document.getElementById("commandInput");
const outputDiv = document.getElementById("output");

// Handle user input on pressing "Enter"
commandInput.addEventListener("keypress", function (event) {
  if (event.key === "Enter") {
    // Prevent form submission
    event.preventDefault();

    // Get the entered command and clear the input field
    const command = commandInput.value.trim();
    commandInput.value = "";

    // Display the command in the output
    appendOutput(`> ${command}`);

    // Process the command
    processCommand(command);
  }
});

// Function to process commands
function processCommand(command) {
  switch (command.toLowerCase()) {
    case "hello":
      appendOutput("Hello!");
      break;
    case "clear":
      clearOutput();
      break;
    default:
      appendOutput(`Unknown command: ${command}`);
  }
}

// Append text to the output area
function appendOutput(text) {
  const newLine = document.createElement("div");
  newLine.textContent = text;
  outputDiv.appendChild(newLine);
  outputDiv.scrollTop = outputDiv.scrollHeight;
}

// Clear the output area
function clearOutput() {
  outputDiv.innerHTML = "";
}
