import { Router } from 'express';
import * as videoController from '../controllers/videoController.js';

const router = Router();

router.get('/api/videos', videoController.listVideos); 
router.get('/thumbnail/:filename', videoController.getThumbnail); 
router.post('/process/:filename', videoController.createJob); 
router.get('/process/:jobId/status', videoController.checkJobStatus);

export default router;