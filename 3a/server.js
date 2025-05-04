const express = require('express');
const app = express();
const PORT = 3000;

//  static files from the public directory
app.use(express.static('public'));

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`); //used backticking for easier port initialisation
});

