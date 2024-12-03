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


// Function to update the progress bar
function updateProgressBar(message) {
    const progressBar = document.getElementById("progress-bar");
    progressBar.innerText = message;
}

// Function to update player stats
function updatePlayerStats(playerNumber, shields, hand) {
    const shieldElement = document.getElementById(`player${playerNumber}-shields`);
    const handElement = document.getElementById(`player${playerNumber}-hand`);
    shieldElement.innerText = shields;
    handElement.innerText = hand;
}

// Function to update current info
function updateCurrentInfo(currentPlayer, hotSeatPlayer, cardDrawn, sponsor) {
    document.querySelector(".current-info").innerHTML = `
        <p>Hot Seat Player (drew Card): ${hotSeatPlayer}</p>
        <p>Current Player (in general): ${currentPlayer}</p>
        <p>Card Drawn: ${cardDrawn}</p>
        <p>Sponsor (if any): ${sponsor}</p>
    `;
}

function fetchGameState() {
    fetch("/api/game/state") // Call the backend endpoint
        .then(response => response.json())
        .then(data => {
            // Use the JSON data to update the UI
            updateProgressBar(data.progressMessage);
            updateCurrentInfo(data.currentPlayer, data.hotSeatPlayer, data.cardDrawn, data.sponsor);

            // Update player stats
            data.players.forEach((player, index) => {
                updatePlayerStats(index + 1, player.shields, player.hand);
            });
        })
        .catch(error => console.error("Error fetching game state:", error));
}

// Periodically fetch the game state every second
setInterval(fetchGameState, 1000);

