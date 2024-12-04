const commandInput = document.getElementById("commandInput");
const outputDiv = document.getElementById("output");

// Automatically initialize the game when the page loads
window.onload = function () {
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get("rigged") === "true") {
    rigGame();
  } else {
    fetch("/api/game/start")
      .then((response) => response.text())
      .then((data) => appendOutput(data))
      .catch((error) => appendOutput(`Error: ${error.message}`));
  }

  setInterval(fetchOutput, 500);
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
function updateCurrentInfo(hotSeatPlayer, cardDrawn, sponsor) {
    document.querySelector(".current-info").innerHTML = `
        <p>Hot Seat Player (drew Card): ${hotSeatPlayer}</p>
        <p>Card Drawn: ${cardDrawn}</p>
        <p>Sponsor (if any): ${sponsor}</p>
    `;
}

function updatePlayerCards(playerNumber, cards) {
    const cardsElement = document.getElementById(`player${playerNumber}-cards`);
    if (cards.length > 0) {
        cardsElement.innerText = cards.join(", "); // Display card names separated by commas
    } else {
        cardsElement.innerText = "No cards yet"; // Fallback if hand is empty
    }
}

function fetchGameState() {
    fetch("/api/game/state") // Call the backend endpoint
        .then(response => response.json())
        .then(data => {
            // Use the JSON data to update the UI
            updateProgressBar(data.progressMessage);
            updateCurrentInfo(data.hotSeatPlayer, data.cardDrawn, data.sponsor);

            // Update player stats
            data.players.forEach((player, index) => {
                const playerNumber = index + 1;
                updatePlayerStats(playerNumber, player.shields, player.hand);
                updatePlayerCards(playerNumber, player.cards);
            });
        })
        .catch(error => console.error("Error fetching game state:", error));
}

// Periodically fetch the game state every second
setInterval(fetchGameState, 500);

function rigGame() {
    fetch("/api/game/rig", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            adventureDeck: [{ cardName: "A1", type: "Adventure", value: 1, category: "Weapon" }],
            eventDeck: [{ cardName: "E1", type: "Event", value: 1, category: "Quest" }],
            hands: {
                Player1: [{ cardName: "H1", type: "Hand", value: 1, category: "Weapon" }],
                Player2: [{ cardName: "H2", type: "Hand", value: 2, category: "Weapon" }]
            }
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.text();
    })
    .then(data => console.log(data))
    .catch(error => console.error(error));
}
