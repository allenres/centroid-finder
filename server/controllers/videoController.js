import * as videoService from '../services/videoService.js';

// GET /api/videos
export const listVideos = async (req, res) => {
    try {
        const videos = await videoService.getAvailableVideos();
        return res.status(200).json(videos);
    } catch (err) {
        return res.status(500).json({ error: "Error reading video directory" });
    }
}

// GET /thumbnail/:filename
export const getThumbnail = (req, res) => {
    const { filename } = req.params;

    videoService.getThumbnailStream(filename, (err, imageBuffer) => {
        if (err) {
            return res.status(500).json({ error: "Error generating thumbnail" });
        }
        res.set('Content-Type', 'image/jpeg');
        return res.send(imageBuffer);
    });
}

// POST /process/:filename
export const createJob = (req, res) => {
    const { filename } = req.params;
    const { targetColor, threshold } = req.query;

    if (!targetColor || !threshold) {
        return res.status(400).json({
            error: "Missing targetColor or threshold query parameter."
        });
    }

    try {
        const jobId = videoService.startProcessingJob(filename, targetColor, threshold);
        return res.status(202).json({ jobId });
    } catch (err) {
        return res.status(500).json({ error: "Error starting job" });
    }
}

// GET /process/:jobId/status
export const checkJobStatus = (req, res) => {
    const { jobId } = req.params;
    try {
        const statusData = videoService.getJobStatus(jobId);
        if (!statusData) {
            return res.status(404).json({ error: "Job ID not found" });
        }
        return res.status(200).json(statusData);
    } catch (err) {
        return res.status(500).json({ error: "Error fetching job status" })
    }
}