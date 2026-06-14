/**
 * Controller for video-related API endpoints.
 *
 * This module exposes HTTP handlers for listing available videos,
 * generating thumbnails, starting video processing jobs, and checking
 * the status of running or completed jobs.
 *
 * All endpoints delegate core business logic to the videoService layer.
 */

import * as videoService from '../services/videoService.js';

/**
 * Retrieves a list of available video files from the server directory.
 *
 * This endpoint reads the video storage location and returns all available
 * video filenames in a JSON array format.
 *
 * @route GET /api/videos
 * @returns {200} JSON array of video filenames
 * @returns {500} Error reading video directory
 */
export const listVideos = async (req, res) => {
    try {
        const videos = await videoService.getAvailableVideos();
        return res.status(200).json(videos);
    } catch (err) {
        return res.status(500).json({ error: "Error reading video directory" });
    }
}

/**
 * Generates and returns a thumbnail image for a given video file.
 *
 * The thumbnail is streamed as a JPEG image. If thumbnail generation fails,
 * a 500 error is returned.
 *
 * @route GET /thumbnail/:filename
 * @param {string} req.params.filename - Name of the video file
 * @returns {image/jpeg} JPEG thumbnail image
 * @returns {500} Error generating thumbnail
 */
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

/**
 * Creates a video processing job for the specified video file.
 *
 * The job uses query parameters to define processing behavior:
 * - targetColor: hex RGB color string (RRGGBB format)
 * - threshold: integer color distance threshold
 *
 * If parameters are missing, a 400 error is returned.
 * On success, a jobId is returned with HTTP 202 (Accepted).
 *
 * @route POST /process/:filename
 * @param {string} req.params.filename - Video filename
 * @query {string} targetColor - Hex color in RRGGBB format
 * @query {number} threshold - Color matching threshold
 * @returns {400} Missing required query parameters
 * @returns {202} JSON object containing jobId
 * @returns {500} Error starting processing job
 */
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

/**
 * Retrieves the current status of a video processing job.
 *
 * Returns job metadata including processing state, results, or errors.
 * If the job ID does not exist, a 404 error is returned.
 *
 * @route GET /process/:jobId/status
 * @param {string} req.params.jobId - Unique job identifier
 * @returns {404} Job ID not found
 * @returns {200} Job status object
 * @returns {500} Error fetching job status
 */
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

export const downloadCSV = (req, res) => {
    return res.status(200).json({"test":"test"});
}