import express from "express";
import dotenv from "dotenv";
import path from "path";
import fs from "fs";
import { fileURLToPath } from "url";
import videoRoutes from "./routes/videoRoutes.js";

// Load environment variables
dotenv.config();

//configure Express.js app
const app = express();
const PORT = process.env.PORT || 3000;

// Ensure our jobs status tracking directory exists
if (!fs.existsSync(process.env.STATUS_DIR)) {
  fs.mkdirSync(process.env.STATUS_DIR, { recursive: true });
}

// Parse JSON bodies if clients send json data
app.use(express.json());

//static directories
app.use("/videos", express.static(process.env.VIDEOS_DIR));
app.use("/results", express.static(process.env.RESULTS_DIR));

//routers
app.use(videoRoutes);

//start server
app.listen(PORT, () => {
  console.log(`Salamander API server running on http://localhost:${PORT}`);
});
